package game.pkg_Tile.behavior;

import game.pkg_Entity.Entity;
import game.pkg_Player.Player;
import game.pkg_Item.Item;
import game.pkg_Object.TileStateWithPos;

public class TileBehavior {

    /**
     * Cette méthode est appellé une fois que le tile est placé,
     * c'est-à-dire qu'elle a déjà été placé dans la room.
     *
     * @param state le state de la tile
     * @param player le joueur qui l'a placé ou null
     */
    public void onPlace(TileStateWithPos state, Player player) {

    }

    /**
     * Cette méthode est appellé une fois que le tile est détruite,
     * c'est-à-dire qu'elle a déjà été remplacé par une nouvelle tile.
     *
     * @param state le state de la tile avant ça déstruction
     * @param player le joueur qui a détruit la tile ou null
     */
    public void onDestroy(TileStateWithPos state, Player player) {

    }

    public void onInteract(TileStateWithPos state, Player player, Item usedItem) {

    }

    /**
     * Cette méthode est appellé quand l'entité collide.
     * Elle est appellé pendant le mouvement, c'est-à-dire que l'entité
     * n'a toujours pas bougé
     *
     * @param state le state de la tile
     * @param entity l'entité qui collide, JAMAIS null
     */
    public void onEntityCollide(TileStateWithPos state, Entity entity) {
    }
}
