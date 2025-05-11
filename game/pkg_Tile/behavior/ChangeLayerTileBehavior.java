package game.pkg_Tile.behavior;

import game.pkg_Entity.Entity;
import game.pkg_Entity.FacingDirection;
import game.pkg_Object.TileStateWithPos;
import game.pkg_Tile.TileProperty;

public class ChangeLayerTileBehavior extends TileBehavior {

    @Override
    public void onChangeTile(TileStateWithPos oldTile, TileStateWithPos newTile, Entity entity, boolean isOldTile) {
        System.out.println("Change layer tile");
        if (isOldTile && oldTile.tile().getProperties().contains(TileProperty.CHANGE_LAYER)) {
            if (newTile.tile().getProperties().contains(TileProperty.CHANGE_LAYER)) {
                entity.nextLayer();
                return;
            }

            if (newTile.layer() < entity.getLayer()) {
                entity.previousLayer();
            }
        } else if (!isOldTile && newTile.tile().getProperties().contains(TileProperty.CHANGE_LAYER)) {
            entity.nextLayer();
        }

        super.onChangeTile(oldTile, newTile, entity, isOldTile);
    }
}
