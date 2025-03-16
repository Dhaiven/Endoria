package game.pkg_Object;

import game.pkg_Room.Room;

public record Position(double x, double y, Room room) {

    public Position(Vector2 vector, Room room) {
        this(vector.x(), vector.y(), room);
    }

    public Vector2 vector2() {
        return new Vector2(x, y);
    }
}
