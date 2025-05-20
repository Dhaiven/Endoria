package game.pkg_Player.pkg_Ui;

import game.GameEngineV2;
import game.pkg_Command.CommandManager;
import game.pkg_Player.Player;
import game.pkg_Player.pkg_Ui.pkg_Layer.GameLayer;
import game.pkg_Player.pkg_Ui.pkg_Overlay.Overlay;
import game.pkg_Player.pkg_Ui.pkg_Overlay.PauseOverlay;
import game.pkg_Player.pkg_Ui.pkg_Overlay.SettingsOverlay;

import javax.swing.*;
import java.util.Stack;

/**
 * This class implements a simple graphical user interface with a
 * text entry area, a text output area and an optional image.
 *
 * @author Michael Kolling
 * @version 1.0 (Jan 2003) DB edited (2023)
 */
public class UserInterface extends JFrame
{

    private final Player player;

    private final TerminalInterface terminalInterface;

    private final GameLayer gameLayer;
    private final PauseOverlay pauseOverlay;
    private final SettingsOverlay settingsOverlay;

    private final Stack<Overlay> lastOpenedOverlays = new Stack<>();

    /**
     * Construct a UserInterface. As a parameter, a Game Engine
     * (an object processing and executing the game commands) is
     * needed.
     */
    public UserInterface(Player player, CommandManager commandManager)
    {
        super("Jeux test");

        this.player = player;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        // Créer le panel de jeu qui sera toujours visible
        gameLayer = new GameLayer(player);

        // Créer un JLayeredPane pour les overlays
        JLayeredPane overlayPane = new JLayeredPane();
        overlayPane.setOpaque(false);
        overlayPane.setLayout(null); // Nécessaire pour JLayeredPane

        overlayPane.add(gameLayer);

        // Créer les overlays
        pauseOverlay = new PauseOverlay(this, this);
        settingsOverlay = new SettingsOverlay(this, this);

        // Ajouter les overlays au layeredPane
        overlayPane.add(pauseOverlay, JLayeredPane.PALETTE_LAYER);
        overlayPane.add(settingsOverlay, JLayeredPane.PALETTE_LAYER);

        // Cacher les overlays au démarrage
        pauseOverlay.setVisible(false);
        settingsOverlay.setVisible(false);

        add(overlayPane);

        addKeyListener(player.getEventManager());
        addWindowFocusListener(player.getEventManager());

        setVisible(true);

        this.terminalInterface = new TerminalInterface(this, player, commandManager);

        requestFocus();
    } // UserInterface(.)

    public Player getPlayer() {
        return player;
    }

    public GameLayer getGameLayer() {
        return gameLayer;
    }

    public PauseOverlay getPauseOverlay() {
        return pauseOverlay;
    }

    public SettingsOverlay getSettingsOverlay() {
        return settingsOverlay;
    }

    public TerminalInterface getTerminalInterface() {
        return terminalInterface;
    }

    public void addOverlay(Overlay overlay) {
        if (lastOpenedOverlays.contains(overlay)) {
            return;
        }

        if (!lastOpenedOverlays.isEmpty()) {
            Overlay lastOverlay = lastOpenedOverlays.peek();
            int lastOverlayIndex = lastOpenedOverlays.indexOf(lastOverlay);

            // This method remove the overlay
            lastOpenedOverlays.peek().setVisible(false);
            // So we readd the last overlay here
            lastOpenedOverlays.add(lastOverlayIndex, lastOverlay);
        }

        GameEngineV2.getInstance().pause();

        lastOpenedOverlays.add(overlay);
    }

    public void removeOverlay(Overlay overlay) {
        lastOpenedOverlays.remove(overlay);
        if (lastOpenedOverlays.isEmpty()) {
            GameEngineV2.getInstance().resume();
        } else {
            lastOpenedOverlays.peek().setVisible(true);
        }
        GameEngineV2.getInstance().forceUpdate(overlay.getVisibleRect());
    }

    public void closeOpenedOverlay() {
        lastOpenedOverlays.peek().setVisible(false);
    }

    public void closeAllOpenedOverlays() {
        Stack<Overlay> toHide = new Stack<>();
        toHide.addAll(lastOpenedOverlays);

        for (int i = toHide.size() - 1; i >= 0; i--) {
            toHide.get(i).setVisible(false);
        }
    }

    @Override
    public void repaint(long time, int x, int y, int width, int height) {
        getGameLayer().repaint(time, x, y, width, height);
        super.repaint(time, x, y, width, height);
    }

    /**
     * Print out some text into the text area.
     */
    public void print(final String pText)
    {
        this.terminalInterface.print(pText);
    } // print(.)

    /**
     * Print out some text into the text area, followed by a line break.
     */
    public void println(final String pText)
    {
        this.print(pText + "\n");
    } // println(.)

    /**
     * Enable or disable input in the entry field.
     * TODO: rename (c'est une fonction de base du prof)
     */
    public void enable(final boolean pOnOff)
    {
        this.terminalInterface.setEnable( pOnOff );
    } // enable(.)
} // UserInterface
