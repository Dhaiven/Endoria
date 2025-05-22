package game.pkg_Object;

public record Vector2i(int x, int y) {

    public Vector2i add(Vector2i other) {
        return new Vector2i(x + other.x, y + other.y);
    }

    public Vector2i pow(Vector2i other) {
        return new Vector2i(x * other.x, y * other.y);
    }
}