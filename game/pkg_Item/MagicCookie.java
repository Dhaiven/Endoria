package game.pkg_Item;

import game.pkg_Player.Player;

/**
 *  Cette classe représente un MagicCookie
 *  Un item permettant de mettre le poids maximum du
 *  joueur à 100.
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class MagicCookie extends Item {

    public MagicCookie() {
        super(null, "cookie", "Un cookie magique qui augmente la poids max", 5);
    }

    @Override
    public void onUse(Player player) {
        super.onUse(player);
        player.setMaxWeight(player.getMaxWeight() + 100);
    }
}
