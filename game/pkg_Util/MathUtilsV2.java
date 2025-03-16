package game.pkg_Util;

import game.pkg_Entity.FacingDirection;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class MathUtilsV2 {

    /**
     * Constante d'epsilon pour gérer les imprécisions des calculs en virgule flottante
     */
    private static final double EPSILON = 0.0001;

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
        // Calculer les bords de l'obstacle
        Rectangle2D obstacleBounds = obstacle.getBounds2D();

        // Vérifier que l'obstacle est dans la direction du mouvement et qu'il y a chevauchement sur l'autre axe
        switch (direction) {
            case NORTH:
                // Vérifier si l'obstacle est au nord et s'il y a chevauchement horizontal
                if (obstacleBounds.getMaxY() <= playerRigidbody.getMinY() &&
                        rectanglesOverlapHorizontally(playerRigidbody, obstacleBounds)) {
                    return playerRigidbody.getMinY() - obstacleBounds.getMaxY();
                }
                break;
            case SOUTH:
                // Vérifier si l'obstacle est au sud et s'il y a chevauchement horizontal
                if (obstacleBounds.getMinY() >= playerRigidbody.getMaxY() &&
                        rectanglesOverlapHorizontally(playerRigidbody, obstacleBounds)) {
                    return obstacleBounds.getMinY() - playerRigidbody.getMaxY();
                }
                break;
            case WEST:
                // Vérifier si l'obstacle est à l'ouest et s'il y a chevauchement vertical
                if (obstacleBounds.getMaxX() <= playerRigidbody.getMinX() &&
                        rectanglesOverlapVertically(playerRigidbody, obstacleBounds)) {
                    return playerRigidbody.getMinX() - obstacleBounds.getMaxX();
                }
                break;
            case EAST:
                // Vérifier si l'obstacle est à l'est et s'il y a chevauchement vertical
                if (obstacleBounds.getMinX() >= playerRigidbody.getMaxX() &&
                        rectanglesOverlapVertically(playerRigidbody, obstacleBounds)) {
                    return obstacleBounds.getMinX() - playerRigidbody.getMaxX();
                }
                break;
        }

        return null; // Pas dans la bonne direction ou pas de chevauchement sur l'autre axe
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

    /**
     * Vérifie si une ligne (raycast) intersecte un rectangle.
     *
     * @param raycast Le raycast à vérifier
     * @param rectangle Le rectangle à vérifier
     * @return true si le raycast intersecte le rectangle, false sinon
     */
    private static boolean raycastIntersectsRectangle(Line2D raycast, Rectangle rectangle) {
        // Créer les quatre lignes du rectangle
        Line2D topLine = new Line2D.Double(rectangle.getMinX(), rectangle.getMinY(),
                rectangle.getMaxX(), rectangle.getMinY());

        Line2D rightLine = new Line2D.Double(rectangle.getMaxX(), rectangle.getMinY(),
                rectangle.getMaxX(), rectangle.getMaxY());

        Line2D bottomLine = new Line2D.Double(rectangle.getMaxX(), rectangle.getMaxY(),
                rectangle.getMinX(), rectangle.getMaxY());

        Line2D leftLine = new Line2D.Double(rectangle.getMinX(), rectangle.getMaxY(),
                rectangle.getMinX(), rectangle.getMinY());

        // Vérifier si le raycast intersecte l'une des lignes du rectangle
        return raycast.intersectsLine(topLine) ||
                raycast.intersectsLine(rightLine) ||
                raycast.intersectsLine(bottomLine) ||
                raycast.intersectsLine(leftLine);
    }

    /**
     * Vérifie si un obstacle chevauche le joueur horizontalement (pour les directions N/S)
     */
    private static boolean obstacleOverlapsHorizontally(Rectangle2D playerRigidbody, Rectangle2D obstacleBounds) {
        return !(obstacleBounds.getMaxX() < playerRigidbody.getMinX() + EPSILON ||
                obstacleBounds.getMinX() > playerRigidbody.getMaxX() - EPSILON);
    }

    /**
     * Vérifie si un obstacle chevauche le joueur verticalement (pour les directions E/O)
     */
    private static boolean obstacleOverlapsVertically(Rectangle2D playerRigidbody, Rectangle2D obstacleBounds) {
        return !(obstacleBounds.getMaxY() < playerRigidbody.getMinY() + EPSILON ||
                obstacleBounds.getMinY() > playerRigidbody.getMaxY() - EPSILON);
    }
}
