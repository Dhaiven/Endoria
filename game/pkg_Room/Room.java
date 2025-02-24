package game.pkg_Room;

import game.pkg_Entity.Character;
import game.pkg_Entity.Entity;
import game.pkg_Entity.FacingDirection;
import game.pkg_Entity.pkg_Player.Player;
import game.pkg_Item.ItemList;
import game.pkg_Object.Position;
import game.pkg_Object.Vector2;
import game.pkg_Tile.Tile;
import game.pkg_Tile.behavior.TileBehavior;
import game.pkg_World.Layers;

import java.awt.*;
import java.awt.geom.Area;
import java.util.*;
import java.util.List;

/**
 *  Cette classe représente une pièce
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class Room
{

    private final Shape shape;
    private final String name;
    private final Layers layers;
    private final Vector2 spawn;
    private final Map<Vector2, Tile>[] tiles;

    private HashMap<FacingDirection, List<Door>> exits = new HashMap<>();
    private HashMap<String, Character> aCharacters = new HashMap<>();

    private String aImageName;

    private ItemList aItemList = new ItemList();

    protected boolean isLoaded = false;

    private final List<Entity> entities = new ArrayList<>();

    public Room(Shape shape, String name, Layers layers, Vector2 spawn) {
        this.shape = shape;
        this.name = name;
        this.layers = layers;
        this.spawn = spawn;

        this.tiles = new Map[layers.size()];
        for (int i = 0; i < layers.size(); i++) {
            tiles[i] = new HashMap<>();
        }

        for (FacingDirection direction : FacingDirection.values()) {
            exits.put(direction, new ArrayList<>());
        }
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

    public boolean contains(Vector2 vector) {
        return this.shape.contains(vector.x(), vector.y());
    }

    public Layers getLayers() {
        return layers;
    }

    /**
     * @return UnmodifiableMap
     */
    public Map<Vector2, Tile> getWorldsTilesCacheAtLayer(int layer) {
        return Collections.unmodifiableMap(tiles[layer - 1]);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setTile(Tile tile, int row, int column, int layer) {
        setTile(tile, row, column, layer, null);
    }

    public void setTile(Tile tile, int row, int column, int layer, Player player) {
        setTile(tile, new Vector2(column * tile.getSprite().getHeight(), row * tile.getSprite().getWidth()), layer, player);
    }

    public void setTile(Tile tile, Vector2 position, int layer) {
        setTile(tile, position, layer, null);
    }

    public void setTile(Tile tile, Vector2 position, int layer, Player player) {
        tiles[layer - 1].put(position, tile);

        for (TileBehavior behavior : tile.getBehaviors()) {
            behavior.onPlace(tile, new Position(position, this), player);
        }
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
                this.aItemList.getItemString() +
                "\n" +
                "Characters :" + this.getCharacterString();
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
    public HashMap<FacingDirection, List<Door>> getExits() {
        return this.exits;
    }

    /**
     * Fonction qui retourne toutes les sorties possibles en fonction
     * de la direction
     */
    public List<Door> getExits(FacingDirection direction) {
        return this.exits.get(direction);
    }

    /**
     * @param pDirection direction souhaitée
     * @return la porte disposable dans la direction donnée
     */
    public Door getExit(Vector2 position, FacingDirection direction) {
        List<Door> possibleDoors = this.exits.get(direction);
        System.out.println("Possition: " + position);
        System.out.println("Possible Doors: " + possibleDoors);
        for (Door door : possibleDoors) {
            if (door.getShape().contains(position.x(), position.y())) {
                System.out.println("contains pos");
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
    public HashMap<String, Character> getCharacters() {
        return this.aCharacters;
    }

    /**
     * Get un personnage en fonction de son nom
     * @param pName le nom du personnage
     * @return Character si le personage exist else null
     */
    public Character getaCharacterByName(String pName) {
        return this.aCharacters.get(pName);
    }

    /**
     * Ajoute un personnage dans cette pièce
     */
    public void addCharacter(Character pCharacter) {
        this.aCharacters.put(pCharacter.getName(), pCharacter);
    }

    /**
     * Supprime un personnage présent dans cette pièce
     */
    public void removeCharacter(Character pCharacter) {
        this.aCharacters.remove(pCharacter.getName());
    }

    /**
     * @return un String de tous les personnages disposables
     */
    public String getCharacterString() {
        StringBuilder result = new StringBuilder();
        for (String name : this.aCharacters.keySet()) {
            result.append(name).append(" ");
        }

        return result.toString();
    }

    /**
     * Return a string describing the room's image name
     */
    public String getImageName() {
        return this.aImageName;
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
        return Objects.equals(shape, room.shape) &&
                Objects.equals(getName(), room.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(shape, getName(), getLayers(), Arrays.hashCode(tiles), getExits(), aCharacters, aImageName, aItemList, isLoaded, getEntities());
    }

    @Override
    public String toString() {
        return "Room{" +
                "name='" + name + '\'' +
                '}';
    }
} // Room
