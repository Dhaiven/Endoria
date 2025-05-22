package game.pkg_Loader.pkg_Tiled;

import game.pkg_Entity.FacingDirection;
import game.pkg_Item.Beamer;
import game.pkg_Item.Key;
import game.pkg_Item.MagicCookie;
import game.pkg_Loader.LoaderUtils;
import game.pkg_Loader.WorldLoader;
import game.pkg_Object.Vector2;
import game.pkg_Room.Door;
import game.pkg_Room.LockDoor;
import game.pkg_Room.Room;
import game.pkg_Tile.Tile;
import game.pkg_Util.Utils;
import game.pkg_World.World;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static game.pkg_Loader.pkg_Tiled.TiledLoaderUtils.*;

public class TiledWorldLoader implements WorldLoader {

    private final HashMap<String, Room> rooms = new HashMap<>();
    private final HashMap<String, File> filesByRoom = new HashMap<>();

    @Override
    public String getExtension() {
        return "world";
    }

    // Load les fichier en .world
    @Override
    public World load(File world) {
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

            Map<String, Map<Integer, Tile>> alreadyLoadedTile = new HashMap<>();

            // Extraire les maps
            String[] mapEntries = json.split("maps:")[1].split(",onlyShowAdjacentMaps")[0].split(",");
            for (String entry : mapEntries) {
                if (entry.trim().isEmpty()) continue;
                String[] parts = entry.split(",");
                for (String part : parts) {
                    if (part.contains("fileName")) {
                        TiledRoomLoader roomLoader = new TiledRoomLoader(alreadyLoadedTile);
                        File roomFile = resolveAbsolutePath(world, part.split(":")[1].trim());
                        Room room = roomLoader.load(roomFile);

                        rooms.put(room.getName(), room);
                        filesByRoom.put(room.getName(), roomFile);
                        alreadyLoadedTile.putAll(roomLoader.getAlreadyLoadedTiles());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Room room : rooms.values()) {
            loadDoors(room, filesByRoom.get(room.getName()));
            loadItems(room);
        }

        return new World(world.getName().split("\\.")[0], this.rooms.values().stream().toList());
    }

    private void loadDoors(Room room, File mapFile) {
        Document map = createDocument(mapFile);

        for (Element groupElement : getAllElementNode(map.getElementsByTagName("group"))) {
            if (!groupElement.getAttribute("name").equals("zone")) continue;

            for (Element objectGroupElement : getAllElementNode(groupElement.getElementsByTagName("objectgroup"))) {
                if (!objectGroupElement.getAttribute("name").equals("doors")) continue;

                for (Element object : getAllElementNode(objectGroupElement.getElementsByTagName("object"))) {
                    Shape doorShape = loadBaseShape(object, room.getRoomScale());
                    Room toRoom = null;
                    Integer toDoorId = null;
                    FacingDirection facing = null;
                    boolean needKey = false;
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
                                    facing = LoaderUtils.loadEnum(property.getAttribute("value"), FacingDirection.class);
                                    break;
                                case "needKey":
                                    needKey = true;
                                    break;
                            }
                        }
                    }

                    if (toRoom == null || toDoorId == null) continue;

                    Door door;
                    if (needKey) {
                        door = new LockDoor(
                                doorShape,
                                Utils.getCenterPosition(
                                        Objects.requireNonNull(
                                                loadDoorShape(filesByRoom.get(toRoom.getName()), toDoorId, room.getRoomScale()
                                                )
                                        )
                                ),
                                toRoom,
                                new Key()
                        );
                    } else {
                        door = new Door(
                                doorShape,
                                Utils.getCenterPosition(
                                        Objects.requireNonNull(
                                                loadDoorShape(filesByRoom.get(toRoom.getName()), toDoorId, room.getRoomScale()
                                                )
                                        )
                                ),
                                toRoom
                        );
                    }

                    room.addExit(facing, door);
                }
            }
        }
    }

    private Shape loadDoorShape(File mapFile, int doorId, Vector2 roomScale) {
        Document map = createDocument(mapFile);

        for (Element groupElement : getAllElementNode(map.getElementsByTagName("group"))) {
            if (!groupElement.getAttribute("name").equals("zone")) continue;

            for (Element objectGroupElement : getAllElementNode(groupElement.getElementsByTagName("objectgroup"))) {
                if (!objectGroupElement.getAttribute("name").equals("doors")) continue;

                for (Element object : getAllElementNode(objectGroupElement.getElementsByTagName("object"))) {
                    if (object.getAttribute("id").equals(String.valueOf(doorId))) {
                        return loadBaseShape(object, roomScale);
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
            case "bool" -> Boolean.parseBoolean(value);
            default -> null; //TODO: custom object
        };
    }

    /**
     * TODO: Load items with a config
     */
    private void loadItems(Room room) {
        switch (room.getName()) {
            case "forestWorld8" -> room.getItemList().addItem(new Key());
            case "forestWorld9" -> room.getItemList().addItem(new Beamer());
            case "forestWorld10" -> room.getItemList().addItem(new MagicCookie());
        }
    }
}
