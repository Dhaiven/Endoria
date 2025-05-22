package game.pkg_Player;

import game.GameEngineV2;
import game.pkg_Command.Command;
import game.pkg_Entity.*;
import game.pkg_Image.AnimatedSprite;
import game.pkg_Image.Sprite;
import game.pkg_Item.Item;
import game.pkg_Item.ItemList;
import game.pkg_Object.Position;
import game.pkg_Object.Vector2;
import game.pkg_Player.pkg_Action.Action;
import game.pkg_Player.pkg_Action.pkg_Processor.ActionProcessorManager;
import game.pkg_Player.pkg_Ui.UserInterface;
import game.pkg_Room.Door;
import game.pkg_Room.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/**
 * Cette classe représente un Joueur
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class Player extends Entity {

    private UserInterface aUserInterface;

    private Stack<Door> aLastRooms = new Stack<>();

    private ItemList aItemList = new ItemList();
    private int aMaxWeight = 100;

    private final PlayerSettings settings;
    private final PlayerEventManager eventManager;
    private final ActionProcessorManager actionProcessorManager;

    public Player(Function<Player, UserInterface> userInterface, Sprite sprite, Room room) {
        this(userInterface, sprite, new Rectangle2D.Double(0, 0, sprite.getWidth(), sprite.getHeight()), room);
    }

    // TODO: custom layer
    public Player(Function<Player, UserInterface> userInterface, Sprite sprite, Rectangle2D rigidBody2D, Room room) {
        super("Player", sprite, rigidBody2D, new Position(room.getSpawnPoint(), room), 1);

        this.eventManager = new PlayerEventManager(this);
        this.settings = new PlayerSettings();
        this.actionProcessorManager = new ActionProcessorManager();

        if (sprite instanceof AnimatedSprite animatedSprite) {
            animatedSprite.setObserver((oldImage, newImage) -> GameEngineV2.getInstance().forceUpdate(new Rectangle(
                    (int) (position.x() - sprite.getWidth() / 2d),
                    (int) (position.y() - sprite.getHeight()),
                    sprite.getWidth(),
                    sprite.getHeight()
            )));
        }

        this.aUserInterface = userInterface.apply(this);
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

    public PlayerEventManager getEventManager() {
        return eventManager;
    }

    public PlayerSettings getSettings() {
        return settings;
    }

    /**
     * @return tous les items que le joueur possède dans son inventaire
     */
    public ItemList getItemList() {
        return this.aItemList;
    }

    public void triggerKeys() {
        Iterator<Action> keysPressedIterator = this.getEventManager().getKeysPressed().iterator();
        while (keysPressedIterator.hasNext()) {
            Action action = keysPressedIterator.next();
            if (GameEngineV2.getInstance().getGameState() != action.getState()) {
                keysPressedIterator.remove();
                continue;
            }

            actionProcessorManager.getActionProcessor(action).onKeyPressed(this);
            if (!action.canSpam()) {
                keysPressedIterator.remove();
            }
        }

        for (Action action : this.getEventManager().getKeysReleased()) {
            actionProcessorManager.getActionProcessor(action).onKeyReleased(this);
        }
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
            return this.onChangeRoom(vNextDoors.get(0));
        }

        // Si plusieurs salles, prends une aléatoire
        Random rand = new Random();
        int index = rand.nextInt(vNextDoors.size());
        return this.onChangeRoom(vNextDoors.get(index));
    }

    /**
     * Fonction permettant d'aller dans la salle souhaiter
     * @param pRoom n'importe quelle pièce du jeu
     * @return boolean true s'il est dans la nouvelle piece else false
     */
    public boolean goRoom(Room pRoom) {
        return this.onChangeRoom(new Door(null, new Vector2(this.position.x(), this.position.y()), pRoom));
    }

    @Override
    public boolean onChangeRoom(Door byDoor) {
        if (!byDoor.canPass(this)) {
            return false;
        }

        Door door = new Door(null, new Vector2(this.position.x(), this.position.y()), this.getCurrentRoom());
        this.aLastRooms.push(door);
        super.onChangeRoom(byDoor);

        for (Entity entity : this.position.room().getEntities()) {
            if (entity instanceof MovingCharacter character) {
                character.onInteract(this);
            }
        }

        // TODO: remove sleep
        // We add sleep because we have a bug with command
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}
        SwingUtilities.invokeLater(() -> {
            GameEngineV2.getInstance().forceUpdate();
        });

        return true;
    }

    /**
     * Fonction permettant de retourner dans la dernière pièce visitée
     * @return true si le joueur a réussi à back else false
     */
    public boolean back() {
        System.out.println(aLastRooms);
        if (this.aLastRooms.isEmpty()) {
            return false;
        }

        Door vLastDoor = this.aLastRooms.pop();
        Room vLastRoom = vLastDoor.getTo();
        if (!this.position.room().isExit(vLastRoom)) {
            return false;
        }

        super.onChangeRoom(vLastDoor);

        for (Entity entity : this.position.room().getEntities()) {
            if (entity instanceof MovingCharacter character) {
                character.onInteract(this);
            }
        }

        // TODO: remove sleep
        // We add sleep because we have a bug with command
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}
        SwingUtilities.invokeLater(() -> {
            GameEngineV2.getInstance().forceUpdate();
        });

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
