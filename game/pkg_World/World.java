package game.pkg_World;

import game.pkg_Room.Room;
import game.pkg_Tile.Tile;

import java.util.*;
import java.util.List;

public class World {

    private final String name;
    private final Layers layers;
    private final List<Room> rooms = new ArrayList<>();

    private Room spawnRoom = new Room(this, "Test", Layers.builder().build());
    protected boolean isLoaded = false;

    public World(String name, Layers layers) {
        this.name = name;
        this.layers = layers;
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

    public Room getRommAt(int row, int column) {
        return this.getSpawnRoom();
    }

    public void setTile(Tile tile, int row, int column, int layer) {
        Room room = getRommAt(row, column);
        //TODO: fix row & column must be less room pos
        room.setTile(tile, row, column, layer);
    }
}
