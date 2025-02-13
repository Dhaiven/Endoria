import java.util.HashMap;
import java.util.Stack;

public class Player {

    private String aName;

    private Room aCurrentRoom;
    private Stack<Room> aLastRooms;

    private HashMap<String, Item> aItems;

    public Player(String pName, Room pCurrentRoom) {
        this.aName = pName;
        this.aCurrentRoom = pCurrentRoom;

        this.aLastRooms = new Stack<>();
        this.aItems = new HashMap<>();
    }

    public Room getCurrentRoom() {
        return this.aCurrentRoom;
    }

    /**
     * Fonction permettant d'aller dans la salle souhaiter
     * @param direction
     * @return boolean true si il est dans la nouvelle piece else false
     */
    public boolean goRoom(final String direction) {
        Room vNextRoom = this.aCurrentRoom.getExit(direction);
        if (vNextRoom == null) {
            return false;
        }

        this.aLastRooms.push(this.aCurrentRoom);
        this.aCurrentRoom = vNextRoom;
        return true;
    }

    public boolean back() {
        if (this.aLastRooms.isEmpty()) {
            return false;
        }

        this.aCurrentRoom = this.aLastRooms.pop();
        return true;
    }

    public boolean take(final String pItemName) {
        Item item = this.aCurrentRoom.getItemByName(pItemName);
        if (item == null) {
            return false;
        }

        this.aCurrentRoom.removeItem(item);
        this.aItems.put(pItemName, item);

        return true;
    }

    public boolean drop(final String pItemName) {
        Item item = this.aItems.get(pItemName);
        if (item == null) {
            return false;
        }

        this.aItems.remove(pItemName);
        this.aCurrentRoom.addItem(item);

        return true;
    }

}
