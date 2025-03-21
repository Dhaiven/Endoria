package game.pkg_Util.pkg_Message.options;

import game.pkg_Object.Vector2;
import game.pkg_Util.pkg_Message.Message;
import game.pkg_Util.pkg_Message.Placement;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;

public abstract class Background extends Options {
    protected Padding padding = Padding.zero();

    protected Map<Placement, Border> borders = new HashMap<>();

    public static BackgroundColor color(Color color) {
        return new BackgroundColor(color);
    }

    public static BackgroundImage image(Image image, Boolean resizable) {
        return new BackgroundImage(image, resizable);
    }

    public Background padding(Padding padding) {
        this.padding = padding;
        return this;
    }

    public Background border(Placement placement, Border border) {
        borders.put(placement, border);
        return this;
    }

    public static class BackgroundImage extends Background {
        protected Image image;
        protected Boolean resizable = false;

        public BackgroundImage(Image image, Boolean resizable) {
            this.image = image;
            this.resizable = resizable;
        }

        @Override
        public Image get() {
            return image;
        }

        @Override
        public void before(Message message, Graphics2D graphics, Rectangle rect) {
            var fontMetric = message.fontMetric(graphics);
            var fontRect = fontMetric.getStringBounds(message.text(), graphics).getBounds();
            var pos = message.getPos(graphics, rect);

            var rowSide = padding.x(new Side(pos.x(), (int) fontRect.getWidth()));
            var columnSide = padding.y(new Side(pos.y() - fontMetric.getAscent(), (int) fontRect.getHeight()));

            if (resizable) {
                //TODO: hints configurable
                image = image.getScaledInstance(rowSide.size(), columnSide.size(), Image.SCALE_DEFAULT);
            }

            graphics.drawImage(image,
                rowSide.start(),
                columnSide.start(),
                null, null
            );
        }
    }

    public static class BackgroundColor extends Background {
        protected Color color;
        protected Color cache = null;

        public BackgroundColor(Color color) {
            this.color = color;
        }

        @Override
        public Color get() {
            return color;
        }

        @Override
        public void before(Message message, Graphics2D graphics, Rectangle rect) {
            cache = graphics.getColor();
            graphics.setColor(color);

            var fontMetric = message.fontMetric(graphics);
            var fontRect = fontMetric.getStringBounds(message.text(), graphics).getBounds();
            var pos = message.getPos(graphics, rect);

            var rowSide = padding.x(new Side(pos.x(), (int) fontRect.getWidth()));
            var columnSide = padding.y(new Side(pos.y() - fontMetric.getAscent(), (int) fontRect.getHeight()));

            graphics.fillRect(
                rowSide.start(),
                columnSide.start(),
                rowSide.size(),
                columnSide.size()
            );

            graphics.setColor(cache);
        }
    }
}
