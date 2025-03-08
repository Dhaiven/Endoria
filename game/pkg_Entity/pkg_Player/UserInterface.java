package game.pkg_Entity.pkg_Player;

import game.pkg_Command.CommandManager;
import game.pkg_Entity.Entity;
import game.pkg_Object.Vector2;
import game.pkg_Room.Room;
import game.pkg_Tile.Tile;
import game.pkg_World.World;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * This class implements a simple graphical user interface with a
 * text entry area, a text output area and an optional image.
 *
 * @author Michael Kolling
 * @version 1.0 (Jan 2003) DB edited (2023)
 */
public class UserInterface extends JPanel
{
    private final Player player;

    private final JFrame aMyFrame;

    private final PlayerInput playerInput;
    private final TerminalInput terminalInput;


    /**
     * Construct a UserInterface. As a parameter, a Game Engine
     * (an object processing and executing the game commands) is
     * needed.
     */
    public UserInterface(Player player, CommandManager commandManager)
    {
        this.player = player;
        this.playerInput = new PlayerInput(player);

        this.aMyFrame = new JFrame("Jeux test");
        this.aMyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.aMyFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.aMyFrame.setUndecorated(true);
        this.aMyFrame.setSize(700, 700);
        this.aMyFrame.add(this);
        this.aMyFrame.addKeyListener(this.playerInput);

        this.aMyFrame.setVisible(true);

        this.terminalInput = new TerminalInput(this.aMyFrame, player, commandManager);

        this.aMyFrame.requestFocus();
    } // UserInterface(.)

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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        //Met le background en noir
        g2d.setBackground(Color.BLACK);
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());

        Room room = player.getPosition().room();
        for (int layer : World.LAYERS) {
            for (Map.Entry<Vector2, Tile> entry : room.getWorldsTilesCacheAtLayer(layer).entrySet()) {
                entry.getValue().paint(g2d, entry.getKey());
            }

            for (Entity entity : room.getEntities()) {
                if (entity.getLayer() == layer) {
                    entity.paint(g2d);
                }
            }
        }
    }
} // UserInterface
