import java.util.Stack;

public class Player {

    private String aName;

    private Room aCurrentRoom;
    private Stack<Room> aLastRooms;

    private ItemList aItemList = new ItemList();
    private int aMaxWeight;

    public Player(String pName, Room pCurrentRoom, int pMaxWeight) {
        this.aName = pName;
        this.aCurrentRoom = pCurrentRoom;
        this.aMaxWeight = pMaxWeight;

        this.aLastRooms = new Stack<>();
    }

    public Room getCurrentRoom() {
        return this.aCurrentRoom;
    }

    public ItemList getItemList() {
        return this.aItemList;
    }

    public int getMaxWeight() {
        return this.aMaxWeight;
    }

    public void setMaxWeight(int aMaxWeight) {
        this.aMaxWeight = aMaxWeight;
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
        Item item = this.aCurrentRoom.getItemList().getItemByName(pItemName);
        if (item == null) {
            return false;
        } else if (this.aItemList.getWeight() + item.getWeight() > this.aMaxWeight) {
            return false;
        }

        this.aCurrentRoom.getItemList().removeItem(item);
        this.aItemList.addItem(item);

        return true;
    }

    public boolean drop(final String pItemName) {
        Item item = this.aItemList.getItemByName(pItemName);
        if (item == null) {
            return false;
        }

        this.aItemList.removeItem(item);
        this.aCurrentRoom.getItemList().addItem(item);

        return true;
    }

    public void use(Item pItem) {
        this.aItemList.removeItem(pItem);
        pItem.onUse(this);
    }
}
