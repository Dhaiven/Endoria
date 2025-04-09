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
    private final Stack<Room> aLastRooms;

    private final ItemList aItemList = new ItemList();
    private int aMaxWeight;

    public Player(final String pName, final Room pCurrentRoom, final int pMaxWeight) {
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
     * @param pMaxWeight le nouveau poids max
     */
    public void setMaxWeight(final int pMaxWeight) {
        this.aMaxWeight = pMaxWeight;
    }

    /**
     * Fonction permettant d'aller dans la salle souhaiter
     * @param pDirection la direction souhaitée
     * @return boolean true s'il est dans la nouvelle piece else false
     */
    public boolean goRoom(final String pDirection) {
        Door vNextDoor = this.aCurrentRoom.getExit(pDirection);
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
    public boolean goRoom(final Room pRoom) {
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
        Item vItem = this.aCurrentRoom.getItemList().getItemByName(pItemName);
        if (vItem == null) {
            return false;
        } else if (this.aItemList.getWeight() + vItem.getWeight() > this.aMaxWeight) {
            return false;
        }

        this.aCurrentRoom.getItemList().removeItem(vItem);
        this.aItemList.addItem(vItem);

        return true;
    }

    /**
     * Fonction permettant de lâcher un item
     * @param pItemName le nom de l'item que le joueur souhaite lâcher
     * @return true si l'item a été lâché else false
     */
    public boolean drop(final String pItemName) {
        Item vItem = this.aItemList.getItemByName(pItemName);
        if (vItem == null) {
            return false;
        }

        this.aItemList.removeItem(vItem);
        this.aCurrentRoom.getItemList().addItem(vItem);

        return true;
    }

    /**
     * Procédure permettant d'utiliser un item
     * Le supprime de l'inventaire
     * @param pItem l'item a utilisé
     */
    public void use(final Item pItem) {
        this.aItemList.removeItem(pItem);
        pItem.onUse(this);
    }
}
