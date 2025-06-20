package game.player.ui;

import game.GameEngine;
import game.player.Player;
import game.player.ui.layer.GameLayer;
import game.player.ui.overlay.Overlay;
import game.player.ui.overlay.PauseOverlay;
import game.player.ui.overlay.SettingsOverlay;

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

    private Player player;

    private PauseOverlay pauseOverlay;
    private SettingsOverlay settingsOverlay;

    private final Stack<Overlay> lastOpenedOverlays = new Stack<>();

    /**
     * Construct a UserInterface. As a parameter, a Game Engine
     * (an object processing and executing the game commands) is
     * needed.
     */
    public UserInterface(Player player) {
        super("Jeux test");

        /**this.player = player;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        // Créer un JLayeredPane pour les overlays
        JLayeredPane overlayPane = new JLayeredPane();
        overlayPane.setOpaque(false);
        overlayPane.setLayout(null); // Nécessaire pour JLayeredPane

        //overlayPane.add(gameLayer);

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
        setVisible(true);

        requestFocus();*/
    } // UserInterface(.)

    public Player getPlayer() {
        return player;
    }

    public PauseOverlay getPauseOverlay() {
        return pauseOverlay;
    }

    public SettingsOverlay getSettingsOverlay() {
        return settingsOverlay;
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

        GameEngine.getInstance().pause();

        lastOpenedOverlays.add(overlay);
    }

    public void removeOverlay(Overlay overlay) {
        lastOpenedOverlays.remove(overlay);
        if (lastOpenedOverlays.isEmpty()) {
            GameEngine.getInstance().resume();
        } else {
            lastOpenedOverlays.peek().setVisible(true);
        }
        //GameEngine.getInstance().forceUpdate(overlay.getVisibleRect());
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
}
