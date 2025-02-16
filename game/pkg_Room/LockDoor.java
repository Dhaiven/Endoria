package game.pkg_Room;

import game.Player;
import game.pkg_Item.Item;

/**
 *  Cette classe représente une porte fermée.
 *  Elle peut s'ouvrit que si le joueur possède la bonne clé
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class LockDoor extends Door {

    private final Item key;

    public LockDoor(Room from, Item key) {
        super(from);
        this.key = key;
    }

    @Override
    public boolean canPass(Player player) {
        Item playerKey = player.getItemList().getItemByName(key.getName());
        if (playerKey != null) {
            player.use(playerKey);
            return true;
        }

        return false;
    }
}
