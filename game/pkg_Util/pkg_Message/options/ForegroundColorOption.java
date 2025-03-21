package game.pkg_Util.pkg_Message.options;

import game.pkg_Util.pkg_Message.Message;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class ForegroundColorOption extends Options {
    protected final Color color;

    protected Color graphicsColorCache = null;

    public ForegroundColorOption(Color color) {
        this.color = color;
    }

    @Override
    public Color get() {
        return color;
    }

    @Override
    public void before(Message message, Graphics2D graphics, Rectangle rect) {
        graphicsColorCache = graphics.getColor();
        graphics.setColor(color);
    }

    @Override
    public void after(Message message, Graphics2D graphics, Rectangle rect) {
        if (graphicsColorCache != null) {
            graphics.setColor(graphicsColorCache);
        }
    }
}
