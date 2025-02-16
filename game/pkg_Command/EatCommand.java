package game.pkg_Command;

import game.Player;
import game.pkg_Item.Item;

public class EatCommand extends Command {

    @Override
    public boolean execute(Player player, String secondWord) {
        if (secondWord == null) {
            player.getUserInterface().println("Eat what ?");
            return false;
        }

        Item item = player.getItemList().getItemByName(secondWord);
        if (item == null) {
            player.getUserInterface().println("Vous ne possédez pas cet item.");
            return false;
        }

        player.use(item);
        player.getUserInterface().println("Vous venez de manger cette item");
        return true;
    }
}
