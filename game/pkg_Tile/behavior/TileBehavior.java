package game.pkg_Tile.behavior;

import game.pkg_Entity.Entity;
import game.pkg_Entity.FacingDirection;
import game.pkg_Player.Player;
import game.pkg_Item.Item;
import game.pkg_Object.TileStateWithPos;
import game.pkg_Tile.TileProperty;

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
     * Cette méthode est appellé quand l'entité essaye de change de tuile
     * c'est à dire quand il arrive ou part de cette tuile
     *
     * @param oldTile l'ancienne tuile, jamais null
     * @param newTile la nouvelle tuile, jamais null
     * @param entity l'entité qui bouge
     *
     * @return true if entity can change tile else false
     */
    public boolean canChangeTile(TileStateWithPos oldTile, TileStateWithPos newTile, Entity entity, FacingDirection direction) {
        /**System.out.println("old tile: " + oldTile);
        System.out.println("new tile: " + newTile);
        System.out.println("myDirection: " + direction);
        System.out.println("absDirection: " + FacingDirection.fromVectors(oldTile.position().vector2(), newTile.position().vector2()));
        if (direction == FacingDirection.fromVectors(oldTile.position().vector2(), newTile.position().vector2())) {
            if (oldTile.tile().hasProperty(TileProperty.CHANGE_LAYER)) {
                return true;
            } else if (newTile.tile().hasProperty(TileProperty.CHANGE_LAYER)) {
                return true;
            }

            return oldTile.layer() == newTile.layer();
        }*/

        return true;
    }

    /**
     * Cette méthode est appellé quand l'entité change de tuile
     * c'est à dire quand il arrive ou part de cette tuile
     * Elle est appellé après le mouvement
     *
     * @param oldTile l'ancienne tuile, jamais null
     * @param newTile la nouvelle tuile, jamais null
     * @param entity l'entité qui bouge
     */
    public void onChangeTile(TileStateWithPos oldTile, TileStateWithPos newTile, Entity entity) {
    }

    /**
     * Cette méthode est appellé quand l'entité essaye de bouger sur cette tile.
     *
     * @param state  le state de la tile
     * @param entity l'entité qui collide
     *
     * @return true if entity can move on this tile else false
     */
    public boolean canMove(TileStateWithPos state, Entity entity) {
        return true;
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
