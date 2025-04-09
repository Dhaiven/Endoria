import java.util.HashMap;

/**
 *  Cette classe représente une pièce
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class Room
{
    
    private final String aDescription;

    private final HashMap<String, Door> exits;
    private final String aImageName;

    private final ItemList aItemList = new ItemList();

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
     * @return la description contenant la description de la pièce
     * et les sorties disponibles
     */
    public String getLongDescription() {
        return "You are " + aDescription + "\nExits: " + this.getExitString() + "\n" + this.aItemList.getItemString();
    }

    /**
     * @param pRoom la pièce dans laquelle on veut se rendre
     * @return true s'il existe une sortie amenant à cette pièce else false
     */
    public boolean isExit(final Room pRoom) {
        for (Door vDoor : this.exits.values()) {
            if (vDoor.getTo().getDescription().equals(pRoom.getDescription())) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param pDirection direction souhaitée
     * @return la porte disposable dans la direction donnée
     */
    public Door getExit(final String pDirection) {
        return this.exits.get(pDirection);
    }

    /**
     * @return un String de toutes les sorties disposables
     */
    public String getExitString() {
        StringBuilder vResult = new StringBuilder();
        for (String vDirection : this.exits.keySet()) {
            vResult.append(vDirection).append(" ");
        }
        
        return vResult.toString();
    }

    /**
     * Set une room de sortie à la direction donnée
     */
    public void setExit(final String pDirection, final Room pExit) {
        this.exits.put(pDirection, new Door(pExit));
    }

    /**
     * Set une room de sortie à la direction donnée
     * Cette sortie nécessite une clé pour être emprunté
     */
    public void setLockedExit(final String pDirection, final Room pExit, final Item pKey) {
        this.exits.put(pDirection, new LockDoor(pExit, pKey));
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
} // Room
