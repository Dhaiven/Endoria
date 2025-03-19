package game.pkg_Entity;

import game.pkg_Entity.pkg_Player.Player;
import game.pkg_Image.StaticSprite;
import game.pkg_Object.Position;
import game.pkg_Room.Door;
import game.pkg_Room.Room;

import javax.swing.*;
import java.util.List;
import java.util.Random;

/**
 * Classe représentant un personnage qui bouge
 * à chaque commande
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public abstract class MovingCharacter extends Character {

    public MovingCharacter(JComponent paintedOn, StaticSprite sprite, Position position) {
        super(paintedOn, sprite, position, 2);
    }

    @Override
    public void onInteract(Player player) {
        Room actualRoom = player.getCurrentRoom();
        List<Door> exits = List.copyOf(actualRoom.getExits(player.getFacing()));

        Random rand = new Random();
        Door door = exits.get(rand.nextInt(exits.size()));

        actualRoom.getEntities().remove(this);
        door.getTo().getEntities().add(this);

        player.getUserInterface().println("Ce personnage vient de mystérieusement disparaitre...");
    }
}
