package game.pkg_Entity;

import game.pkg_Entity.pkg_Player.Player;
import game.pkg_Object.Position;

import javax.swing.*;

/**
 * Classe représentant un PNJ
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public abstract class Character extends Entity {

    public Character(JComponent paintedOn, Sprite sprite, Position position, FacingDirection facing) {
        super(paintedOn, sprite, position, facing);
    }

    /**
     * @return le nom du personnage
     */
    abstract public String getName();

    /**
     * Procédure appellé quand le joueur "intéragis" avec ce personnage
     * @param player le joueur qui intéragit
     */
    public void onInteract(Player player) {

    }
}
