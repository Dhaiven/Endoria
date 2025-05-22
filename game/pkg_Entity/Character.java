package game.pkg_Entity;

import game.pkg_Image.Sprite;
import game.pkg_Player.Player;
import game.pkg_Image.StaticSprite;
import game.pkg_Object.Position;

import java.awt.geom.Rectangle2D;

/**
 * Classe représentant un PNJ
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public abstract class Character extends Entity {

    public Character(String name, StaticSprite sprite, Position position, int layer) {
        super(name, sprite, position, layer);
    }

    public Character(String name, Sprite sprite, Rectangle2D rigidBody2D, Position position, int layer, FacingDirection facing) {
        super(name, sprite, rigidBody2D, position, layer, facing);
    }

    /**
     * Procédure appellé quand le joueur "intéragis" avec ce personnage
     * @param player le joueur qui intéragit
     */
    public void onInteract(Player player) {

    }
}
