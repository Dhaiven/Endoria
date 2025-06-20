package game.player;

import com.almasb.fxgl.texture.Texture;
import game.GameEngine;
import game.entity.*;
import game.item.ItemList;
import game.object.Position;
import game.player.action.Action;
import game.player.action.processor.ActionProcessorManager;
import game.player.ui.UserInterface;
import game.world.World;

import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.function.Function;

/**
 * Cette classe représente un Joueur
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class Player {

    private final UserInterface aUserInterface;
    private final World world;

    private final ItemList aItemList = new ItemList();

    private final PlayerSettings settings;
    private final PlayerEventListener eventManager;
    private final ActionProcessorManager actionProcessorManager;

    public Player(Function<Player, UserInterface> userInterface, Texture sprite, World world) {
        this(userInterface, sprite, new Rectangle2D.Double(0, 0, sprite.getWidth(), sprite.getHeight()), world);
    }

    // TODO: custom layer
    public Player(Function<Player, UserInterface> userInterface, Texture sprite, Rectangle2D rigidBody2D, World world) {
        //super("Player", sprite, rigidBody2D, new Position(100, 100, world), 1);

        this.world = world;
        this.eventManager = new PlayerEventListener(this);
        this.settings = new PlayerSettings();
        this.actionProcessorManager = new ActionProcessorManager();

        this.aUserInterface = userInterface.apply(this);
    }

    /**
     * @return l'interface utilisateur permettant d'afficher des messages
     */
    public UserInterface getUserInterface() {
        return this.aUserInterface;
    }

    /**
     * @return le monde qui contient toutes les pièces
     */
    public World getWorld() {
        return world;
    }

    public PlayerEventListener getEventManager() {
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
            if (GameEngine.getInstance().getGameState() != action.getState()) {
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
}
