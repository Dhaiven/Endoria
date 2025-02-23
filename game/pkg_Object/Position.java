package game.pkg_Object;

import game.pkg_Room.Room;

public record Position(int x, int y, Room room) {

    public Position(Vector2 vector, Room room) {
        this(vector.x(), vector.y(), room);
    }
}
