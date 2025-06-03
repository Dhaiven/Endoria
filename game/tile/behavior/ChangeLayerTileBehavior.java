package game.tile.behavior;

import game.entity.Entity;
import game.object.TileStateWithPos;
import game.tile.property.TileProperties;

public class ChangeLayerTileBehavior extends TileBehavior {

    // TODO: refactor
    @Override
    public void onChangeTile(TileStateWithPos oldTile, TileStateWithPos newTile, Entity entity, boolean isOldTile) {
        if (!isOldTile && newTile.tile().hasPropertyValue(TileProperties.CHANGE_LAYER)) {
            if (newTile.layer().layer() > entity.getLayer()) {
                entity.nextLayer();
            } else {
                //TODO: remove this condition if we add multiple change layer in succession
                if (!oldTile.tile().hasPropertyValue(TileProperties.CHANGE_LAYER)) {
                    entity.previousLayer();
                }
            }
        }

        super.onChangeTile(oldTile, newTile, entity, isOldTile);
    }
}
