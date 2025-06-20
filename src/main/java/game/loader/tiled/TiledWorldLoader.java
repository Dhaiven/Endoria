package game.loader.tiled;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.BoundingShape;
import game.loader.WorldLoader;
import game.object.*;
import game.util.Utils;
import game.world.World;
import javafx.geometry.Point2D;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.*;
import java.util.List;

import static game.loader.tiled.TiledLoaderUtils.*;

public class TiledWorldLoader implements WorldLoader {

    private final Map<String, Map<Integer, SpawnData>> alreadyLoadedTiles = new HashMap<>();

    @Override
    public String getExtension() {
        return "tmx";
    }

    // Load les fichier en .tmx
    public World load(File file) {
        try {
            Document map = createDocument(file);

            Element mapElement = firstElement(map.getElementsByTagName("map"));
            if (mapElement == null) {
                throw new RuntimeException("No map element found");
            }

            int tileWidth = Integer.parseInt(mapElement.getAttribute("tilewidth"));
            int tileHeight = Integer.parseInt(mapElement.getAttribute("tileheight"));

            Vector2i screenSize = Utils.TEXTURE_SIZE;
            Vector2 roomScale = new Vector2(
                    (double) screenSize.x() / tileWidth,
                    (double) screenSize.y() / tileHeight
            );

            // On récupère tous les tilesets
            Map<String, Integer> needFiles = new HashMap<>();
            for (Element tileSetElement : getAllElementNode(map.getElementsByTagName("tileset"))) {
                // Extraction du chemin du fichier .tsx depuis l'attribut source
                String tsxFilePath = tileSetElement.getAttribute("source");
                File tsxFile = resolveAbsolutePath(file, tsxFilePath);

                int firstId = Integer.parseInt(tileSetElement.getAttribute("firstgid"));
                needFiles.put(tsxFile.getPath(), firstId);

                System.out.println("Loading tileset: " + tsxFile.getPath());

                // Charger les textures du fichier .tsx référencé
                TiledTileLoader tileLoader = new TiledTileLoader(roomScale);
                this.alreadyLoadedTiles.put(tsxFile.getPath(), tileLoader.load(tsxFile));
            }

            Map<Integer, SpawnData> tiles = new HashMap<>();
            for (Map.Entry<String, Integer> entry : needFiles.entrySet()) {
                for (var tileEntry : alreadyLoadedTiles.get(entry.getKey()).entrySet()) {
                    tiles.put(tileEntry.getKey() + entry.getValue(), tileEntry.getValue());
                }
            }

            Vector2 spawnPoint = null;
            for (Element objectGroup : getAllElementNode(map.getElementsByTagName("objectgroup"))) {
                for (Element object : getAllElementNode(objectGroup.getElementsByTagName("object"))) {
                    if (object.getAttribute("name").equals("spawnpoint")) {
                        Rectangle2D spawnPointShape = loadBaseShape(object, roomScale).getBounds2D();
                        spawnPoint = new Vector2(
                                (int) spawnPointShape.getX(),
                                (int) spawnPointShape.getY()
                        );

                        break;
                    }
                }

                if (spawnPoint != null) {
                    break;
                }
            }

            World world = new World(
                    file.getName().split("\\.")[0],
                    spawnPoint
            );

            int layerId = 0;
            int dimensionWidth = 0;
            int dimensionHeight = 0;
            List<Point2D> points = new ArrayList<>();
            for (Element groupElement : getAllElementNode(map.getElementsByTagName("group"))) {
                int subLayerId = 0;
                for (Element layerElement : getAllElementNode(groupElement.getElementsByTagName("layer"))) {
                    if (layerElement.getAttribute("visible").equals("0")) {
                        continue;
                    }

                    var subLayer = new SubLayer(layerId, subLayerId, layerElement.getAttribute("name"));

                    // Extraire les données du CSV
                    var chunkElements = getAllElementNode(groupElement.getElementsByTagName("chunk"));
                    if (chunkElements.isEmpty()) {
                        var csvElement = (Element) layerElement.getElementsByTagName("data").item(0);
                        int width = Integer.parseInt(csvElement.getAttribute("width"));
                        int height = Integer.parseInt(csvElement.getAttribute("height"));
                        for (var state : loadTilesFromCSV(csvElement, tiles, width, height, 0, 0).entrySet()) {
                            //world.setTile(state.getValue(), state.getKey(), subLayer);
                        }

                        dimensionWidth += width;
                        dimensionHeight += height;
                    } else {
                        for (Element chunkElement : chunkElements) {
                            int width = Integer.parseInt(chunkElement.getAttribute("width"));
                            int height = Integer.parseInt(chunkElement.getAttribute("height"));
                            int x = Integer.parseInt(chunkElement.getAttribute("x"));
                            int y = Integer.parseInt(chunkElement.getAttribute("y"));
                            points.add(new Point2D(x, y));
                            for (var state : loadTilesFromCSV(
                                    chunkElement, tiles,
                                    width, height,
                                    y, x
                            ).entrySet()) {
                                var data = new SpawnData(state.getKey().toPixel().x(), state.getKey().toPixel().y());
                                for (var v : state.getValue().getData().entrySet()) {
                                    data.put(v.getKey(), v.getValue());
                                }
                                FXGL.spawn("tile", data);
                                //world.setTile(state.getValue(), state.getKey(), subLayer);
                            }
                        }
                    }

                    subLayerId++;
                }

                layerId++;
            }

            BoundingShape shape = BoundingShape.polygon(points);
            world.setDimension(shape);

            loadEntities(world, file, roomScale);

            return world;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Map<Cell, SpawnData> loadTilesFromCSV(Element csvElement, Map<Integer, SpawnData> tiles, int width, int height, int startRow, int startColumn) {
        String csvData = csvElement.getTextContent().trim();
        System.out.println("CSV Data: " + csvData);

        // Découper le CSV en éléments séparés par des virgules
        String[] ids = csvData.split(",");

        Map<Cell, SpawnData> result = new HashMap<>();
        // Pour chaque ID de tile, on associe l'image correspondante
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int tileId = Integer.parseInt(ids[row * width + column].trim());
                if (!tiles.containsKey(tileId)) continue;

                result.put(
                        new Cell(column + startColumn, row + startRow),
                        tiles.get(tileId)
                );
            }
        }

        return result;
    }

