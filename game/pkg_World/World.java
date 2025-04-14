package game.pkg_World;

import game.pkg_Room.Room;

import java.util.ArrayList;
import java.util.List;

public class World {

    public static final int[] LAYERS = new int[] {0, 1, 2, 3};

    private final String name;
    private final List<Room> rooms = new ArrayList<>();

    private final Room spawnRoom;

    public World(String name, List<Room> pRooms) {
        this.name = name;
        this.rooms.addAll(pRooms);

        this.spawnRoom = this.rooms.get(0);
    }

    public String getName() {
        return name;
    }

    public Room getSpawnRoom() {
        return spawnRoom;
    }
}
