package game.pkg_Command;

import game.GameEngineV2;
import game.pkg_Entity.FacingDirection;
import game.pkg_Player.Player;
import game.pkg_Room.Door;

import java.util.List;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class GoCommand extends Command {

    public GoCommand() {
        super("go", "Permet d'aller dans une salle en fonction de la direction souhaité");
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length == 0) {
            player.getUserInterface().println("Go where ?");
            return false;
        }

        FacingDirection direction = FacingDirection.from(args[0]);
        if (direction == null) {
            player.getUserInterface().println("La direction " + args[0] + " n'existe pas");
            return false;
        }

        List<Door> vNextDoors = player.getPosition().room().getExits(direction);
        if (vNextDoors.isEmpty()) {
            player.getUserInterface().println("Il n'y a pas de porte dans cette direction");
            return false;
        } else if (vNextDoors.size() == 1) {
            return success(player, vNextDoors.get(0));
        }

        // Si plusieurs salles
        if (args.length == 1) {
            player.getUserInterface().println("Il y a plusieurs portes dans cette direction");
            player.getUserInterface().println("Ajouter un paramètre parmis les salles suivante: ");
            for (Door door : vNextDoors) {
                player.getUserInterface().print(door.getTo().getName() + " ");
            }

            return false;
        }

        for (Door door : vNextDoors) {
            if (door.getTo().getName().equals(args[1])) {
                return success(player, door);
            }
        }

        player.getUserInterface().println("La salle  " + args[1] + " vers " + direction.getName() + " n'existe pas");
        return false;
    }

    private boolean success(Player player, Door door) {
        player.onChangeRoom(door);
        player.getUserInterface().println("Vous venez d'être téléporté !");
        //player.getGameEngine().printLocationInfo();
        return true;
    }
}
