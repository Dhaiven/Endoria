package game.pkg_Player;

import game.GameEngineV2;
import game.pkg_Command.CommandManager;
import game.pkg_Entity.Entity;
import game.pkg_Object.Vector2;
import game.pkg_Room.Room;
import game.pkg_Tile.Tile;
import game.pkg_Util.pkg_Message.Message;
import game.pkg_Util.pkg_Message.options.FontOption;
import game.pkg_Util.pkg_Message.options.ForegroundColorOption;
import game.pkg_World.World;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

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

    private final TerminalInput terminalInput;

    private final List<Message> messages = Collections.synchronizedList(new ArrayList<Message>());

    /**
     * Construct a UserInterface. As a parameter, a Game Engine
     * (an object processing and executing the game commands) is
     * needed.
     */
    public UserInterface(Player player, CommandManager commandManager)
    {
        this.player = player;

        this.aMyFrame = new JFrame("Jeux test");
        this.aMyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.aMyFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.aMyFrame.setUndecorated(true);
        this.aMyFrame.setSize(700, 700);
        this.aMyFrame.add(this);

        this.aMyFrame.addKeyListener(player.getEventManager());
        this.aMyFrame.addWindowFocusListener(player.getEventManager());

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

        //Messages
        Iterator<Message> messageIterator = messages.iterator();
        while (messageIterator.hasNext()) {
            Message message = messageIterator.next();
            message.draw(g2d, getVisibleRect());
        }
    }

    public void sendTitle(String text, Message.Pos pos) {
        send(
                new Message(text, pos)
                        .add(new FontOption(new Font(Font.DIALOG, Font.BOLD, 50)))
                        //.add(Background.color(Color.BLACK).padding(Padding.all(20, 50)))
                        .add(new ForegroundColorOption(Color.WHITE))
        );
    }

    public void send(Message message) {
        messages.add(message);
        repaint();
    }

    public void pause() {
        if (GameEngineV2.getInstance().isPaused()) return;
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        GameEngineV2.getInstance().pause();
        sendTitle("PAUSE", Message.Pos.CENTER);
    }

    public void resume() {
        if (!GameEngineV2.getInstance().isPaused()) return;
        System.out.println("8888888888888888888888888888888888888888888888888888888");
        GameEngineV2.getInstance().resume();
        messages.clear(); // TODO: clear juste les msg du menu pause
    }
} // UserInterface
