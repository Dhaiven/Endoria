package game.pkg_World.loader;

import game.pkg_Entity.Sprite;
import game.pkg_Object.Vector2;
import game.pkg_Room.Door;
import game.pkg_Room.Room;
import game.pkg_Tile.Tile;
import game.pkg_Util.FileUtils;
import game.pkg_Util.Utils;
import game.pkg_World.Layers;
import game.pkg_World.World;
import org.w3c.dom.*;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

public class TmxWorldLoader {

    private static final String GROUND_LAYER = "ground";
    private static final String DECORATION_LAYER = "decoration";
    private static final String ENTITY_LAYER = "entity";
    private static final String DECORATION_BEHIND_ENTITY_LAYER = "decorationBehindEntity";

    private static Document createDocument(File file) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static File resolveAbsolutePath(File callBy, String to) {
        return new File(callBy.getParent() + File.separator + to);
    }

    private final List<Sprite> allTextures = new ArrayList<>();
    private final HashMap<String, Room> worldRooms = new HashMap<>();
    private final Map<Integer, List<TilePos>> mapLayers = new HashMap<>();
    private final Map<String, Shape> baseRoomsOffset = new HashMap<>();

    public TmxWorldLoader() {

    }

    // Méthode pour extraire les textures depuis un fichier .tsx
    private List<Sprite> extractTexturesFromTileset(File tsxFile) {
        List<Sprite> textures = new ArrayList<>();

        try {
            Document doc = createDocument(tsxFile);

            for (Element tileElement : getAllElementNode(doc.getElementsByTagName("tileset"))) {
                int tileWidth = Integer.parseInt(tileElement.getAttribute("tilewidth"));
                int tileHeight = Integer.parseInt(tileElement.getAttribute("tileheight"));

                // Extraction du chemin de l'image à partir de la balise <image>
                for (Element imageElement : getAllElementNode(tileElement.getElementsByTagName("image"))) {
                    String imagePath = imageElement.getAttribute("source");

                    // Résoudre également le chemin de l'image
                    File imageFile = resolveAbsolutePath(tsxFile, imagePath);
                    BufferedImage sourceImage = ImageIO.read(imageFile);

                    int imageWidth = Integer.parseInt(imageElement.getAttribute("width"));
                    int imageHeight = Integer.parseInt(imageElement.getAttribute("height"));

                    // Extraire toutes les sous-images correspondant aux tuiles
                    for (int y = 0; y < imageHeight; y += tileHeight) {
                        for (int x = 0; x < imageWidth; x += tileWidth) {
                            textures.add(new Sprite(sourceImage.getSubimage(x, y, tileWidth, tileHeight)));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return textures;
    }

    private List<Element> getAllElementNode(NodeList nodeList) {
        List<Element> elements = new ArrayList<>();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node item = nodeList.item(i);
            if (item.getNodeType() == Node.ELEMENT_NODE) {
                elements.add((Element) item);
            }
        }

        return elements;
    }

    public World loadWorld(String name) {
        Layers.LayersBuilder layersBuilder = Layers.builder();

        try {
            File file = new File(FileUtils.WORLD_RESOURCES + name + "/" + name + ".tmx");
            Document doc = createDocument(file);

            // On récupère tous les tilesets
            for (Element tileSetElement : getAllElementNode(doc.getElementsByTagName("tileset"))) {
                // Extraction du chemin du fichier .tsx depuis l'attribut source
                String tsxFilePath = tileSetElement.getAttribute("source");

                // Charger les textures du fichier .tsx référencé
                List<Sprite> tsxTextures = extractTexturesFromTileset(resolveAbsolutePath(file, tsxFilePath));
                allTextures.addAll(tsxTextures);
            }

            for (Element layerElement : getAllElementNode(doc.getElementsByTagName("layer"))) {

                String layerName = layerElement.getAttribute("name");
                int layerId = Integer.parseInt(layerElement.getAttribute("id"));
                switch (layerName) {
                    case GROUND_LAYER -> layersBuilder.setGroundLayer(layerId);
                    case DECORATION_LAYER -> layersBuilder.setDecorationLayer(layerId);
                    case ENTITY_LAYER -> layersBuilder.setEntityLayer(layerId);
                    case DECORATION_BEHIND_ENTITY_LAYER -> layersBuilder.setDecorationBehindEntityLayer(layerId);
                    default -> throw new IllegalArgumentException("Layer " + layerName + " is not supported");
                }

                // Extraire les données du CSV
                Node dataNode = layerElement.getElementsByTagName("data").item(0);
                List<TilePos> tiles = getTilePosList(dataNode, layerElement);

                mapLayers.put(layerId, tiles);
            }

            for (Element group : getAllElementNode(doc.getElementsByTagName("group"))) {
                String zoneGroupName = group.getAttribute("name");
                if (!zoneGroupName.equals("zone")) continue;

                int order = 0;
                List<Element> allObjectGroup = getAllElementNode(group.getElementsByTagName("objectgroup"));
                for (ObjectGroupOrder objectGroupOrder : ObjectGroupOrder.values()) {
                    if (objectGroupOrder.getOrder() != order) continue;

                    List<Element> correspondObjects = new ArrayList<>();
                    for (Element objectElement : allObjectGroup) {
                        if (objectElement.getAttribute("name").equals(objectGroupOrder.getId())) {
                            correspondObjects.add(objectElement);
                        }
                    }

                    for (Element object : correspondObjects) {
                        switch (objectGroupOrder) {
                            case ROOMS -> loadRooms(object);
                            case DOORS -> loadDoors(object);
                        }

                        order++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new World(name, List.copyOf(worldRooms.values()), Layers.builder().build());
    }

    private void loadRooms(Element rooms) {
        for (Element object : getAllElementNode(rooms.getElementsByTagName("object"))) {
            String roomName = object.getAttribute("name");
            Shape basicShape = loadBaseShape(object);

            baseRoomsOffset.put(roomName, basicShape);

            int x = (int) Float.parseFloat(object.getAttribute("x"));
            int y = (int) Float.parseFloat(object.getAttribute("y"));

            Room room = new Room(loadShape(object, 0, 0), roomName, Layers.builder().build());
            for (Map.Entry<Integer, List<TilePos>> entry : mapLayers.entrySet()) {
                for (TilePos tilePos : entry.getValue()) {
                    if (basicShape.contains(tilePos.x(), tilePos.y())) {
                        room.setTile(
                                tilePos.tile(),
                                new Vector2(
                                        tilePos.x() - x,
                                        tilePos.y() - y
                                ),
                                entry.getKey()
                        );
                    }
                }
            }

            worldRooms.put(roomName, room);
        }
    }

    private void loadDoors(Element doors) {
        List<Element> objects = getAllElementNode(doors.getElementsByTagName("object"));

        Map<String, Room> doorByRooms = new HashMap<>();
        Map<String, Shape> doorByShapes = new HashMap<>();
        for (Element object : objects) {
           String id = object.getAttribute("id");
           Area doorArea = new Area(loadBaseShape(object));

            for (Map.Entry<String, Shape> entry : baseRoomsOffset.entrySet()) {
                Area area = new Area(entry.getValue());
                area.intersect(doorArea);
                if (!area.isEmpty()) {
                    Room room = worldRooms.get(entry.getKey());
                    doorByRooms.put(id, room);
                    doorByShapes.put(id, loadOffsetShape(object, entry.getValue()));
                    break;
                }
            }
        }

        for (Element object : objects) {
            String fromRoomId = object.getAttribute("id");
            Room fromRoom = doorByRooms.get(fromRoomId);
            if (fromRoom == null) {
                throw new IllegalArgumentException("Room " + object.getAttribute("name") + " is not supported");
            }

            String doorName = object.getAttribute("name");
            String[] nameParts = doorName.split(";");
            for (String namePart : nameParts) {
                if (namePart.startsWith("to")) {
                    String toRoomId = namePart.substring(2).toLowerCase();
                    Room toRoom = doorByRooms.get(toRoomId);

                    fromRoom.addExit(
                            Utils.getDirection(baseRoomsOffset.get(fromRoom.getName()), baseRoomsOffset.get(toRoom.getName())),
                            new Door(
                                    doorByShapes.get(fromRoomId),
                                    Utils.getCenterPosition(doorByShapes.get(toRoomId)),
                                    toRoom
                            )
                    );
                }
            }
        }
    }

    private Shape loadOffsetShape(Element object, Shape offset) {
        Rectangle bounds = offset.getBounds();

        int x = (int) Float.parseFloat(object.getAttribute("x")) - bounds.x;
        int y = (int) Float.parseFloat(object.getAttribute("y")) - bounds.y;

        return loadShape(object, x, y);
    }

    private Shape loadBaseShape(Element object) {
        int x = (int) Float.parseFloat(object.getAttribute("x"));
        int y = (int) Float.parseFloat(object.getAttribute("y"));

        return loadShape(object, x, y);
    }

    private Shape loadShape(Element object, int startAtX, int startAtY) {
        List<Element> polygons = getAllElementNode(object.getElementsByTagName("polygon"));
        if (polygons.isEmpty()) {
            return new Rectangle(
                    startAtX,
                    startAtY,
                    (int) Float.parseFloat(object.getAttribute("width")),
                    (int) Float.parseFloat(object.getAttribute("height"))
            );
        }

        Element polygonElement = polygons.get(0);
        String pointsStr = polygonElement.getAttribute("points");
        String[] pointsArray = pointsStr.split(" ");

        int[] xPoints = new int[pointsArray.length];
        int[] yPoints = new int[pointsArray.length];

        for (int i = 0; i < pointsArray.length; i++) {
            String[] point = pointsArray[i].split(",");
            int localX = Integer.parseInt(point[0]);
            int localY = Integer.parseInt(point[1]);

            // Convertit les coordonnées relatives en absolues
            xPoints[i] = startAtX + localX;
            yPoints[i] = startAtY + localY;
        }

        return new Polygon(xPoints, yPoints, pointsArray.length);
    }

    private List<TilePos> getTilePosList(Node dataNode, Element layerElement) {
        String csvData = dataNode.getTextContent().trim();

        // Découper le CSV en éléments séparés par des virgules
        String[] ids = csvData.split(",");

        int width = Integer.parseInt(layerElement.getAttribute("width"));
        int height = Integer.parseInt(layerElement.getAttribute("height"));

        List<TilePos> tiles = new ArrayList<>();
        // Pour chaque ID de tile, on associe l'image correspondante
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int tileId = Integer.parseInt(ids[row * width + column].trim());
                if (tileId <= 0) continue;

                int textureIndex = tileId - 1;  // L'ID commence à 1, les index de la liste commencent à 0
                if (textureIndex >= allTextures.size()) continue;

                Sprite sprite = allTextures.get(textureIndex);

                tiles.add(new TilePos(
                        new Tile(sprite),
                        column * Utils.TEXTURE_WIDTH,
                        row * Utils.TEXTURE_HEIGHT
                ));
            }
        }
        return tiles;
    }

    private record TilePos(Tile tile, int x, int y) {
    }
}
