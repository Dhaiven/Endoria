package game.pkg_Player.pkg_Interface;

import game.pkg_Command.CommandManager;
import game.pkg_Player.Player;

import javax.swing.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * This class implements a simple graphical user interface with a
 * text entry area, a text output area and an optional image.
 *
 * @author Michael Kolling
 * @version 1.0 (Jan 2003) DB edited (2023)
 */
public class UserInterface extends JFrame
{

    private final TerminalInterface terminalInput;

    private final GamePanel gamePanel;
    private final PausePanel pausePanel;

    /**
     * Construct a UserInterface. As a parameter, a Game Engine
     * (an object processing and executing the game commands) is
     * needed.
     */
    public UserInterface(Player player, CommandManager commandManager)
    {
        super("Jeux test");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);

        gamePanel = new GamePanel(player);
        gamePanel.setVisible(true);

        pausePanel = new PausePanel(this);
        pausePanel.setVisible(false);

        // Ajouter le panel de pause au panel de jeu
        gamePanel.add(pausePanel);
        // Ajouter le panel principal au JFrame
        add(gamePanel);

        addKeyListener(player.getEventManager());
        addWindowFocusListener(player.getEventManager());

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                pausePanel.setLocation(
                        getWidth() / 2 - pausePanel.getWidth() / 2,
                        getHeight() / 2 - pausePanel.getHeight() / 2
                );
            }
        });

        setVisible(true);

        this.terminalInput = new TerminalInterface(this, player, commandManager);

        requestFocus();
    } // UserInterface(.)

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public PausePanel getPausePanel() {
        return pausePanel;
    }

    public TerminalInterface getTerminalInterface() {
        return terminalInput;
    }

    // TODO: supp cette fonction est faire getPausePanel().setVisible(visible);
    public void setPausePanelVisible(boolean visible) {
        pausePanel.setVisible(visible);
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
    public void println( final String pText )
    {
        this.print( pText + "\n" );
    } // println(.)

    /**
     * Enable or disable input in the entry field.
     */
    public void enable( final boolean pOnOff )
    {
        this.terminalInput.setEnable( pOnOff );
    } // enable(.)
} // UserInterface
