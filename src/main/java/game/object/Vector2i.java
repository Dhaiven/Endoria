package game.object;

import game.util.Utils;

public record Vector2i(int x, int y) {

    public Vector2i add(Vector2i other) {
        return new Vector2i(x + other.x, y + other.y);
    }

    public Vector2i pow(Vector2i other) {
        return new Vector2i(x * other.x, y * other.y);
    }

    public Vector2i divide(Vector2i other) {
        return new Vector2i(x / other.x, y / other.y);
    }

    public Cell toCell() {
        return toCell(Utils.TEXTURE_SIZE);
    }

    public Cell toCell(Vector2i tileSize) {
        return new Cell(x / tileSize.x(), y / tileSize.y());
    }
}