package game.pkg_Util;

import game.pkg_Entity.FacingDirection;
import game.pkg_Object.Vector2;
import game.pkg_Object.Vector2i;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class Utils {

    public static final Vector2i TEXTURE_SIZE = getTextureSize();

    public static final double COLLISION_RADIUS = 0.01;

    public static Vector2 getCenterPosition(Shape shape) {
        Rectangle2D bounds = shape.getBounds2D();
        return new Vector2(bounds.getCenterX(), bounds.getCenterY());
    }

    private static Vector2i getTextureSize() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        return new Vector2i(
                (int) (screenSize.getWidth() / 32),
                (int) (screenSize.getHeight() / 18)
        );
    }

    public static Vector2 getSidedPosition(Rectangle2D rectangle, FacingDirection direction) {
        return switch (direction) {
            case NORTH -> new Vector2(rectangle.getCenterX(), rectangle.getMinY());
            case SOUTH -> new Vector2(rectangle.getCenterX(), rectangle.getMaxY());
            case EAST  -> new Vector2(rectangle.getMaxX(), rectangle.getCenterY());
            case WEST  -> new Vector2(rectangle.getMinX(), rectangle.getCenterY());
        };
    }

    public static Line2D getRectangleSide(Rectangle2D rectangle, FacingDirection direction) {
        return switch (direction) {
            case NORTH -> new Line2D.Double(rectangle.getX(), rectangle.getY(), rectangle.getMaxX(), rectangle.getY());
            case SOUTH -> new Line2D.Double(rectangle.getX(), rectangle.getMaxY(), rectangle.getMaxX(), rectangle.getMaxY());
            case EAST  -> new Line2D.Double(rectangle.getMaxX(), rectangle.getY(), rectangle.getMaxX(), rectangle.getMaxY());
            case WEST  -> new Line2D.Double(rectangle.getX(), rectangle.getY(), rectangle.getX(), rectangle.getMaxY());
        };
    }

    public static Line2D getRayCastFromRectangleSide(Rectangle2D rectangle, FacingDirection direction, Vector2 deltaPosition) {
        Vector2 start = getSidedPosition(rectangle, direction);

        // Créer le segment de raycast
        return new Line2D.Double(
                start.x(),
                start.y(),
                start.x() + deltaPosition.x(),
                start.y() + deltaPosition.y()
        );
    }

    public static Vector2 convertToTileSystem(Vector2 position) {
        return new Vector2(
                Utils.TEXTURE_SIZE.x() * (int) (position.x() / Utils.TEXTURE_SIZE.x()),
                Utils.TEXTURE_SIZE.y() * (int) (position.y() / Utils.TEXTURE_SIZE.y())
        );
    }
}
