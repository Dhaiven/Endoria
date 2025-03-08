package game.pkg_Util;

import game.pkg_Entity.FacingDirection;
import game.pkg_Object.Vector2;

import java.awt.*;

public class Utils {

    public static final int TEXTURE_WIDTH = 64;
    public static final int TEXTURE_HEIGHT = 64;

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

    public static boolean isPointInRange(Vector2 point1, Vector2 point2) {
        // Vérifie si x2 est dans la plage [x1, x1 + 32]
        boolean isXInRange = point2.x() >= point1.x() && point2.x() <= point1.x() + 32;

        // Vérifie si y2 est dans la plage [y1, y1 + 32]
        boolean isYInRange = point2.y() >= point1.y() && point2.y() <= point1.y() + 32;

        // Retourne true si les deux conditions sont vraies
        return isXInRange && isYInRange;
    }

    public static Vector2 getTiledPoint(Vector2 point) {
        return new Vector2(
                point.x() - (point.x() % Utils.TEXTURE_HEIGHT),
                point.y() - (point.y() % Utils.TEXTURE_WIDTH)
        );
    }

    public static Vector2 getRealPointWithoutTile(Vector2 point) {
        return new Vector2(
                point.x() % Utils.TEXTURE_WIDTH,
                point.y() % Utils.TEXTURE_HEIGHT
        );
    }
}
