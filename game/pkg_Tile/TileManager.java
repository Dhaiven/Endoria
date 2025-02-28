package game.pkg_Tile;

import game.pkg_Tile.behavior.TileBehavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TileManager {

    private HashMap<Integer, List<TileBehavior>> tilesBehaviors = new HashMap<>();

    public TileManager() {
        this.register(new TileBehavior(), 1, 2, 3, 4, 5);
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
