package game.pkg_Room;

import game.pkg_Entity.pkg_Player.Player;
import game.pkg_Item.Item;

import java.awt.*;

/**
 *  Cette classe représente une porte fermée.
 *  Elle peut s'ouvrit que si le joueur possède la bonne clé
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class LockDoor extends Door {

    private final Item key;

    public LockDoor(Shape shape, Room to, Item key) {
        super(shape, to);
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
