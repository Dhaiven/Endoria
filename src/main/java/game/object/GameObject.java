package game.object;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;

import java.awt.Graphics2D;

public class GameObject extends Component {

    protected Texture sprite;

    public GameObject(Texture sprite) {
        this.sprite = sprite;
    }

    public Texture getTexture() {
        return sprite;
    }

    public int getWidth() {
        return 16;
    }

    public int getHeight() {
        return 16;
    }

    public boolean canPaint(Graphics2D g2d) {
        return true;
    }
}
