package game.pkg_Object;

import game.pkg_Tile.Tile;

public record TileStateWithPos(Tile tile, Position position, int layer) {
}
