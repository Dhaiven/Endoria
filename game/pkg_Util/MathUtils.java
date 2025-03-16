package game.pkg_Util;

import game.pkg_Entity.FacingDirection;
import game.pkg_Object.Position;
import game.pkg_Object.Vector2;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class MathUtils {

    public static Vector2 getIntersection(Line2D rayCasting, Shape shape) {
        if (shape instanceof Line2D) {
            return intersectSegment((Line2D) shape, rayCasting);
        } else if (shape instanceof Polygon) {
            System.out.println("shape instanceof Polygon");
            return intersectSegmentPolygon(rayCasting, (Polygon) shape);
        } else if (shape instanceof Rectangle) {
            return intersectSegmentRectangle(rayCasting, (Rectangle) shape);
        } else if (shape instanceof Ellipse2D) {
            return null; //TODO: intersectSegmentCircle(rayCasting, ((Ellipse2D) shape));
        }

        return intersectSegmentRectangle(rayCasting, shape.getBounds2D());
    }

    public static Vector2 intersectSegmentRectangle(Line2D segment, Rectangle2D rect) {
        double xMin = rect.getMinX();
        double yMin = rect.getMinY();
        double xMax = rect.getMaxX();
        double yMax = rect.getMaxY();

        boolean segmentInRect = rect.contains(segment.getX1(), segment.getY1());

        Line2D[] rectangleEdges = new Line2D[]{
                new Line2D.Double(xMin, yMin, xMax, yMin), // Bas
                new Line2D.Double(xMax, yMin, xMax, yMax), // Droite
                new Line2D.Double(xMax, yMax, xMin, yMax), // Haut
                new Line2D.Double(xMin, yMax, xMin, yMin)  // Gauche
        };

        double minT = Double.POSITIVE_INFINITY;

        for (Line2D edge : rectangleEdges) {
            Vector2 t = intersectSegment(segment, edge);
            if (t != null) { // t doit être valide
                return t;
            }
        }

        return null;
    }

    public static Double distance(Line2D segment, Line2D segmentRect) {
        Vector2 intersection = intersectSegment(segment, segmentRect);
        if (intersection == null) return null;

        var rayOrigin = segment.getP1();

        return Math.sqrt(Math.pow(intersection.x() - rayOrigin.getX(), 2) + Math.pow(intersection.y() - rayOrigin.getY(), 2));
    }

    /**
     * Calcule l'intersection entre deux segments de droite.
     * Utilise la méthode des vecteurs directionnels pour simplifier le calcul.
     *
     * @param line1 Premier segment de droite
     * @param line2 Deuxième segment de droite
     * @return Le paramètre t qui représente la position de l'intersection sur le premier segment,
     *         ou -1 si il n'y a pas d'intersection (segments parallèles ou non croisés).
     */
    public static Vector2 intersectSegment(Line2D segment1, Line2D segment2) {
        double x1 = segment1.getX1(), y1 = segment1.getY1();
        double x2 = segment1.getX2(), y2 = segment1.getY2();
        double x3 = segment2.getX1(), y3 = segment2.getY1();
        double x4 = segment2.getX2(), y4 = segment2.getY2();

        double denom = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        // Si denom == 0, les segments sont parallèles ou colinéaires
        if (Math.abs(denom) < 1e-10) {
            return null;
        }

        double px = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / denom;
        double py = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / denom;

        Vector2 intersection = new Vector2(px, py);

        // Vérifie si l'intersection est bien sur les segments (et pas en dehors)
        if (!segment1.intersectsLine(segment2)) {
            return null;
        }

        return intersection;
    }


    /**
     * Vérifie si un segment intersecte un cercle.
     *
     * Formule : Calcul de l'intersection d'un segment avec un cercle en résolvant une équation quadratique.
     * On vérifie les valeurs de t1 et t2, et si elles sont dans [0, 1], alors il y a une intersection.
     *
     * Discriminant = b^2 - 4ac
     * t1 = (-b - sqrt(discriminant)) / 2a
     * t2 = (-b + sqrt(discriminant)) / 2a
     */
    public static double intersectSegmentCircle(Line2D segment, Ellipse2D circle) {
        // Récupère les coordonnées du cercle
        double cx = circle.getCenterX();
        double cy = circle.getCenterY();
        double radius = circle.getWidth() / 2;  // Le rayon du cercle est la moitié de la largeur (ou de la hauteur)

        // Décomposition du segment (p1 et p2)
        double x1 = segment.getX1();
        double y1 = segment.getY1();
        double x2 = segment.getX2();
        double y2 = segment.getY2();

        // Calcul des coefficients de l'équation paramétrique du segment
        double dx = x2 - x1;
        double dy = y2 - y1;

        // Calcul de la racine du discriminant pour trouver l'intersection
        double a = dx * dx + dy * dy;
        double b = 2 * (dx * (x1 - cx) + dy * (y1 - cy));
        double c = (x1 - cx) * (x1 - cx) + (y1 - cy) * (y1 - cy) - radius * radius;

        // Calcul du discriminant
        double discriminant = b * b - 4 * a * c;

        // Si le discriminant est négatif, il n'y a pas d'intersection
        if (discriminant < 0) {
            return -1; // Aucune intersection
        }

        // Si le discriminant est positif, il y a 2 intersections possibles
        double sqrtDiscriminant = Math.sqrt(discriminant);

        // Calcul des deux solutions de l'équation quadratique
        double t1 = (-b - sqrtDiscriminant) / (2 * a);
        double t2 = (-b + sqrtDiscriminant) / (2 * a);

        // Si t1 et t2 sont dans l'intervalle [0, 1], on considère qu'il y a une intersection
        if (t1 >= 0 && t1 <= 1) {
            return t1;  // On retourne le premier point d'intersection
        } else if (t2 >= 0 && t2 <= 1) {
            return t2;  // On retourne le second point d'intersection
        }

        // Si aucune des solutions n'est dans l'intervalle [0, 1], il n'y a pas d'intersection sur le segment
        return -1; // Aucune intersection sur le segment
    }

    public static Vector2 intersectSegmentPolygon(Line2D segment, Polygon polygon) {
        // Nombre de points dans le polygone
        int n = polygon.npoints;

        // Vérifie l'intersection du segment avec chaque côté du polygone
        for (int i = 0; i < n; i++) {
            // Récupère les coordonnées des points successifs pour chaque côté du polygone
            int x1 = polygon.xpoints[i];
            int y1 = polygon.ypoints[i];
            int x2 = polygon.xpoints[(i + 1) % n];  // Le prochain point (en boucle)
            int y2 = polygon.ypoints[(i + 1) % n];

            // Crée le segment représentant le côté du polygone
            Line2D polygonEdge = new Line2D.Double(x1, y1, x2, y2);

            // Vérifie l'intersection entre le segment et le côté du polygone
            Vector2 t = intersectSegment(segment, polygonEdge);
            if (t != null) {
                return t; // Retourne le paramètre t de l'intersection
            }
        }

        // Si aucune intersection n'est trouvée
        return null; // Aucune intersection avec le polygone
    }

    /**
     * Calcule la distance entre deux points.
     *
     * Formule : La distance euclidienne entre deux points (x1, y1) et (x2, y2) est donnée par :
     * distance = sqrt((x2 - x1)^2 + (y2 - y1)^2)
     */
    public static double distance(Vector2 p1, Vector2 p2) {
        return Math.sqrt((p2.x() - p1.x()) * (p2.x() - p1.x()) + (p2.y() - p1.y()) * (p2.y() - p1.y()));
    }

    public static double distance(Vector2 point, Rectangle2D shape) {
        double cx = Math.max(shape.getMinX(), Math.min(point.x(), shape.getMaxX()));
        double cy = Math.max(shape.getMinY(), Math.min(point.y(), shape.getMaxY()));
        return Math.hypot(point.x() - cx, point.y() - cy);
    }

    public static double distance(Vector2 point, Ellipse2D shape) {
        double cx = shape.getCenterX();
        double cy = shape.getCenterY();
        double rx = shape.getWidth() / 2.0;
        double ry = shape.getHeight() / 2.0;

        double dx = (point.x() - cx) / rx;
        double dy = (point.y() - cy) / ry;
        double distance = Math.hypot(dx, dy);
        return (distance - 1) * Math.min(rx, ry);
    }

    public static double distance(Vector2 point, Line2D shape) {
        double x1 = shape.getX1(), y1 = shape.getY1();
        double x2 = shape.getX2(), y2 = shape.getY2();

        double dx = x2 - x1;
        double dy = y2 - y1;
        double lengthSquared = dx * dx + dy * dy;
        if (lengthSquared == 0) return Math.hypot(point.x() - x1, point.y() - y1);

        double t = ((point.x() - x1) * dx + (point.y() - y1) * dy) / lengthSquared;
        t = Math.max(0, Math.min(1, t));

        double closestX = x1 + t * dx;
        double closestY = y1 + t * dy;

        return Math.hypot(point.x() - closestX, point.y() - closestY);
    }

    public static double distance(Vector2 point, Polygon shape) {
        if (shape.contains(point.x(), point.y())) {
            return 0;
        }

        double minDistance = Double.MAX_VALUE;
        int[] xPoints = shape.xpoints;
        int[] yPoints = shape.ypoints;
        int nPoints = shape.npoints;

        for (int i = 0; i < nPoints; i++) {
            int j = (i + 1) % nPoints;
            Line2D edge = new Line2D.Double(xPoints[i], yPoints[i], xPoints[j], yPoints[j]);
            double dist = distance(point, edge);
            minDistance = Math.min(minDistance, dist);
        }

        return minDistance;
    }

    public static double distance(Rectangle2D rect, FacingDirection direction, Shape collision) {
        if (collision instanceof Line2D line2D) {
            return distance(rect, line2D, direction);
        } else if (collision instanceof Polygon polygon) {
            System.out.println("shape instanceof Polygon");
            return distance(rect, polygon, direction);
        } else if (collision instanceof Rectangle rectangle) {
            return  distance(rect, rectangle, direction);
        } else if (collision instanceof Ellipse2D) {
            return 0d; //TODO: intersectSegmentCircle(rayCasting, ((Ellipse2D) shape));
        }

        return distance(rect, collision.getBounds2D(), direction);
    }

    /**
     * Calcule la distance entre deux points.
     *
     * Formule : La distance euclidienne entre deux points (x1, y1) et (x2, y2) est donnée par :
     * distance = sqrt((x2 - x1)^2 + (y2 - y1)^2)
     */
    public static double distance(Point2D p1, Point2D p2) {
        return Math.sqrt((p2.getX() - p1.getX()) * (p2.getX() - p1.getX()) + (p2.getY() - p1.getY()) * (p2.getY() - p1.getY()));
    }

    public static double signedDistance(Point2D p1, Point2D p2, Vector2 direction) {
        double dx = p2.getX() - p1.getX();
        double dy = p2.getY() - p1.getY();

        double distance = Math.sqrt(dx * dx + dy * dy);

        // Produit scalaire pour vérifier si p2 est dans la direction souhaitée
        double dotProduct = dx * direction.x() + dy * direction.y();

        return dotProduct >= 0 ? distance : -distance;
    }

    public static FacingDirection getClosestDirection(Rectangle2D rect1, Rectangle2D rect2) {
        double dxLeft = Math.abs(rect1.getMinX() - rect2.getMaxX()); // Distance de rect1 à la gauche de rect2
        double dxRight = Math.abs(rect1.getMaxX() - rect2.getMinX()); // Distance de rect1 à la droite de rect2
        double dyTop = Math.abs(rect1.getMinY() - rect2.getMaxY()); // Distance de rect1 au-dessus de rect2
        double dyBottom = Math.abs(rect1.getMaxY() - rect2.getMinY()); // Distance de rect1 en dessous de rect2

        // Trouver la direction avec la plus petite distance
        double minDistance = Math.min(Math.min(dxLeft, dxRight), Math.min(dyTop, dyBottom));

        if (minDistance == dxLeft) return FacingDirection.WEST;
        if (minDistance == dxRight) return FacingDirection.EAST;
        if (minDistance == dyTop) return FacingDirection.NORTH;
        return FacingDirection.SOUTH;
    }

    public static FacingDirection getClosestDirection(Line2D line1, Line2D line2, Vector2 vector2) {
        // Centres des lignes
        double x1 = (line1.getX1() + line1.getX2()) / 2;
        double y1 = (line1.getY1() + line1.getY2()) / 2;
        double x2 = (line2.getX1() + line2.getX2()) / 2;
        double y2 = (line2.getY1() + line2.getY2()) / 2;

        // Différences en X et Y
        double dx = (x2 - x1) * Math.abs(vector2.x());
        double dy = (y2 - y1) * Math.abs(vector2.y());

        System.out.println("if: " + (Math.abs(dx) <=     Math.abs(dy)));

        // Comparaison absolue des distances pour déterminer la direction
        if (Math.abs(dx) > Math.abs(dy)) {
            return dy > 0 ? FacingDirection.SOUTH : FacingDirection.NORTH;
        } else {
            return dx > 0 ? FacingDirection.EAST : FacingDirection.WEST;
        }
    }


    public static Double distance(Line2D rect, Rectangle2D shape, FacingDirection direction) {
        Line2D edge = switch (direction) {
            case SOUTH -> new Line2D.Double(shape.getX(), shape.getY(), shape.getMaxX(), shape.getY());
            case NORTH -> new Line2D.Double(shape.getX(), shape.getMaxY(), shape.getMaxX(), shape.getMaxY());
            case WEST -> new Line2D.Double(shape.getMaxX(), shape.getY(), shape.getMaxX(), shape.getMaxY());
            case EAST -> new Line2D.Double(shape.getX(), shape.getY(), shape.getX(), shape.getMaxY());
        };


        System.out.println("getClosestDirection=" + getClosestDirection(rect, edge, direction.getVector()));
        if (getClosestDirection(rect, edge, direction.getVector()).getOpposite() == direction) {
            /**if (edge.getY1() > rect.getY1() && FacingDirection.NORTH.equals(direction)) {
             return null;
             } else if (edge.getY1() > rect.getY1() && FacingDirection.SOUTH.equals(direction)) {
             return null;
             } else if (edge.getX1() > rect.getX1() && FacingDirection.EAST.equals(direction)) {
             return null;
             } else if (edge.getX1() < rect.getX1() && FacingDirection.WEST.equals(direction)) {
             System.out.println("edge.getX1() = " + edge.getX1() +  ", rect.getX1() = " + rect.getX1());
             return null;
             }*/

            double x1 = (rect.getX1() + rect.getX2()) / 2;
            double y1 = (rect.getY1() + rect.getY2()) / 2;
            double x2 = (edge.getX1() + edge.getX2()) / 2;
            double y2 = (edge.getY1() + edge.getY2()) / 2;

            Point2D point1 = new Point2D.Double(x1, y1);
            Point2D point2 = new Point2D.Double(x2, y2);
            double distance = signedDistance(point1, point2, direction.getVector());
            System.out.println("distance: " + distance);
            System.out.println("direction: " + direction);
            /**if (direction == FacingDirection.SOUTH && distance > 0) {
             return null;
             }
             if (direction == FacingDirection.NORTH && distance < 0) {
             return null;
             }
             if (direction == FacingDirection.EAST && distance > 0) {
             return null;
             }
             if (direction == FacingDirection.WEST && distance < 0) {
             return null;
             }*/

            return distance;
        }
        return null;
    }

    public static double distance(Rectangle2D rect, Rectangle2D shape, FacingDirection direction) {
        double dx = Math.max(0, Math.max(shape.getMinX() - rect.getMaxX(), rect.getMinX() - shape.getMaxX()));
        double dy = Math.max(0, Math.max(shape.getMinY() - rect.getMaxY(), rect.getMinY() - shape.getMaxY()));

        return filterDistanceByDirection(dx, dy, direction);
    }

    public static double distance(Rectangle2D rect, Ellipse2D shape, FacingDirection direction) {
        return rect.intersects(shape.getBounds2D()) ? 0 : distance(rect, shape.getBounds2D(), direction);
    }

    public static double distance(Rectangle2D rect, Line2D shape, FacingDirection direction) {
        double minDistance = Double.MAX_VALUE;

        // On teste les quatre coins du rectangle pour déterminer la distance minimale vers la ligne.
        minDistance = Math.min(minDistance, shape.ptSegDist(rect.getMinX(), rect.getMinY()));
        minDistance = Math.min(minDistance, shape.ptSegDist(rect.getMinX(), rect.getMaxY()));
        minDistance = Math.min(minDistance, shape.ptSegDist(rect.getMaxX(), rect.getMinY()));
        minDistance = Math.min(minDistance, shape.ptSegDist(rect.getMaxX(), rect.getMaxY()));

        return filterDistanceByDirection(minDistance, minDistance, direction);
    }

    public static double distance(Rectangle2D rect, Polygon shape, FacingDirection direction) {
        double minDistance = Double.MAX_VALUE;
        int[] xPoints = shape.xpoints;
        int[] yPoints = shape.ypoints;
        int nPoints = shape.npoints;

        for (int i = 0; i < nPoints; i++) {
            int j = (i + 1) % nPoints;
            Line2D edge = new Line2D.Double(xPoints[i], yPoints[i], xPoints[j], yPoints[j]);
            double edgeDistance = distance(rect, edge, direction);
            minDistance = Math.min(minDistance, edgeDistance);
        }

        return minDistance;
    }

    private static double filterDistanceByDirection(double dx, double dy, FacingDirection direction) {
        return switch (direction) {
            case NORTH -> dy > 0 ? dy : Double.MAX_VALUE;
            case SOUTH -> dy < 0 ? -dy : Double.MAX_VALUE;
            case EAST -> dx > 0 ? dx : Double.MAX_VALUE;
            case WEST -> dx < 0 ? -dx : Double.MAX_VALUE;
        };
    }
}


