package game.pkg_World;

import game.pkg_Room.Room;

import java.util.ArrayList;
import java.util.List;

public class World {

    private final String name;
    private final List<Room> rooms = new ArrayList<>();

    private Room spawnRoom = null;

    public World(String name, List<Room> pRooms) {
        this.name = name;
        this.rooms.addAll(pRooms);

        for (Room room : rooms) {
            if (room.getSpawnPoint() != null) {
                this.spawnRoom = room;
                break;
            }
        }
    }

    public String getName() {
        return name;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Room getSpawnRoom() {
        return spawnRoom;
    }
}
