package game.pkg_Tile;

import game.pkg_Tile.behavior.ChangeLayerTileBehavior;
import game.pkg_Tile.behavior.CollisionTileBehavior;
import game.pkg_Tile.behavior.TileBehavior;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class TileManager {

    private final HashMap<Predicate<Tile>, List<TileBehavior>> tilesBehaviors = new HashMap<>();

    public TileManager() {
        this.register(tile -> tile.getCollisions().length != 0, new CollisionTileBehavior());
        this.registerTileProperty(TileProperty.CHANGE_LAYER, new ChangeLayerTileBehavior());
    }

    public List<TileBehavior> getTileBehaviors(Tile tile) {
        List<TileBehavior> result = new ArrayList<>();
        for (var entry : tilesBehaviors.entrySet()) {
            if (entry.getKey().test(tile)) {
                result.addAll(entry.getValue());
            }
        }

        if (result.isEmpty()) {
            result.add(new TileBehavior());
        }

        return result;
    }

    public void register(Predicate<Tile> predicate, TileBehavior tileBehavior) {
        if (!tilesBehaviors.containsKey(predicate)) {
            tilesBehaviors.put(predicate, new ArrayList<>());
        }

        tilesBehaviors.get(predicate).add(tileBehavior);
    }

    public void registerTileProperty(TileProperty property, TileBehavior tileBehavior) {
        register(tile -> tile.getProperties().contains(property), tileBehavior);
    }
}
