package game.item;

import game.player.Player;
import game.object.GameObject;
import game.image.StaticSprite;

/**
 *  Cette classe représente un Item de base
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class Item extends GameObject {

    private final String aName;
    private final String aDescription;
    private final int aWeight;

    public Item(StaticSprite sprite, String pName, String pDescription, int pWeight) {
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
