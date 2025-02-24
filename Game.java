import game.pkg_Command.CommandManager;
import game.pkg_Entity.FacingDirection;
import game.pkg_Entity.pkg_Player.Player;
import game.pkg_Entity.pkg_Player.UserInterface;
import game.pkg_Object.Position;
import game.pkg_Entity.Sprite;
import game.pkg_Object.Vector2;
import game.pkg_Util.FileUtils;
import game.pkg_World.WorldManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class Game extends JPanel {

    public Game() {
        CommandManager commandManager = new CommandManager();

        try {
            var playerSprite = ImageIO.read(new File(FileUtils.ASSETS_RESOURCES + "player.png"));
            var worldManager = new WorldManager();
            Player player = new Player(player1 -> new UserInterface(player1, commandManager), new Sprite(playerSprite.getSubimage(0, 0, 64, 64)), worldManager.getWorld("museum").getSpawnRoom());
            player.spawn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Game();
    }
}
