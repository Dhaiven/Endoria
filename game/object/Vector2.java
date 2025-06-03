package game.object;

import game.util.Utils;

public record Vector2(double x, double y) {

    public Vector2 add(Vector2 other) {
        return new Vector2(x + other.x, y + other.y);
    }

    public Vector2 pow(Vector2 other) {
        return new Vector2(x * other.x, y * other.y);
    }

    public Cell toCell() {
        return toCell(Utils.TEXTURE_SIZE);
    }

    public Cell toCell(Vector2i tileSize) {
        return new Cell((int) (x / tileSize.x()), (int) (y / tileSize.y()));
    }
}
