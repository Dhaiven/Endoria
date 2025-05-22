package game.pkg_Entity;

import game.pkg_Player.Player;
import game.pkg_Image.StaticSprite;
import game.pkg_Object.Position;
import game.pkg_Room.Door;
import game.pkg_Room.Room;

import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 * Classe représentant un personnage qui bouge
 * à chaque commande
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class MovingCharacter extends Character {

    public MovingCharacter(String name, StaticSprite sprite, Position position) {
        super(name, sprite, new Rectangle(0, 0, 0, 0), position, 1, FacingDirection.NORTH);
    }

    @Override
    public boolean canPaint(Graphics2D g2d) {
        return super.canPaint(g2d) && sprite != null;
    }

    @Override
    public void onInteract(Player player) {
        Room actualRoom = player.getCurrentRoom();
        List<Door> exits = List.copyOf(actualRoom.getExits(player.getFacing()));

        // Le personnage est "condamné"
        if (exits.isEmpty()) return;

        Random rand = new Random();
        Door door = exits.get(rand.nextInt(exits.size()));

        actualRoom.getEntities().remove(this);
        door.getTo().getEntities().add(this);

        player.getUserInterface().println("Ce personnage vient de mystérieusement disparaitre...");
    }
}
