package game.pkg_Object;

import game.pkg_Room.Room;
import game.pkg_World.World;

public class Position {

    private final int x;
    private final int y;
    private final int layer;
    private final Room room;

    public Position(int x, int y, int layer, Room room) {
        this.x = x;
        this.y = y;
        this.layer = layer;
        this.room = room;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getLayer() {
        return layer;
    }

    public Room getRoom() {
        return room;
    }

    public World getWorld() {
        return room.getWorld();
    }
}
