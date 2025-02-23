package game.pkg_Util;

import game.pkg_Entity.FacingDirection;
import game.pkg_Object.Vector2;

import java.awt.*;

public class Utils {

    public static final int TEXTURE_WIDTH = 32;
    public static final int TEXTURE_HEIGHT = 32;

    public static FacingDirection getDirection(Shape shapeFrom, Shape shapeTo) {
        Rectangle boundsFrom = shapeFrom.getBounds();
        Rectangle boundsTo = shapeTo.getBounds();

        int xFrom = boundsFrom.x + boundsFrom.width / 2;
        int yFrom = boundsFrom.y + boundsFrom.height / 2;
        int xTo = boundsTo.x + boundsTo.width / 2;
        int yTo = boundsTo.y + boundsTo.height / 2;

        int dx = xTo - xFrom;
        int dy = yTo - yFrom;

        if (Math.abs(dx) > Math.abs(dy)) {
            return (dx > 0) ? FacingDirection.EAST : FacingDirection.WEST;
        }

        return (dy > 0) ? FacingDirection.SOUTH : FacingDirection.NORTH;
    }

    public static Vector2 getCenterPosition(Shape shape, FacingDirection direction) {
        Rectangle bounds = shape.getBounds();

        return switch (direction) {
            case NORTH -> new Vector2(bounds.x + bounds.width / 2, bounds.y);
            case SOUTH -> new Vector2(bounds.x + bounds.width / 2, bounds.y + bounds.height);
            case EAST -> new Vector2(bounds.x + bounds.width, bounds.y + bounds.height / 2);
            case WEST -> new Vector2(bounds.x, bounds.y + bounds.height / 2);
        };
    }
}
