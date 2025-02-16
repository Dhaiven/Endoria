import java.util.Stack;

/**
 *  Cette classe représente un Joueur
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class Player {

    private UserInterface aUserInterface;
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

    /**
     * @return l'interface utilisateur permettant d'afficher des messages
     */
    public UserInterface getUserInterface() {
        return this.aUserInterface;
    }

    /**
     * Permet de changer l'interface du joueur
     * NE PAS UTILISER AUTRE PAR QUE DANS GAME ENGINE
     */
    public void setUserInterface(UserInterface pUserInterface) {
        this.aUserInterface = pUserInterface;
    }

    /**
     * @return la pièce dans laquelle se trouve actuellement le joueur
     */
    public Room getCurrentRoom() {
        return this.aCurrentRoom;
    }

    /**
     * @return tous les items que le joueur possède dans son inventaire
     */
    public ItemList getItemList() {
        return this.aItemList;
    }

    /**
     * @return le poids max que le joueur peut porter
     */
    public int getMaxWeight() {
        return this.aMaxWeight;
    }

    /**
     * Procédure permettant de changer le poids max que le
     * joueur peut porter
     * @param aMaxWeight le nouveau poids max
     */
    public void setMaxWeight(int aMaxWeight) {
        this.aMaxWeight = aMaxWeight;
    }

    /**
     * Fonction permettant d'aller dans la salle souhaiter
     * @param direction la direction souhaitée
     * @return boolean true s'il est dans la nouvelle piece else false
     */
    public boolean goRoom(final String direction) {
        Door vNextDoor = this.aCurrentRoom.getExit(direction);
        if (vNextDoor == null || !vNextDoor.canPass(this)) {
            return false;
        }

        return this.goRoom(vNextDoor.getTo());
    }

    /**
     * Fonction permettant d'aller dans la salle souhaiter
     * @param pRoom n'importe quelle pièce du jeu
     * @return boolean true s'il est dans la nouvelle piece else false
     */
    public boolean goRoom(Room pRoom) {
        this.aLastRooms.push(this.aCurrentRoom);
        this.aCurrentRoom = pRoom;
        return true;
    }

    /**
     * Fonction permettant de retourner dans la dernière pièce visitée
     * @return true si le joueur a réussi à back else false
     */
    public boolean back() {
        if (this.aLastRooms.isEmpty()) {
            return false;
        }

        Room vLastRoom = this.aLastRooms.peek();
        if (!this.aCurrentRoom.isExit(vLastRoom)) {
            return false;
        }

        this.aCurrentRoom = this.aLastRooms.pop();
        return true;
    }

    /**
     * Fonction permettant de récupérer un item
     * @param pItemName le nom de l'item que le joueur souhaite prendre
     * @return true si le joueur a pris l'item else false
     */
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

    /**
     * Fonction permettant de lâcher un item
     * @param pItemName le nom de l'item que le joueur souhaite lâcher
     * @return true si l'item a été lâché else false
     */
    public boolean drop(final String pItemName) {
        Item item = this.aItemList.getItemByName(pItemName);
        if (item == null) {
            return false;
        }

        this.aItemList.removeItem(item);
        this.aCurrentRoom.getItemList().addItem(item);

        return true;
    }

    /**
     * Procédure permettant d'utiliser un item
     * Le supprime de l'inventaire
     * @param pItem l'item a utilisé
     */
    public void use(Item pItem) {
        this.aItemList.removeItem(pItem);
        pItem.onUse(this);
    }
}