    private void loadEntities(World world, File mapFile, Vector2 roomScale) {
        Document map = createDocument(mapFile);

        for (Element objectGroupElement : getAllElementNode(map.getElementsByTagName("objectgroup"))) {
            if (!objectGroupElement.getAttribute("name").equals("entities")) continue;

            for (Element object : getAllElementNode(objectGroupElement.getElementsByTagName("object"))) {
                Vector2 spawnPoint = loadVector(object, roomScale);
                String name = "";
                boolean isMoving = false;
                for (Element properties : getAllElementNode(object.getElementsByTagName("properties"))) {
                    for (Element property : getAllElementNode(properties.getChildNodes())) {
                        String propertyName = property.getAttribute("name");
                        String propertyValue = property.getAttribute("value");
                        switch (propertyName) {
                            case "entity" -> {
                                if (propertyValue.equals("moving")) {
                                    isMoving = true;
                                }
                            }
                            case "name" -> name = propertyValue;
                        }
                    }
                }

                /**Entity entity;
                entity = new Entity(name, null, new Position(spawnPoint, world), 2);

                world.addEntity(entity);*/
            }
        }
    }

    private Object loadProperty(Element property) {
        String value = property.getAttribute("value");
        if (!property.hasAttribute("type")) {
            return value;
        }

        return switch (property.getAttribute("type")) {
            case "int" -> Integer.parseInt(value);
            case "float" -> Float.parseFloat(value);
            case "color" -> Color.decode(value);
            case "bool" -> Boolean.parseBoolean(value);
            default -> null; //TODO: custom object
        };
    }
}
