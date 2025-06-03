package game.object;

import game.tile.Tile;
import game.world.World;

public record TileStateWithPos(Tile tile, Cell cell, World world, SubLayer layer) implements Comparable<TileStateWithPos> {

    @Override
    public int compareTo(TileStateWithPos o) {
        // Ordre du plus bas au plus haut
        int cmp = Integer.compare(layer.layer(), o.layer().layer());
        if (cmp == 0) {
            return Integer.compare(layer.subLayer(), o.layer().subLayer());
        }
        return cmp;
    }
}
