package game.pkg_Item;

import game.pkg_Entity.pkg_Player.Player;
import game.pkg_Object.GameObject;
import game.pkg_Entity.Sprite;

/**
 *  Cette classe représente un Item de base
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class Item extends GameObject {

    private String aName;
    private String aDescription;
    private int aWeight;

    public Item(Sprite sprite, String pName, String pDescription, int pWeight) {
        super(sprite);
        this.aName = pName;
        this.aDescription = pDescription;
        this.aWeight = pWeight;
    }

    /**
     * @return le nom de l'item
     */
    public String getName() {
        return this.aName;
    }

    /**
     * @return la description de l'item
     */
    public String getDescription() {
        return this.aDescription;
    }

    /**
     * @return la description de l'item qui doit être affiché au joueur
     */
    public String getLongDescription() {
        return "Item " + this.getName() + "\n" + this.getDescription() + "\nWeight: " + this.getWeight();
    }

    /**
     * @return le poids de l'item
     */
    public int getWeight() {
        return this.aWeight;
    }

    /**
     * Procédure appellé quand le joueur utilise cet item
     * @param player le joueur ayant utilisé cet item
     */
    public void onUse(Player player) {

    }
}
