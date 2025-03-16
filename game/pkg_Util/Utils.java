package game.pkg_Util;

import game.pkg_Entity.FacingDirection;
import game.pkg_Object.TileStateWithPos;
import game.pkg_Object.Vector2;
import game.pkg_Room.Room;
import game.pkg_Tile.Tile;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static final int TEXTURE_WIDTH = 64;
    public static final int TEXTURE_HEIGHT = 64;

    public static final double COLLISION_RADIUS = 1;

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
                point.x() - (point.x() % Utils.TEXTURE_WIDTH),
                point.y() - (point.y() % Utils.TEXTURE_HEIGHT)
        );
    }

    public static TileStateWithPos[] getCollideTiled(Rectangle2D rigidbody2D, Room room) {
        List<TileStateWithPos> result = new ArrayList<>();
        Vector2 startAtTiledPoint = getTiledPoint(new Vector2((int) rigidbody2D.getX(), (int) rigidbody2D.getY()));
        for (int x = (int) startAtTiledPoint.x(); x <= (int) rigidbody2D.getMaxX(); x += Utils.TEXTURE_WIDTH) {
            for (int y = (int) startAtTiledPoint.y(); y <= (int) rigidbody2D.getMaxY(); y += Utils.TEXTURE_HEIGHT) {
                TileStateWithPos tileStateWithPos = room.getHigherTileAt(new Vector2(x, y));
                if (tileStateWithPos != null) {
                    result.add(tileStateWithPos);
                }
            }
        }

        return result.toArray(new TileStateWithPos[0]);
    }

    public static double distance(Shape shape1, Shape shape2) {
        // Créer des zones pour les deux formes
        Area area1 = new Area(shape1);
        Area area2 = new Area(shape2);

        // Vérifier si les formes se chevauchent
        area1.intersect(area2);
        if (!area1.isEmpty()) {
            // Si les formes se chevauchent, retourner une distance négative
            return -area1.getBounds2D().getWidth() * area1.getBounds2D().getHeight();
        }

        // Si les formes ne se chevauchent pas, calculer la distance minimale entre elles
        Rectangle2D bounds1 = shape1.getBounds2D();
        Rectangle2D bounds2 = shape2.getBounds2D();

        double dx = Math.max(bounds1.getMinX(), bounds2.getMinX()) - Math.min(bounds1.getMaxX(), bounds2.getMaxX());
        double dy = Math.max(bounds1.getMinY(), bounds2.getMinY()) - Math.min(bounds1.getMaxY(), bounds2.getMaxY());

        if (dx < 0 && dy < 0) {
            return Math.sqrt(dx * dx + dy * dy);
        } else {
            return Math.max(dx, dy);
        }
    }

    public static Vector2 distance(Shape shape, Vector2 pos) {
        Rectangle2D bounds = shape.getBounds2D();

        double dx = bounds.getMinX() - pos.x(); // Si il est a gauche en x du shape
        double dy = bounds.getMinY() - pos.y(); // Si il est a gauche en y du shape

        if (dx < 0) { // Si c'est négatif, il est à droite du shape en x
            dx = pos.x() - bounds.getMaxX();
        }
        if (dy < 0) { // Si c'est négatif, il est à droite du shape en x
            dy = pos.y() - bounds.getMaxY();
        }

        return new Vector2(dx, dy);
    }

    public static double distance(Rectangle2D shape1, Rectangle2D shape2, FacingDirection direction) {
        return switch (direction) {
            case NORTH -> shape2.getMinY() - shape1.getMinY();
            case SOUTH -> shape1.getMaxY() - shape2.getMaxY();
            case EAST -> shape2.getMinX() - shape1.getMinX();
            case WEST -> shape1.getMaxX() - shape2.getMaxX();
        };
    }

    public static Rectangle2D getRealPointWithoutTile(Rectangle2D shape) {
        return new Rectangle(
                (int) (shape.getX() % Utils.TEXTURE_WIDTH),
                (int) (shape.getY() % Utils.TEXTURE_HEIGHT),
                (int) shape.getWidth(),
                (int) shape.getHeight()
        );
    }
}
