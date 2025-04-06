package game.pkg_Tile;

import game.pkg_Tile.behavior.CollisionTileBehavior;
import game.pkg_Tile.behavior.TileBehavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TileManager {

    private final HashMap<Integer, List<TileBehavior>> tilesBehaviors = new HashMap<>();

    public TileManager() {
        this.register(new CollisionTileBehavior(), 66);
    }

    public List<TileBehavior> getTileBehaviors(int tiledId) {
        if (tilesBehaviors.containsKey(tiledId)) {
            return tilesBehaviors.get(tiledId);
        }

        return new ArrayList<>();
    }

    public void register(int id, TileBehavior tileBehavior) {
        if (!tilesBehaviors.containsKey(id)) {
            tilesBehaviors.put(id, new ArrayList<>());
        }

        tilesBehaviors.get(id).add(tileBehavior);
    }

    public void register(TileBehavior tileBehavior, int ...ids) {
        for (Integer id : ids) {
            this.register(id, tileBehavior);
        }
    }
}
