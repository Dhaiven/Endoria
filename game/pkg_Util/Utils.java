package game.pkg_Util;

import game.pkg_Object.Vector2;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

public class Utils {

    public static final int TEXTURE_WIDTH = 128;
    public static final int TEXTURE_HEIGHT = 128;

    public static final double COLLISION_RADIUS = 0.01;

    public static Vector2 getCenterPosition(Shape shape) {
        Rectangle2D bounds = shape.getBounds2D();
        return new Vector2(bounds.getCenterX(), bounds.getCenterY());
    }
}
