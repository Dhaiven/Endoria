package game.pkg_Entity;

import game.pkg_Entity.pkg_Player.Player;
import game.pkg_Object.Position;
import game.pkg_Object.Vector2;

import javax.swing.*;

/**
 * Classe représentant un PNJ
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public abstract class Character extends Entity {

    public Character(JComponent paintedOn, Sprite sprite, Position position, int layer) {
        super(paintedOn, sprite, position, layer);
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
