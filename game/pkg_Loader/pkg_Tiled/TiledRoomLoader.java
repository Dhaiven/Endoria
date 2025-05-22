package game.pkg_Loader.pkg_Tiled;

import game.pkg_Entity.Entity;
import game.pkg_Entity.FacingDirection;
import game.pkg_Entity.MovingCharacter;
import game.pkg_Loader.Loader;
import game.pkg_Loader.LoaderUtils;
import game.pkg_Object.Position;
import game.pkg_Object.Vector2;
import game.pkg_Object.Vector2i;
import game.pkg_Room.Door;
import game.pkg_Room.Room;
import game.pkg_Tile.Tile;
import game.pkg_Util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static game.pkg_Loader.pkg_Tiled.TiledLoaderUtils.*;

public class TiledRoomLoader implements Loader {

    private final Map<String, Map<Integer, Tile>> alreadyLoadedTiles;

    public TiledRoomLoader(final Map<String, Map<Integer, Tile>> alreadyLoadedTiles) {
        this.alreadyLoadedTiles = alreadyLoadedTiles;
    }

    @Override
    public String getExtension() {
        return "tmx";
    }

    public Map<String, Map<Integer, Tile>> getAlreadyLoadedTiles() {
        return alreadyLoadedTiles;
    }

    // Load les fichier en .tmx
    public Room load(File mapFile) throws IOException {
        Document map = createDocument(mapFile);

        Element mapElement = (Element) map.getElementsByTagName("map").item(0);

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
            File tsxFile = resolveAbsolutePath(mapFile, tsxFilePath);

            int firstId = Integer.parseInt(tileSetElement.getAttribute("firstgid"));
            needFiles.put(tsxFile.getPath(), firstId);
            if (this.alreadyLoadedTiles.containsKey(tsxFile.getPath())) {
                continue;
            }

            // Charger les textures du fichier .tsx référencé
            TiledTileLoader tileLoader = new TiledTileLoader(firstId, roomScale);
            this.alreadyLoadedTiles.put(tsxFile.getPath(), tileLoader.load(tsxFile));
        }

        Map<Integer, Tile> tiles = new HashMap<>();
        for (Map.Entry<String, Integer> entry : needFiles.entrySet()) {
            for (var tileEntry : alreadyLoadedTiles.get(entry.getKey()).entrySet()) {
                tiles.put(tileEntry.getKey() + entry.getValue(), tileEntry.getValue());
            }
        }

        Map<Integer, List<Map<Vector2i, Tile>>> mapLayers = new HashMap<>();
        int layerId = 0;
        for (Element groupElement : getAllElementNode(map.getElementsByTagName("group"))) {
            List<Map<Vector2i, Tile>> mapLayer = new ArrayList<>();
            for (Element layerElement : getAllElementNode(groupElement.getElementsByTagName("layer"))) {
                if (layerElement.getAttribute("visible").equals("0")) {
                    continue;
                }

                // Extraire les données du CSV
                mapLayer.add(loadTilesFromCSV(layerElement, tiles));
            }

            mapLayers.put(layerId, mapLayer);
            layerId++;
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

        int width = (int) (Integer.parseInt(mapElement.getAttribute("width")) * roomScale.x());
        int height = (int) (Integer.parseInt(mapElement.getAttribute("height")) * roomScale.y());
        Shape shape = new Rectangle(0, 0, width * tileWidth, height * tileHeight);

        Room room = new Room(
                shape,
                mapFile.getName().split("\\.")[0],
                roomScale,
                mapLayers,
                spawnPoint
        );
        loadEntities(room, mapFile, roomScale);

        return room;
    }

    private Map<Vector2i, Tile> loadTilesFromCSV(Element csvElement, Map<Integer, Tile> tiles) {
        Element data = (Element) csvElement.getElementsByTagName("data").item(0);
        String csvData = data.getTextContent().trim();

        // Découper le CSV en éléments séparés par des virgules
        String[] ids = csvData.split(",");

        int width = Integer.parseInt(csvElement.getAttribute("width"));
        int height = Integer.parseInt(csvElement.getAttribute("height"));

        Map<Vector2i, Tile> result = new HashMap<>();
        // Pour chaque ID de tile, on associe l'image correspondante
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int tileId = Integer.parseInt(ids[row * width + column].trim());
                if (!tiles.containsKey(tileId)) continue;

                result.put(
                        new Vector2i(column, row).pow(Utils.TEXTURE_SIZE),
                        tiles.get(tileId)
                );
            }
        }

        return result;
    }

    private void loadEntities(Room room, File mapFile, Vector2 roomScale) {
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

                Entity entity;
                if (isMoving) {
                    entity = new MovingCharacter(name, null, new Position(spawnPoint, room));
                } else {
                    entity = new Entity(name, null, new Position(spawnPoint, room), 2);
                }

                room.addEntity(entity);
            }
        }
    }
}
