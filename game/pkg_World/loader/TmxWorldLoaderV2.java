package game.pkg_World.loader;

import game.pkg_Entity.FacingDirection;
import game.pkg_Image.Sprite;
import game.pkg_Object.Vector2;
import game.pkg_Room.Door;
import game.pkg_Room.Room;
import game.pkg_Tile.Tile;
import game.pkg_Util.Utils;
import game.pkg_World.World;
import org.w3c.dom.*;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class TmxWorldLoaderV2 {

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

    private Vector2 roomScale = new Vector2(1, 1);

    private final HashMap<Integer, Tile> tiles = new HashMap<>();
    private final HashMap<String, Room> rooms = new HashMap<>();
    private final HashMap<String, File> filesByRoom = new HashMap<>();

    public TmxWorldLoaderV2() {

    }

    // Méthode pour extraire les textures depuis un fichier .tsx
    private Map<Integer, Tile> loadTiles(File tsxFile, final int firstId) {
        Map<Integer, Tile> textures = new HashMap<>();

        try {
            Document doc = createDocument(tsxFile);

            for (Element tileElement : getAllElementNode(doc.getElementsByTagName("tileset"))) {
                int tileWidth = Integer.parseInt(tileElement.getAttribute("tilewidth"));
                int tileHeight = Integer.parseInt(tileElement.getAttribute("tileheight"));

                List<Element> collisionTiles = getAllElementNode(tileElement.getElementsByTagName("tile"));

                // Extraction du chemin de l'image à partir de la balise <image>
                for (Element imageElement : getAllElementNode(tileElement.getElementsByTagName("image"))) {
                    String imagePath = imageElement.getAttribute("source");

                    // Résoudre également le chemin de l'image
                    File imageFile = resolveAbsolutePath(tsxFile, imagePath);
                    BufferedImage sourceImage = ImageIO.read(imageFile);

                    int imageWidth = Integer.parseInt(imageElement.getAttribute("width"));
                    int imageHeight = Integer.parseInt(imageElement.getAttribute("height"));

                    // Extraire toutes les sous-images correspondant aux tuiles
                    int id = firstId;
                    for (int y = 0; y < imageHeight; y += tileHeight) {
                        for (int x = 0; x < imageWidth; x += tileWidth) {
                            Shape collision = null;
                            for (Element collisionTile : collisionTiles) {
                                int tileId = Integer.parseInt(collisionTile.getAttribute("id"));
                                if (tileId != (id - firstId)) continue;

                                Element objectGroup = (Element) collisionTile.getElementsByTagName("objectgroup").item(0);
                                Element object = (Element) objectGroup.getElementsByTagName("object").item(0);

                                collision = loadBaseShape(object);
                                break;
                            }

                            textures.put(id, new Tile(
                                    id - firstId,
                                    new Sprite(sourceImage.getSubimage(x, y, tileWidth, tileHeight)),
                                    collision
                            ));
                            id++;
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

    // Load les fichier en .world
    public World loadWorld(File world) {
        List<Room> rooms = new ArrayList<>();

        try {
            StringBuilder jsonContent = new StringBuilder();

            // Lire le contenu du fichier JSON
            try (BufferedReader br = new BufferedReader(new FileReader(world))) {
                String line;
                while ((line = br.readLine()) != null) {
                    jsonContent.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            String json = jsonContent.toString();

            // Supprimer les accolades et crochets pour simplifier l'analyse
            json = json.replaceAll("[\\[\\]{}\"]", "");

            // Extraire les maps
            String[] mapEntries = json.split("maps:")[1].split(",onlyShowAdjacentMaps")[0].split(",");
            for (String entry : mapEntries) {
                if (entry.trim().isEmpty()) continue;
                String[] parts = entry.split(",");
                for (String part : parts) {
                    if (part.contains("fileName")) {
                        rooms.add(loadMap(resolveAbsolutePath(world, part.split(":")[1].trim())));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Room room : rooms) {
            loadDoors(room, filesByRoom.get(room.getName()));
            System.out.println(room.getExits());
        }

        return new World(world.getName().split("\\.")[0], rooms);
    }

    // Load les fichier en .tmx
    private Room loadMap(File mapFile) {
        Document map = createDocument(mapFile);

        Element mapElement = (Element) map.getElementsByTagName("map").item(0);

        int tileWidth = Integer.parseInt(mapElement.getAttribute("tilewidth"));
        int tileHeight = Integer.parseInt(mapElement.getAttribute("tileheight"));

        roomScale = new Vector2(
                (double) Utils.TEXTURE_WIDTH / tileWidth,
                (double) Utils.TEXTURE_HEIGHT / tileHeight
        );

        // On récupère tous les tilesets
        for (Element tileSetElement : getAllElementNode(map.getElementsByTagName("tileset"))) {
            // Extraction du chemin du fichier .tsx depuis l'attribut source
            String tsxFilePath = tileSetElement.getAttribute("source");
            int firstId = Integer.parseInt(tileSetElement.getAttribute("firstgid"));

            // Charger les textures du fichier .tsx référencé
            Map<Integer, Tile> tsxTextures = loadTiles(resolveAbsolutePath(mapFile, tsxFilePath), firstId);
            tiles.putAll(tsxTextures);
        }

        Map<Integer, Map<Vector2, Tile>> mapLayers = new HashMap<>();
        for (Element layerElement : getAllElementNode(map.getElementsByTagName("layer"))) {
            int layerId = Integer.parseInt(layerElement.getAttribute("id"));

            // Extraire les données du CSV
            mapLayers.put(layerId - 1, loadTilesFromCSV(layerElement));
        }

        Shape spawnPoint = null;
        for (Element objectGroup : getAllElementNode(map.getElementsByTagName("objectgroup"))) {
            for (Element object : getAllElementNode(objectGroup.getElementsByTagName("object"))) {
                if (object.getAttribute("name").equals("spawnpoint")) {
                    spawnPoint = loadBaseShape(object);
                    break;
                }
            }

            if (spawnPoint != null) {
                break;
            }
        }

        if (spawnPoint == null) {
            throw new RuntimeException("Spawnpoint not found");
        }

        int width = (int) (Integer.parseInt(mapElement.getAttribute("width")) * roomScale.x());
        int height = (int) (Integer.parseInt(mapElement.getAttribute("height")) * roomScale.y());
        Rectangle2D spawnPointBounds = spawnPoint.getBounds2D();
        Shape shape = new Rectangle(0, 0, width * tileWidth, height * tileHeight);

        Room room = new Room(
                shape,
                mapFile.getName().split("\\.")[0],
                new Vector2(
                        (int) spawnPointBounds.getX(),
                        (int) spawnPointBounds.getY()
                ),
                mapLayers
        );

        rooms.put(room.getName(), room);
        filesByRoom.put(room.getName(), mapFile);

        /**for (Element group : getAllElementNode(map.getElementsByTagName("group"))) {
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
                        case SPAWNS -> loadSpawns(object);
                        case ROOMS -> loadRooms(object);
                        case DOORS -> loadDoors(object);
                    }

                    order++;
                }
            }
        }*/

        return room;
    }

    private void loadDoors(Room room, File mapFile) {
        Document map = createDocument(mapFile);

        for (Element groupElement : getAllElementNode(map.getElementsByTagName("group"))) {
            if (!groupElement.getAttribute("name").equals("zone")) continue;

            for (Element objectGroupElement : getAllElementNode(groupElement.getElementsByTagName("objectgroup"))) {
                if (!objectGroupElement.getAttribute("name").equals("doors")) continue;

                System.out.println("objectGroupElement: " + objectGroupElement.getAttribute("name"));
                for (Element object : getAllElementNode(objectGroupElement.getElementsByTagName("object"))) {
                    System.out.println("object: " + object.getAttribute("name"));
                    Shape doorShape = loadBaseShape(object);
                    Room toRoom = null;
                    Integer toDoorId = null;
                    FacingDirection facing = null;
                    for (Element properties : getAllElementNode(object.getElementsByTagName("properties"))) {
                        for (Element property : getAllElementNode(properties.getChildNodes())) {
                            Object value = loadProperty(property);
                            if (value == null) continue;

                            switch (property.getAttribute("name")) {
                                case "toRoomName":
                                    toRoom = rooms.get(value.toString());
                                    break;
                                case "toDoorId":
                                    toDoorId = (int) value;
                                    break;
                                case "facing":
                                    facing = loadFacingProperty(property);
                                    break;
                            }
                        }
                    }

                    if (toRoom == null) {
                        throw new RuntimeException("toRoom is not initialized");
                    }
                    if (toDoorId == null) {
                        throw new RuntimeException("DoorId is not initialized");
                    }

                    room.addExit(
                            facing,
                            new Door(
                                    doorShape,
                                    Utils.getCenterPosition(Objects.requireNonNull(loadDoorShape(filesByRoom.get(toRoom.getName()), toDoorId))),
                                    toRoom
                            )
                    );
                    System.out.println("room add exit");
                }
            }
        }
    }

    private Shape loadDoorShape(File mapFile, int doorId) {
        Document map = createDocument(mapFile);

        for (Element groupElement : getAllElementNode(map.getElementsByTagName("group"))) {
            if (!groupElement.getAttribute("name").equals("zone")) continue;

            for (Element objectGroupElement : getAllElementNode(groupElement.getElementsByTagName("objectgroup"))) {
                if (!objectGroupElement.getAttribute("name").equals("doors")) continue;

                for (Element object : getAllElementNode(objectGroupElement.getElementsByTagName("object"))) {
                    System.out.println("objectID: " + object.getAttribute("id"));
                    System.out.println("id: " + doorId);
                    if (object.getAttribute("id").equals(String.valueOf(doorId))) {
                        return loadBaseShape(object);
                    }
                }
            }
        }

        return null;
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
            default -> null; //TODO: custom object
        };
    }

    /**
     * TODO: faire une class permetant de load plein d'objet custom si on en a plusieurs
     * Pour l'instant, si on a que Facing on laisse ça
     * @param property
     * @return
     */
    private FacingDirection loadFacingProperty(Element property) {
        return switch (property.getAttribute("value")) {
            case "NORTH" -> FacingDirection.NORTH;
            case "SOUTH" -> FacingDirection.SOUTH;
            case "EAST" -> FacingDirection.EAST;
            case "WEST" -> FacingDirection.WEST;
            default -> throw new IllegalStateException("Unexpected value: " + property.getAttribute("value"));
        };
    }
    

    /**private void loadDoors(Element doors) {
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
            String doorName = object.getAttribute("name");

            String fromRoomId = object.getAttribute("id");
            Room fromRoom = doorByRooms.get(fromRoomId);

            if (fromRoom == null) {
                throw new IllegalArgumentException("Room " + doorName + " is not supported");
            }

            if (doorName.startsWith("to")) {
                String toRoomId = doorName.substring(2).toLowerCase();
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

    private void loadSpawns(Element spawns) {
        for (Element object : getAllElementNode(spawns.getElementsByTagName("object"))) {
            String objectName = object.getAttribute("name");
            if (objectName.startsWith("spawn")) {
                String rommName = objectName.substring(5);
                rommName = rommName.substring(0, 1).toLowerCase() + rommName.substring(1);

                spawnRooms.put(rommName, object);
            }
        }
    }*/

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
        startAtX *= roomScale.x();
        startAtY *= roomScale.y();

        List<Element> polygons = getAllElementNode(object.getElementsByTagName("polygon"));
        if (!polygons.isEmpty()) {
            Element polygonElement = polygons.get(0);
            String pointsStr = polygonElement.getAttribute("points");
            String[] pointsArray = pointsStr.split(" ");

            int[] xPoints = new int[pointsArray.length];
            int[] yPoints = new int[pointsArray.length];

            for (int i = 0; i < pointsArray.length; i++) {
                String[] point = pointsArray[i].split(",");
                int localX = (int) Float.parseFloat(point[0]);
                int localY = (int) Float.parseFloat(point[1]);

                // Convertit les coordonnées relatives en absolues
                xPoints[i] = (int) (startAtX + (localX * roomScale.x()));
                yPoints[i] = (int) (startAtY + (localY * roomScale.y()));
            }

            return new Polygon(xPoints, yPoints, pointsArray.length);
        }

        List<Element> points = getAllElementNode(object.getElementsByTagName("point"));
        if (!points.isEmpty()) {
            return new Ellipse2D.Double(startAtX, startAtY, 0, 0);
        }

        int width = (int) ((int) Float.parseFloat(object.getAttribute("width")) * roomScale.x());
        int height = (int) ((int) Float.parseFloat(object.getAttribute("height")) * roomScale.y());

        List<Element> ellipses = getAllElementNode(object.getElementsByTagName("ellipse"));
        if (!ellipses.isEmpty()) {
            return new Ellipse2D.Float(startAtX, startAtY, width, height);
        }

        return new Rectangle(startAtX, startAtY, width, height);
    }

    private Map<Vector2, Tile> loadTilesFromCSV(Element csvElement) {
        Element data = (Element) csvElement.getElementsByTagName("data").item(0);
        String csvData = data.getTextContent().trim();

        // Découper le CSV en éléments séparés par des virgules
        String[] ids = csvData.split(",");

        int width = Integer.parseInt(csvElement.getAttribute("width"));
        int height = Integer.parseInt(csvElement.getAttribute("height"));

        Map<Vector2, Tile> tiles = new HashMap<>();
        // Pour chaque ID de tile, on associe l'image correspondante
        for (int row = 0; row < height; row++) {
            for (int column = 0; column < width; column++) {
                int tileId = Integer.parseInt(ids[row * width + column].trim());
                if (!this.tiles.containsKey(tileId)) continue;

                tiles.put(
                        new Vector2(
                            column * Utils.TEXTURE_WIDTH,
                            row * Utils.TEXTURE_HEIGHT
                        ),
                        this.tiles.get(tileId)
                );
            }
        }

        return tiles;
    }
}
