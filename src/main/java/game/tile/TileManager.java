package game.tile;

import game.tile.behavior.ChangeLayerTileBehavior;
import game.tile.behavior.CollisionTileBehavior;
import game.tile.behavior.TileBehavior;
import game.tile.property.TileProperties;
import game.tile.property.TileProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

public class TileManager {

    private final HashMap<Predicate<Tile>, List<TileBehavior>> tilesBehaviors = new HashMap<>();

    public TileManager() {
        this.register(tile -> true, new TileBehavior());
        this.register(tile -> tile.getCollisions().length != 0, new CollisionTileBehavior());
        this.registerTileProperty(TileProperties.CHANGE_LAYER, new ChangeLayerTileBehavior());
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

    public void registerTileProperty(TileProperty<?> property, TileBehavior tileBehavior) {
        register(tile -> tile.getPropertyValues().containsKey(property), tileBehavior);
    }
}
