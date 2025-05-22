package game.pkg_Room;

import game.pkg_Player.Player;
import game.pkg_Object.Vector2;

import java.awt.*;

/**
 *  Cette classe implémente une porte classique
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class Door {

    private final Shape shape;
    private final Vector2 position;
    private final Room to;

    public Door(Shape shape, Vector2 spawn, Room to) {
        this.shape = shape;
        this.position = spawn;
        this.to = to;
    }

    public Shape getShape() {
        return shape;
    }

    public Vector2 getSpawnPosition() {
        return position;
    }

    /**
     * @return la salle vers laquel cette porte nous amène
     */
    public Room getTo() {
        return to;
    }

    /**
     * @param player Le joueur qui veut passer
     * @return true si le joueur peut passer else false
     */
    public boolean canPass(Player player) {
        return true;
    }

    @Override
    public String toString() {
        return "Door{" +
                "shape=" + shape +
                ", to=" + to +
                '}';
    }
}
