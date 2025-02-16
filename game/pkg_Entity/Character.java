package game.pkg_Entity;

import game.pkg_Entity.pkg_Player.Player;

/**
 * Classe représentant un PNJ
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class Character {

    private String aName;

    public Character(String pName) {
        this.aName = pName;
    }

    /**
     * @return le nom du personnage
     */
    public String getName() {
        return this.aName;
    }

    /**
     * Procédure appellé quand le joueur "intéragis" avec ce personnage
     * @param player le joueur qui intéragit
     */
    public void onInteract(Player player) {

    }
}
