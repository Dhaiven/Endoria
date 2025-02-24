package game.pkg_World;

import game.pkg_Room.Room;

import java.util.ArrayList;
import java.util.List;

public class World {

    private final String name;
    private final Layers layers;
    private final List<Room> rooms = new ArrayList<>();

    private Room spawnRoom;

    public World(String name, List<Room> pRooms, Layers layers) {
        this.name = name;
        this.rooms.addAll(pRooms);
        this.layers = layers;

        this.spawnRoom = this.rooms.get(1);
    }

    public String getName() {
        return name;
    }

    public Room getSpawnRoom() {
        return spawnRoom;
    }

    public Layers getLayers() {
        return layers;
    }
}
