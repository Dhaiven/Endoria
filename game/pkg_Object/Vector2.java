package game.pkg_Object;

public record Vector2(double x, double y) {

    public Vector2 add(Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }
}
