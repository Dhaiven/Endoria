import java.util.HashMap;

/**
 * Classe Room - un lieu du jeu d'aventure Zuul.
 *
 * @author DEBELLE Hugo
 */
public class Room
{
    
    private String aDescription;

    private HashMap<String, Room> exits;

    /**
     * Construct de Room
     */
    public Room(final String pDescription) {
        this.aDescription = pDescription;
        exits = new HashMap<>();
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
        return "You are " + aDescription + "\nExits: " + this.getExitString();
    }

    /**
     * @param pDirection - direction souhaité
     * @return la salle disposable dans la direction donnée
     */
    public Room getExit(String pDirection) {
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
        exits.put(direction, exit);
    }

    /**
     * Set les rooms dans les direction north, east, south et west
     * @deprecated
     */
    public void setExits(
        final Room pNorthExit,
        final Room pEastExit,
        final Room pSouthExit,
        final Room pWestExit
    ) {
        if (pNorthExit != null) {
            exits.put("north", pNorthExit);
        }
        if (pEastExit != null) {
            exits.put("east", pEastExit);
        }
        if (pSouthExit != null) {
            exits.put("south", pSouthExit);
        }
        if (pWestExit != null) {
            exits.put("west", pWestExit);
        }
    }
} // Room
