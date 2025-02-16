package game.pkg_Entity;

import game.pkg_Entity.pkg_Player.Player;
import game.pkg_Room.Door;
import game.pkg_Room.Room;

import java.util.List;
import java.util.Random;

public class MovingCharacter extends Character {

    public MovingCharacter(String pName) {
        super(pName);
    }

    @Override
    public void onInteract(Player player) {
        Room actualRoom = player.getCurrentRoom();
        List<Door> exits = List.copyOf(actualRoom.getExits().values());

        Random rand = new Random();
        Door door = exits.get(rand.nextInt(exits.size()));

        actualRoom.removeCharacter(this);
        door.getTo().addCharacter(this);

        player.getUserInterface().println("Ce personnage vient de mystérieusement disparaitre...");
    }
}
