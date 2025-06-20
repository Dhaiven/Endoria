package game.world;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.physics.BoundingShape;
import game.object.*;
import game.player.Player;
import game.scheduler.Scheduler;
import game.util.Utils;

import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class World {

    private final String name;
    private BoundingShape dimension; // Dimensions du monde en pixels (largeur, hauteur)
    private final Vector2 spawn;

    private final Scheduler scheduler = new Scheduler();

    // Pour accès spatial rapide
    private final Map<Integer, Map<Integer, NavigableSet<TileStateWithPos>>> tilesByPosition = new HashMap<>();

    /**
     * <layer, <subLayer, <tile>>>
     */
    private final Map<Integer, Map<Integer, List<TileStateWithPos>>> tilesByLayer = new HashMap<>();

    /**
     * ATTENTION: cet objet est éfficace si bcp plus de lecture que d'écriture
     * si on doit trop changer le nombre d'entités, choisir une autre méthode
     */
    private final List<Entity> entities = new CopyOnWriteArrayList<>();

    protected boolean isLoaded = false;

    public World(String name, Vector2 spawn) {
        this.name = name;
        this.spawn = spawn;
    }

    public String getName() {
        return name;
    }

    public BoundingShape getDimension() {
        return dimension;
    }

    public void setDimension(BoundingShape dimension) {
        this.dimension = dimension;
    }

    public Scheduler getSchedulerService() {
        return scheduler;
    }

    public Vector2 getSpawnPoint() {
        return spawn;
    }

    public Map<Integer, Map<Integer, List<TileStateWithPos>>> getTiles() {
        return tilesByLayer;
    }

    private NavigableSet<TileStateWithPos> getSet(Cell cell) {
        var columns = tilesByPosition.get(cell.column());
        if (columns == null) return null;
        return columns.get(cell.row());
    }

    private NavigableSet<TileStateWithPos> getSet(int column, int row) {
        var columns = tilesByPosition.get(column);
        if (columns == null) return null;
        return columns.get(row);
    }

    public TileStateWithPos getHighestTileAt(Cell cell) {
        NavigableSet<TileStateWithPos> set = getSet(cell);
        return (set != null && !set.isEmpty()) ? set.last() : null;
    }

    public TileStateWithPos getTileAt(Cell cell, SubLayer layer) {
        NavigableSet<TileStateWithPos> set = getSet(cell);
        if (set != null) {
            for (var value : set) {
                if (value.layer().equals(layer)) {
                    return value;
                }
            }
        }

        return null;
    }

    /**
     * Retourne les tuiles visibles dans la zone spécifiée.
     * @param viewBounds La zone visible à l’écran (en pixels)
     * @return Liste des tuiles visibles
     */
    public List<NavigableSet<TileStateWithPos>> getTilesInView(Rectangle2D viewBounds) {
        List<NavigableSet<TileStateWithPos>> visibleTiles = new ArrayList<>();

        if (viewBounds == null) {
            for (var map : tilesByPosition.values()) {
                visibleTiles.addAll(map.values());
            }
            return visibleTiles;
        }

        int tileWidth = Utils.TEXTURE_SIZE.x();
        int tileHeight = Utils.TEXTURE_SIZE.y();

        int startCol = (int) Math.floor(viewBounds.getMinX() / tileWidth);
        int endCol   = (int) Math.ceil((viewBounds.getMaxX()) / tileWidth);  // -1 pour inclure le bord

        int startRow = (int) Math.floor(viewBounds.getMinY() / tileHeight);
        int endRow   = (int) Math.ceil((viewBounds.getMaxY()) / tileHeight);
        for (int col = startCol; col <= endCol; col++) {
            Map<Integer, NavigableSet<TileStateWithPos>> columnMap = tilesByPosition.get(col);
            if (columnMap == null) continue;

            for (int row = startRow; row <= endRow; row++) {
                NavigableSet<TileStateWithPos> tileSet = columnMap.get(row);
                if (tileSet != null) {
                    visibleTiles.add(tileSet);
                }
            }
        }

        return visibleTiles;
    }


    public boolean layerExist(int layer) {
        return tilesByLayer.containsKey(layer);
    }

    public boolean layerExist(SubLayer layer) {
        return layerExist(layer.layer()) && tilesByLayer.get(layer.layer()).containsKey(layer.subLayer());
    }

    public void setTile(Entity tile, Cell cell, SubLayer layer) {
        setTile(tile, cell, layer, null);
    }

    public void setTile(Entity tile, Cell cell, SubLayer layer, Player player) {
        TileStateWithPos oldState = getTileAt(cell, layer);
        if (oldState != null) {
            //oldState.tile().getBehaviors().forEach(behavior -> behavior.onDestroy(oldState, player));
            tilesByPosition.get(oldState.cell().column()).get(oldState.cell().row()).remove(oldState);
            tilesByLayer.get(oldState.layer().layer()).get(oldState.layer().subLayer()).remove(oldState);
        }

        TileStateWithPos newState = new TileStateWithPos(tile, cell, this, layer);
        tilesByPosition
                .computeIfAbsent(cell.column(), k -> new HashMap<>())
                .computeIfAbsent(cell.row(), k -> new TreeSet<>())
                .add(newState);
        tilesByLayer
                .computeIfAbsent(layer.layer(), l -> new HashMap<>())
                .computeIfAbsent(layer.subLayer(), s -> new ArrayList<>())
                .add(newState);

        //tile.getBehaviors().forEach(behavior -> behavior.onPlace(newState, player));
    }
}
