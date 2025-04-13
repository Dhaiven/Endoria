package game.pkg_Player.pkg_Ui;

import game.GameEngineV2;
import game.pkg_Command.CommandManager;
import game.pkg_Player.Player;
import game.pkg_Player.pkg_Ui.pkg_Layer.GameLayer;
import game.pkg_Player.pkg_Ui.pkg_Overlay.Overlay;
import game.pkg_Player.pkg_Ui.pkg_Overlay.PauseOverlay;
import game.pkg_Player.pkg_Ui.pkg_Overlay.SettingsOverlay;

import javax.swing.*;
import java.awt.*;
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

    private final TerminalInterface terminalInput;

    private final GameLayer gamePanel;
    private final PauseOverlay pausePanel;
    private final SettingsOverlay settingsPanel;

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
        gamePanel = new GameLayer(player);
        add(gamePanel);

        // Créer un JLayeredPane pour les overlays
        JLayeredPane overlayPane = new JLayeredPane();
        overlayPane.setOpaque(false);
        overlayPane.setLayout(null); // Nécessaire pour JLayeredPane

        gamePanel.add(overlayPane);

        // Créer les overlays
        pausePanel = new PauseOverlay(this, this);
        settingsPanel = new SettingsOverlay(this, this);

        // Ajouter les overlays au layeredPane
        overlayPane.add(pausePanel, JLayeredPane.PALETTE_LAYER);
        overlayPane.add(settingsPanel, JLayeredPane.PALETTE_LAYER);

        // Cacher les overlays au démarrage
        pausePanel.setVisible(false);
        settingsPanel.setVisible(false);

        addKeyListener(player.getEventManager());
        addWindowFocusListener(player.getEventManager());

        setVisible(true);

        this.terminalInput = new TerminalInterface(this, player, commandManager);

        requestFocus();
    } // UserInterface(.)

    public Player getPlayer() {
        return player;
    }

    public GameLayer getGameOverlay() {
        return gamePanel;
    }

    public PauseOverlay getPauseOverlay() {
        return pausePanel;
    }

    public SettingsOverlay getSettingsPanel() {
        return settingsPanel;
    }

    public TerminalInterface getTerminalInterface() {
        return terminalInput;
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
            System.out.println(lastOpenedOverlays);
        } else {
            GameEngineV2.getInstance().pause();
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
    }

    public void closeOpenedOverlay() {
        lastOpenedOverlays.peek().setVisible(false);
    }

    /**
     * Print out some text into the text area.
     */
    public void print(final String pText)
    {
        this.terminalInput.print(pText);
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
        this.terminalInput.setEnable( pOnOff );
    } // enable(.)
} // UserInterface
