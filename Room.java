import java.util.HashMap;

/**
 * Classe Room - un lieu du jeu d'aventure Zuul.
 *
 * @author DEBELLE Hugo
 */
public class Room
{
    
    private String aDescription;

    private HashMap<String, Door> exits;
    private String aImageName;

    private ItemList aItemList = new ItemList();

    /**
     * Construct de Room
     */
    public Room(final String pDescription) {
       this(pDescription, null);
    }

    public Room(final String pDescription, final String pImage)
    {
        this.aDescription = pDescription;
        this.exits = new HashMap<>();
        this.aImageName = pImage;
    }

    /**
     * @return la description de la room
     */
    public String getDescription() {
        return this.aDescription;
    }

    /**
     * @return la description contenant la description de la zoom
     * et les sorties disponibles
     */
    public String getLongDescription() {
        return "You are " + aDescription + "\nExits: " + this.getExitString() + "\n" + this.aItemList.getItemString();
    }

    public boolean isExit(Room pRoom) {
        for (Door door : this.exits.values()) {
            if (door.getTo().getDescription().equals(pRoom.getDescription())) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param pDirection - direction souhaité
     * @return la porte disposable dans la direction donnée
     */
    public Door getExit(String pDirection) {
        return this.exits.get(pDirection);
    }

    /**
     * @return un String de touts les sorties disposables
     */
    public String getExitString() {
        StringBuilder result = new StringBuilder();
        for (String direction : exits.keySet()) {
            result.append(direction).append(" ");
        }
        
        return result.toString();
    }

    /**
     * set une room de sortie à la direction données
     */
    public void setExit(String direction, Room exit) {
        exits.put(direction, new Door(exit));
    }

    /**
     * set une room de sortie à la direction données
     * Cette sortie nécéssite une clé pour être emprumpté
     */
    public void setLockedExit(String direction, Room exit, Item key) {
        exits.put(direction, new LockDoor(exit, key));
    }

    /**
     * Return a string describing the room's image name
     */
    public String getImageName() {
        return this.aImageName;
    }

    public ItemList getItemList() {
        return this.aItemList;
    }
} // Room
