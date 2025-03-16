package game.pkg_Entity.pkg_Player;

import game.pkg_Command.Command;
import game.pkg_Entity.*;
import game.pkg_Entity.Character;
import game.pkg_Image.Sprite;
import game.pkg_Item.Item;
import game.pkg_Item.ItemList;
import game.pkg_Object.Position;
import game.pkg_Object.Vector2;
import game.pkg_Room.Door;
import game.pkg_Room.Room;

import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;

/**
 *  Cette classe représente un Joueur
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class Player extends Entity {

    private UserInterface aUserInterface;

    private Stack<Door> aLastRooms = new Stack<>();

    private ItemList aItemList = new ItemList();
    private int aMaxWeight;

    // TODO: faut pas devoir reset paintedOn après le super
    // TODO: custom layer
    public Player(Function<Player, UserInterface> userInterface, Sprite sprite, Room room) {
        super(null, sprite, new Position(room.getSpawnPoint(), room), 2);
        this.aUserInterface = userInterface.apply(this);
        this.paintedOn = this.aUserInterface;
    }
    /**
     * @return l'interface utilisateur permettant d'afficher des messages
     */
    public UserInterface getUserInterface() {
        return this.aUserInterface;
    }

    /**
     * @return la pièce dans laquelle se trouve actuellement le joueur
     */
    public Room getCurrentRoom() {
        return this.position.room();
    }

    /**
     * @return tous les items que le joueur possède dans son inventaire
     */
    public ItemList getItemList() {
        return this.aItemList;
    }

    @Override
    public boolean onUpdate() {
        boolean hasUpdate = super.onUpdate();
        return checkMovement() || hasUpdate;
    }

    private boolean checkMovement() {
        boolean hasUpdate = false;

        Set<FacingDirection> directions = this.aUserInterface.getPlayerInput().getMovements();
        for (FacingDirection direction : directions) {
            // Si on a 2 mouvements de sens opposé alors il vont s'annuler donc on évite de les calculer
            // Attention, si un deux 2 mouvements est cancel par une collision, le 2eme est censé prendre le dessus
            // Ici, on enlève ce comportement. A voir pour le remettre si quand on a un fps bas, ceci pose un probleme
            if (directions.contains(direction.getOpposite())) {
                continue;
            }
            this.move(direction);
            hasUpdate = true;
        }

        return hasUpdate;
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
    public boolean goRoom(final FacingDirection direction) {
        List<Door> vNextDoors = this.position.room().getExits(direction);
        if (vNextDoors.isEmpty()) {
            return false;
        } else if (vNextDoors.size() == 1) {
            this.onChangeRoom(vNextDoors.get(0));
        } else {
            // Si plusieurs salles, prends une aléatoire
            Random rand = new Random();
            int index = rand.nextInt(vNextDoors.size());
            this.onChangeRoom(vNextDoors.get(index));
        }

        return true;
    }

    /**
     * Fonction permettant d'aller dans la salle souhaiter
     * @param pRoom n'importe quelle pièce du jeu
     * @return boolean true s'il est dans la nouvelle piece else false
     */
    public boolean goRoom(Room pRoom) {
        this.aLastRooms.push(new Door(null, new Vector2(this.position.x(), this.position.y()), pRoom));
        //this.changeRoom(pRoom);
        return true;
    }

    @Override
    public void onChangeRoom(Door byDoor) {
        this.aLastRooms.push(new Door(null, new Vector2(this.position.x(), this.position.y()), this.position.room()));
        super.onChangeRoom(byDoor);
    }

    /**
     * Fonction permettant de retourner dans la dernière pièce visitée
     * @return true si le joueur a réussi à back else false
     */
    public boolean back() {
        if (this.aLastRooms.isEmpty()) {
            return false;
        }
        System.out.println("back");

        Door vLastDoor = this.aLastRooms.pop();
        Room vLastRoom = vLastDoor.getTo();
        System.out.println("vLastRoom = " + vLastRoom);
        if (!this.position.room().isExit(vLastRoom)) {
            return false;
        }

        // Met super et pas this car on veut pas que ça ajoute
        // la salle actuelle comme la dernière
        super.onChangeRoom(vLastDoor);
        return true;
    }

    /**
     * Fonction permettant de récupérer un item
     * @param pItemName le nom de l'item que le joueur souhaite prendre
     * @return true si le joueur a pris l'item else false
     */
    public boolean take(final String pItemName) {
        Item item = this.position.room().getItemList().getItemByName(pItemName);
        if (item == null) {
            return false;
        } else if (this.aItemList.getWeight() + item.getWeight() > this.aMaxWeight) {
            return false;
        }

        this.position.room().getItemList().removeItem(item);
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
        this.position.room().getItemList().addItem(item);

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

    public void onExecuteCommand(Command command, String[] params) {
        for (Entity entity : this.position.room().getEntities()) {
            if (entity instanceof MovingCharacter character) {
                character.onInteract(this);
            }
        }

        command.execute(this, params);
    }
}
