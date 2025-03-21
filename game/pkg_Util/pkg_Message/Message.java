package game.pkg_Util.pkg_Message;

import game.pkg_Object.Vector2i;
import game.pkg_Util.pkg_Message.options.FontOption;
import game.pkg_Util.pkg_Message.options.Options;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import java.util.*;

public class Message {
    protected String text;
    protected Pos posType = null;
    protected Vector2i customPos = null;

    protected Map<Class<? extends Options>, Options> options = new HashMap<>();

    public Message(String text, Pos pos) {
        this.text = text;
        this.posType = pos;
    }

    public Message(String text, Vector2i pos) {
        this.text = text;
        this.customPos = pos;
    }

    public Message add(Options option) {
        options.put(option.getClass(), option);
        return this;
    }

    public String text() {
        return text;
    }

    @SuppressWarnings("unchecked")
    public <T extends Options> T option(Class<T> clazz) {
        return (T) options.get(clazz);
    }

    @SuppressWarnings("unchecked")
    public <T extends Options, V> V optionValue(Class<T> clazz, V defaultValue) {
        var option = option(clazz);
        var result = defaultValue;
        if (option != null) {
            result = (V) option.get();
        }

        return result;
    }

    public FontMetrics fontMetric(Graphics2D graphics) {
        Font font = optionValue(FontOption.class, graphics.getFont());
        return graphics.getFontMetrics(font);
    }

    public void draw(Graphics2D graphics, Rectangle rect) {
        for (var option : options.values()) {
            option.before(this, graphics, rect);
        }

        var pos = getPos(graphics, rect);
        graphics.drawString(text, pos.x(), pos.y());

        for (var option : options.values()) {
            option.after(this, graphics, rect);
        }
    }

    public Vector2i getPos(Graphics2D graphics, Rectangle rect) {
        if (posType == Pos.CENTER) {
            FontMetrics metrics = fontMetric(graphics);
            // Determine the X coordinate for the text
            int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
            // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
            int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
            return new Vector2i(x, y);
        } else if (posType == Pos.BOTTOM_RIGHT_HAND_CORNER) {
            FontMetrics metrics = fontMetric(graphics);
            // Determine the X coordinate for the text
            int x = rect.width - metrics.stringWidth(text);
            // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
            int y = rect.height - metrics.getHeight() - metrics.getAscent();
            return new Vector2i(x, y);
        }

        return customPos;
    }

    public enum Pos {
        CENTER,
        BOTTOM_RIGHT_HAND_CORNER;
    }
}
