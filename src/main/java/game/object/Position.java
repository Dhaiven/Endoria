package game.object;

import game.world.World;

import java.util.Objects;

public record Position(double x, double y, World world) {

    public Position(Vector2 vector, World world) {
        this(vector.x(), vector.y(), world);
    }

    public Position(Vector2i vector, World world) {
        this(vector.x(), vector.y(), world);
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
        return Objects.hash(x(), y(), world);
    }
}
