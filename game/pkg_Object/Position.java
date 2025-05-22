package game.pkg_Object;

import game.pkg_Room.Room;

import java.util.Objects;

public record Position(double x, double y, Room room) {

    public Position(Vector2 vector, Room room) {
        this(vector.x(), vector.y(), room);
    }

    public Position(Vector2i vector, Room room) {
        this(vector.x(), vector.y(), room);
    }

    public Vector2 vector2() {
        return new Vector2(x, y);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Position position)) return false;
        return Double.compare(x(), position.x()) == 0 && Double.compare(y(), position.y()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x(), y(), room());
    }
}
