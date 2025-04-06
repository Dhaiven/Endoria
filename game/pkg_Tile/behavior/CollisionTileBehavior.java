package game.pkg_Tile.behavior;

import game.pkg_Entity.Entity;
import game.pkg_Object.TileStateWithPos;

public class CollisionTileBehavior extends TileBehavior {

    @Override
    public void onEntityCollide(TileStateWithPos state, Entity entity) {
        System.out.println("Collision Tile Behavior");
    }
}
