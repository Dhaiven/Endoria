package game.pkg_Room;

import game.pkg_Entity.pkg_Player.Player;

/**
 *  Cette classe implémente une porte classique
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class Door {

    private final Room to;

    public Door(Room to) {
        this.to = to;
    }

    /**
     * @return la salle vers Raquel cette porte nous amène
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
}
