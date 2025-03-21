package game.pkg_Util.pkg_Message.options;

import game.pkg_Util.pkg_Message.Message;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public class FontOption extends Options {
    protected final Font font;

    protected Font graphicsFontCache = null;

    public FontOption(Font font) {
        this.font = font;
    }

    @Override
    public Font get() {
        return font;
    }

    @Override
    public void before(Message message, Graphics2D graphics, Rectangle rect) {
        graphicsFontCache = graphics.getFont();
        graphics.setFont(font);
    }

    @Override
    public void after(Message message, Graphics2D graphics, Rectangle rect) {
        if (graphicsFontCache != null) {
            graphics.setFont(graphicsFontCache);
        }
    }
}
