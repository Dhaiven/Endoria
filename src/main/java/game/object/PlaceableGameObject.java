package game.object;

import com.almasb.fxgl.texture.Texture;

public class PlaceableGameObject extends GameObject {

    protected Position position;
    protected int layer;

    public PlaceableGameObject(Texture sprite, Position position, int layer) {
        super(sprite);
        this.position = position;
        this.layer = layer;
    }

    public Position getPosition() {
        return position;
    }
}
