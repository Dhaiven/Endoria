package game.pkg_Tile.behavior;

import game.pkg_Entity.Entity;
import game.pkg_Object.TileStateWithPos;
import game.pkg_Tile.TileProperty;

public class ChangeLayerTileBehavior extends TileBehavior {

    @Override
    public void onChangeTile(TileStateWithPos oldTile, TileStateWithPos newTile, Entity entity, boolean isOldTile) {
        if (!isOldTile && newTile.tile().getProperties().contains(TileProperty.CHANGE_LAYER)) {
            if (newTile.layer() > entity.getLayer()) {
                entity.nextLayer();
            } else {
                entity.previousLayer();
            }
        }

        super.onChangeTile(oldTile, newTile, entity, isOldTile);
    }
}
