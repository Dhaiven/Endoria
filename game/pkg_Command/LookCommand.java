package game.pkg_Command;

import game.Player;
import game.pkg_Item.Item;

public class LookCommand extends Command {

    @Override
    public boolean execute(Player player, String secondWord) {
        if (secondWord != null) {
            Item actualItem = player.getCurrentRoom().getItemList().getItemByName(secondWord);
            if (actualItem != null) {
                player.getUserInterface().println(actualItem.getLongDescription());
                return true;
            }

            player.getUserInterface().println("I don't know how to look at something in particular yet.");
            return true;
        }

        player.getUserInterface().println(player.getCurrentRoom().getLongDescription());
        return true;
    }
}
