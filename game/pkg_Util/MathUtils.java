package game.pkg_Util;

import game.pkg_Entity.FacingDirection;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class MathUtils {

    public static Double distance(Rectangle2D playerRigidbody, Shape obstacle, FacingDirection direction) {
        if (obstacle instanceof Polygon polygon) {
            return distance(playerRigidbody, polygon, direction);
        } else if (obstacle instanceof Rectangle rectangle) {
            return distance(playerRigidbody, rectangle, direction);
        }

        return distance(playerRigidbody, obstacle.getBounds(), direction);
    }

    /**
     * Calcule la distance entre le rigidbody du joueur et un obstacle dans une direction donnée.
     * Retourne null si l'obstacle n'est pas dans la direction du mouvement.
     *
     * @param playerRigidbody Le Rectangle2D représentant le rigidbody du joueur
     * @param obstacle Le Rectangle représentant l'obstacle
     * @param direction La direction dans laquelle le joueur se déplace
     * @return La distance jusqu'à la collision ou null si l'obstacle n'est pas dans la direction spécifiée
     */
    public static Double distance(Rectangle2D playerRigidbody, Rectangle obstacle, FacingDirection direction) {
        // Vérifier que l'obstacle est dans la direction du mouvement et qu'il y a chevauchement sur l'autre axe
        switch (direction) {
            case NORTH:
                // Vérifier si l'obstacle est au nord et s'il y a chevauchement horizontal
                if (obstacle.getMaxY() <= playerRigidbody.getMinY() &&
                        rectanglesOverlapHorizontally(playerRigidbody, obstacle)) {
                    return playerRigidbody.getMinY() - obstacle.getMaxY();
                }
                break;
            case SOUTH:
                // Vérifier si l'obstacle est au sud et s'il y a chevauchement horizontal
                if (obstacle.getMinY() >= playerRigidbody.getMaxY() &&
                        rectanglesOverlapHorizontally(playerRigidbody, obstacle)) {
                    return obstacle.getMinY() - playerRigidbody.getMaxY();
                }
                break;
            case WEST:
                // Vérifier si l'obstacle est à l'ouest et s'il y a chevauchement vertical
                if (obstacle.getMaxX() <= playerRigidbody.getMinX() &&
                        rectanglesOverlapVertically(playerRigidbody, obstacle)) {
                    return playerRigidbody.getMinX() - obstacle.getMaxX();
                }
                break;
            case EAST:
                // Vérifier si l'obstacle est à l'est et s'il y a chevauchement vertical
                if (obstacle.getMinX() >= playerRigidbody.getMaxX() &&
                        rectanglesOverlapVertically(playerRigidbody, obstacle)) {
                    return obstacle.getMinX() - playerRigidbody.getMaxX();
                }
                break;
        }

        return null; // Pas dans la bonne direction ou pas de chevauchement sur l'autre axe
    }

    /**
     * Calcule la distance entre le rigidbody du joueur et un polygone dans une direction donnée.
     * Retourne null si le polygone n'est pas dans la direction du mouvement.
     *
     * @param playerRigidbody Le Rectangle2D représentant le rigidbody du joueur
     * @param polygon Le Polygon avec lequel on vérifie la collision
     * @param direction La direction dans laquelle le joueur se déplace
     * @return La distance jusqu'à la collision ou null si le polygone n'est pas dans la direction spécifiée
     */
    public static Double distance(Rectangle2D playerRigidbody, Polygon polygon, FacingDirection direction) {
        // Calculer les limites du polygone
        Rectangle polyBounds = polygon.getBounds();

        // Vérifier d'abord si le polygone est dans la bonne direction
        if (!isPolygonInDirection(playerRigidbody, polyBounds, direction)) {
            return null; // Pas dans la bonne direction
        }

        // Récupérer tous les points du polygone
        int numPoints = polygon.npoints;
        Point2D[] points = new Point2D[numPoints];
        for (int i = 0; i < numPoints; i++) {
            points[i] = new Point2D.Double(polygon.xpoints[i], polygon.ypoints[i]);
        }

        // Si le polygone est dans la bonne direction, calculer la distance minimale
        double minDistance = Double.MAX_VALUE;
        boolean foundValidEdge = false;

        // Pour chaque paire de points consécutifs (formant une arête du polygone)
        for (int i = 0; i < numPoints; i++) {
            int j = (i + 1) % numPoints; // Pour boucler au dernier point
            Line2D edge = new Line2D.Double(points[i], points[j]);

            // Vérifier si cette arête est dans la direction du mouvement et peut être "vue" par le raycast
            if (isEdgeInDirection(edge, playerRigidbody, direction)) {
                double edgeDistance = getDistanceToEdge(playerRigidbody, edge, direction);
                if (edgeDistance >= 0 && edgeDistance < minDistance) {
                    minDistance = edgeDistance;
                    foundValidEdge = true;
                }
            }
        }

        return foundValidEdge ? minDistance : null;
    }

    /**
     * Vérifie si les limites du polygone sont dans la direction spécifiée par rapport au rigidbody
     */
    private static boolean isPolygonInDirection(Rectangle2D playerRigidbody, Rectangle polyBounds, FacingDirection direction) {
        return switch (direction) {
            case NORTH -> polyBounds.getMaxY() <= playerRigidbody.getMinY() &&
                    rectanglesOverlapHorizontally(playerRigidbody, polyBounds);
            case SOUTH -> polyBounds.getMinY() >= playerRigidbody.getMaxY() &&
                    rectanglesOverlapHorizontally(playerRigidbody, polyBounds);
            case WEST -> polyBounds.getMaxX() <= playerRigidbody.getMinX() &&
                    rectanglesOverlapVertically(playerRigidbody, polyBounds);
            case EAST -> polyBounds.getMinX() >= playerRigidbody.getMaxX() &&
                    rectanglesOverlapVertically(playerRigidbody, polyBounds);
        };
    }

    /**
     * Vérifie si une arête du polygone est dans la direction spécifiée
     */
    private static boolean isEdgeInDirection(Line2D edge, Rectangle2D playerRigidbody, FacingDirection direction) {
        // Récupérer les coordonnées de l'arête
        double x1 = edge.getX1();
        double y1 = edge.getY1();
        double x2 = edge.getX2();
        double y2 = edge.getY2();

        return switch (direction) {
            case NORTH ->
                // Arête horizontale ou oblique au nord du joueur
                    (y1 <= playerRigidbody.getMinY() && y2 <= playerRigidbody.getMinY());
            case SOUTH ->
                // Arête horizontale ou oblique au sud du joueur
                    (y1 >= playerRigidbody.getMaxY() && y2 >= playerRigidbody.getMaxY());
            case WEST ->
                // Arête verticale ou oblique à l'ouest du joueur
                    (x1 <= playerRigidbody.getMinX() && x2 <= playerRigidbody.getMinX());
            case EAST ->
                // Arête verticale ou oblique à l'est du joueur
                    (x1 >= playerRigidbody.getMaxX() && x2 >= playerRigidbody.getMaxX());
        };
    }

    /**
     * Calcule la distance entre le rigidbody et une arête dans une direction donnée
     */
    private static double getDistanceToEdge(Rectangle2D playerRigidbody, Line2D edge, FacingDirection direction) {
        // Point du rigidbody à partir duquel mesurer la distance
        double playerX = 0;
        double playerY = 0;

        // Point de l'arête le plus proche du rigidbody dans la direction spécifiée
        double edgeX = 0;
        double edgeY = 0;

        switch (direction) {
            case NORTH:
                playerY = playerRigidbody.getMinY();
                // Projeter le point du joueur sur l'arête horizontalement
                edgeY = Math.min(edge.getY1(), edge.getY2());
                return playerY - edgeY;
            case SOUTH:
                playerY = playerRigidbody.getMaxY();
                // Projeter le point du joueur sur l'arête horizontalement
                edgeY = Math.max(edge.getY1(), edge.getY2());
                return edgeY - playerY;
            case WEST:
                playerX = playerRigidbody.getMinX();
                // Projeter le point du joueur sur l'arête verticalement
                edgeX = Math.min(edge.getX1(), edge.getX2());
                return playerX - edgeX;
            case EAST:
                playerX = playerRigidbody.getMaxX();
                // Projeter le point du joueur sur l'arête verticalement
                edgeX = Math.max(edge.getX1(), edge.getX2());
                return edgeX - playerX;
        }

        return 0d;
    }

    /**
     * Projette un point sur une ligne
     */
    private static double projectPointOntoLine(double pointCoord, double otherCoord, Line2D line) {
        // Si la ligne est parfaitement horizontale ou verticale
        if (Math.abs(line.getY2() - line.getY1()) < 0.0001) {
            // Ligne horizontale, on retourne juste le X du point
            return pointCoord;
        } else if (Math.abs(line.getX2() - line.getX1()) < 0.0001) {
            // Ligne verticale, on retourne juste le Y du point
            return pointCoord;
        } else {
            // Ligne oblique, on calcule la projection
            double m = (line.getY2() - line.getY1()) / (line.getX2() - line.getX1());
            double b = line.getY1() - m * line.getX1();

            // Coordonnée X du point projeté
            double projX = (pointCoord + m * otherCoord - m * b) / (m * m + 1);

            // Vérifier si le point projeté est sur le segment
            if (projX < Math.min(line.getX1(), line.getX2())) {
                projX = Math.min(line.getX1(), line.getX2());
            } else if (projX > Math.max(line.getX1(), line.getX2())) {
                projX = Math.max(line.getX1(), line.getX2());
            }

            return projX;
        }
    }

    /**
     * Vérifie si deux rectangles se chevauchent horizontalement
     */
    private static boolean rectanglesOverlapHorizontally(Rectangle2D rect1, Rectangle2D rect2) {
        return !(rect1.getMaxX() <= rect2.getMinX() || rect1.getMinX() >= rect2.getMaxX());
    }

    /**
     * Vérifie si deux rectangles se chevauchent verticalement
     */
    private static boolean rectanglesOverlapVertically(Rectangle2D rect1, Rectangle2D rect2) {
        return !(rect1.getMaxY() <= rect2.getMinY() || rect1.getMinY() >= rect2.getMaxY());
    }
}
