package game.object;

import game.util.Utils;

public record Cell(int column, int row) {

    public Vector2i toPixel() {
        return toPixel(Utils.TEXTURE_SIZE);
    }

    public Vector2i toPixel(Vector2i tileSize) {
        return new Vector2i(column * tileSize.x(), row * tileSize.y());
    }
}
