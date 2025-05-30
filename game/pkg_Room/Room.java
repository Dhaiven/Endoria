package game.pkg_Room;

import game.pkg_Entity.Character;
import game.pkg_Entity.Entity;
import game.pkg_Entity.FacingDirection;
import game.pkg_Object.Vector2i;
import game.pkg_Player.Player;
import game.pkg_Item.ItemList;
import game.pkg_Object.Position;
import game.pkg_Object.TileStateWithPos;
import game.pkg_Object.Vector2;
import game.pkg_Tile.Tile;
import java.awt.*;
import java.awt.geom.Area;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Cette classe représente une pièce
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class Room {

    private final Shape shape;
    private final String name;
    private final Vector2 spawn;
    private final List<List<TileStateWithPos>> tiles = new ArrayList<>();

    private Vector2 roomScale;

    private final HashMap<FacingDirection, List<Door>> exits = new HashMap<>();

    /**
     * ATTENTION: cet objet est éfficace si bcp plus de lecture que d'écriture
     * si on doit trop changer le nombre d'entités, choisir une autre méthode
     */
    private final List<Entity> entities = new CopyOnWriteArrayList<>();

    private final ItemList aItemList = new ItemList();

    protected boolean isLoaded = false;

    public Room(Shape shape, String name, Vector2 roomScale, Vector2 spawn) {
        this(shape, name, roomScale, new HashMap<>(), spawn);
    }

    public Room(Shape shape, String name, Vector2 roomScale, Map<Integer, List<Map<Vector2i, Tile>>> tiles) {
        this(shape, name, roomScale, tiles, null);
    }

    public Room(Shape shape, String name, Vector2 roomScale, Map<Integer, List<Map<Vector2i, Tile>>> tiles, Vector2 spawn) {
        this.shape = shape;
        this.name = name;
        this.spawn = spawn;
        this.roomScale = roomScale;

        for (var layerEntry : tiles.entrySet()) {
            if (this.tiles.size() == layerEntry.getKey()) {
                this.tiles.add(new ArrayList<>());
            }

            for (var subLayerEntry : layerEntry.getValue()) {
                for (var state : subLayerEntry.entrySet()) {
                    var value = new TileStateWithPos(
                            state.getValue(),
                            new Position(state.getKey(), this),
                            layerEntry.getKey()
                    );
                    this.tiles.get(layerEntry.getKey()).add(value);
                    state.getValue().getBehaviors().forEach(behavior -> {
                        behavior.onPlace(value, null);
                    });
                }
            }
        }

        for (FacingDirection direction : FacingDirection.values()) {
            this.exits.put(direction, new ArrayList<>());
        }
    }



    public Shape getShape() {
        return shape;
    }

    public Area getArea() {
        return new Area(shape);
    }

    public Vector2 getSpawnPoint() {
        return spawn;
    }

    public String getName() {
        return name;
    }

    public Vector2 getRoomScale() {
        return roomScale;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void load() {
        isLoaded = true;
        onLoad();
    }

    protected void onLoad() {

    }

    public void unload() {
        isLoaded = false;
        onUnload();
    }

    protected void onUnload() {
        entities.forEach(Entity::despawn);
        entities.clear();
    }

    public void onUpdate() {
        for (Entity entity : entities) {
           entity.onUpdate();
        }
    }

    public boolean contains(Vector2 vector) {
        return this.shape.contains(vector.x(), vector.y());
    }

    public List<List<TileStateWithPos>> getTiles() {
        return tiles;
    }

    public TileStateWithPos getHighestTileAt(Vector2 vector) {
        for (int layer = this.tiles.size() - 1; layer >= 0; layer--) {
            List<TileStateWithPos> subLayers = this.tiles.get(layer);
            for (int subLayer = subLayers.size() - 1; subLayer >= 0; subLayer--) {
                TileStateWithPos state = subLayers.get(subLayer);
                if (state.position().vector2().equals(vector)) {
                    return state;
                }
            }
        }

        return null;
    }

    public TileStateWithPos getTileAt(Vector2 vector, int layer) {
        for (TileStateWithPos entries : tiles.get(layer)) {
            if (entries.position().vector2().equals(vector)) {
                return entries;
            }
        }

        return null;
    }

    public Set<Integer> getLayers() {
        Set<Integer> result = new HashSet<>();
        for (int layer = 0; layer < this.tiles.size(); layer++) {
            result.add(layer);
        }

        return result;
    }

    public List<TileStateWithPos> getWorldsTilesCacheAtLayer(int layer) {
        return tiles.get(layer);
    }

    public void setTile(Tile tile, int row, int column, int layer) {
        setTile(tile, row, column, layer, null);
    }

    public void setTile(Tile tile, int row, int column, int layer, Player player) {
        setTile(tile, new Vector2(column * tile.getSprite().getHeight(), row * tile.getSprite().getWidth()), layer, player);
    }

    public void setTile(Tile tile, Vector2i position, int layer) {
        setTile(tile, new Vector2(position.x(), position.y()), layer);
    }

    public void setTile(Tile tile, Vector2 position, int layer) {
        setTile(tile, position, layer, null);
    }

    public void setTile(Tile tile, Vector2i position, int layer, Player player) {
        setTile(tile, new Vector2(position.x(), position.y()), layer, player);
    }

    public void setTile(Tile tile, Vector2 position, int layer, Player player) {
        TileStateWithPos oldState = getTileAt(position, layer);
        if (oldState != null) {
            tiles.get(layer).remove(oldState);
        }

        TileStateWithPos newState = new TileStateWithPos(tile, new Position(position, this), layer);
        tiles.get(layer).add(newState);

        if (oldState != null) {
            oldState.tile().getBehaviors().forEach(behavior -> behavior.onDestroy(oldState, player));
        }

        tile.getBehaviors().forEach(behavior -> behavior.onPlace(newState, player));
    }

    /**
     * @return la description de la room
     */
    /**public String getDescription() {
        return this.aDescription;
    }*/

    /**
     * @return la description contenant la description de la pièce
     * et les sorties disponibles
     */
    public String getLongDescription() {
        return "You are " + name +
                "\n" +
                "Exits: " + this.getExitString() +
                "\n" +
                this.aItemList.getItemString();
    }

    /**
     * @param pRoom la pièce dans laquelle on veut se rendre
     * @return true s'il existe une sortie amenant à cette pièce else false
     */
    public boolean isExit(Room pRoom) {
        for (List<Door> doors : this.exits.values()) {
            for (Door door : doors) {
                if (door.getTo().equals(pRoom)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Fonction qui retourne toutes les sorties possibles
     */
    public Map<FacingDirection, List<Door>> getExits() {
        return this.exits;
    }

    /**
     * Fonction qui retourne toutes les sorties possibles en fonction
     * de la direction
     */
    public List<Door> getExits(FacingDirection direction) {
        return this.exits.get(direction);
    }

    public Door getExit(Vector2 position, FacingDirection direction) {
        List<Door> possibleDoors = this.exits.get(direction);
        for (Door door : possibleDoors) {
            if (door.getShape().contains(position.x(), position.y())) {
                return door;
            }
        }

        return null;
    }

    /**
     * @return un String de toutes les sorties disposables
     */
    public String getExitString() {
        StringBuilder result = new StringBuilder();
        for (FacingDirection direction : exits.keySet()) {
            result.append(direction.getName()).append(" ");
        }
        
        return result.toString();
    }

    public void addExit(FacingDirection direction, Door door) {
        exits.get(direction).add(door);
    }

    /**
     * @return tous les personnages de la classe
     */
    public List<Entity> getEntities() {
        return this.entities;
    }

    /**
     * Get an entity with this name
     * @param pName entity's name
     * @return Entity if she exists else null
     */
    public Entity getEntityByName(String pName) {
        for (Entity entity : this.entities) {
            if (entity.getName().equals(pName)) {
                return entity;
            }
        }

        return null;
    }

    /**
     * Ajoute un personnage dans cette pièce
     */
    public void addEntity(Entity pEntity) {
        this.entities.add(pEntity);
    }

    /**
     * Supprime un personnage présent dans cette pièce
     */
    public void removeEntity(Entity pEntity) {
        this.entities.remove(pEntity);
    }

    /**
     * @return tous les personnages de la classe
     */
    public List<Entity> getCharacters() {
        return this.entities.stream().filter(entity -> entity instanceof Character).collect(Collectors.toList());
    }

    /**
     * Get un personnage en fonction de son nom
     * @param pName le nom du personnage
     * @return Character si le personage exist else null
     */
    public Character getaCharacterByName(String pName) {
        for (Entity entity : this.entities) {
            if (entity instanceof Character) {
                if (entity.getName().equals(pName)) {
                    return (Character) entity;
                }
            }
        }

        return null;
    }

    /**
     * Ajoute un personnage dans cette pièce
     */
    public void addCharacter(Character pCharacter) {
        this.addEntity(pCharacter);
    }

    /**
     * Supprime un personnage présent dans cette pièce
     */
    public void removeCharacter(Character pCharacter) {
        this.removeEntity(pCharacter);
    }

    /**
     * @return String de tous les personnages disposables
     */
    public String getCharacterString() {
        StringBuilder result = new StringBuilder();
        for (Entity entity : this.entities) {
            if (entity instanceof Character) {
                result.append(entity.getName()).append(" ");
            }
        }

        return result.toString();
    }

    /**
     * @return tous les items disponibles dans cette pièce
     */
    public ItemList getItemList() {
        return this.aItemList;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Room room)) return false;
        return Objects.equals(getName(), room.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(shape, getName(), getExits(), entities, aItemList, isLoaded, getEntities());
    }

    @Override
    public String toString() {
        return "Room{" +
                "name='" + name + '\'' +
                '}';
    }
} // Room
