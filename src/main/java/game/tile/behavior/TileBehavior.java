package game.tile.behavior;

import com.almasb.fxgl.entity.Entity;
import game.player.Player;
import game.item.Item;
import game.object.TileStateWithPos;

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
     * Cette méthode est appellé quand l'entité change de tuile
     * c'est à dire quand il arrive ou part de cette tuile
     * Elle est appellé après le mouvement
     *
     * @param oldTile l'ancienne tuile, jamais null
     * @param newTile la nouvelle tuile, jamais null
     * @param entity l'entité qui bouge
     * @param isOldTile true si cet tile est l'ancienne tile false si c'est la nouvelle
     */
    public void onChangeTile(TileStateWithPos oldTile, TileStateWithPos newTile, Entity entity, boolean isOldTile) {
    }

    /**
     * Cette méthode est appellé quand l'entité bouge sur cette tile.
     * Elle est appellé après le mouvement
     *
     * @param state  le state de la tile
     * @param entity l'entité qui collide
     */
    public void onEntityMove(TileStateWithPos state, Entity entity) {
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
