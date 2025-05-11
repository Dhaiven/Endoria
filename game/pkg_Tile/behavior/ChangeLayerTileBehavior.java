package game.pkg_Tile.behavior;

import game.pkg_Entity.Entity;
import game.pkg_Entity.FacingDirection;
import game.pkg_Object.TileStateWithPos;
import game.pkg_Tile.TileProperty;

public class ChangeLayerTileBehavior extends TileBehavior {

    @Override
    public boolean canChangeTile(TileStateWithPos oldTile, TileStateWithPos newTile, Entity entity, FacingDirection direction) {
        return true;
    }

    @Override
    public void onChangeTile(TileStateWithPos oldTile, TileStateWithPos newTile, Entity entity) {
        System.out.println("Change layer tile");
        if (oldTile.tile().getProperties().contains(TileProperty.CHANGE_LAYER)) {
            if (newTile.tile().getProperties().contains(TileProperty.CHANGE_LAYER)) {
                //TODO
                return;
            }

            //entity.previousLayer();
        } else if (newTile.tile().getProperties().contains(TileProperty.CHANGE_LAYER)) {
            entity.nextLayer();
        }

        super.onChangeTile(oldTile, newTile, entity);
    }
}
