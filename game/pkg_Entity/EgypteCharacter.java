package game.pkg_Entity;

import game.pkg_Player.Player;

/**
 * Classe représentant le PNJ dans
 * la pièce Egypte
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class  EgypteCharacter extends Character {

    public EgypteCharacter() {
        super(null, null, 0);
    }

    @Override
    public String getName() {
        return "egypte";
    }

    @Override
    public void onInteract(Player player) {
        player.getUserInterface().println("Bienvenue dans la pièce Egypte !");
        player.getUserInterface().println("As-tu visité la pièce préhistoric ?");
        player.getUserInterface().println("Des rummeurs circulent disant qu'elle se setirait à droite");
        player.getUserInterface().println("de l'entrée, mais j'ai jamais réussi à savoir la vérité");
    }
}
