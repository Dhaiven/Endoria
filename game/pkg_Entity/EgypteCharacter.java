package game.pkg_Entity;

import game.pkg_Entity.pkg_Player.Player;

public class EgypteCharacter extends Character {

    public EgypteCharacter(String pName) {
        super(pName);
    }

    @Override
    public void onInteract(Player player) {
        player.getUserInterface().println("Bienvenue dans la pièce Egypte !");
        player.getUserInterface().println("As-tu visité la pièce préhistoric ?");
        player.getUserInterface().println("Des rummeurs circulent disant qu'elle se setirait à droite");
        player.getUserInterface().println("de l'entrée, mais j'ai jamais réussi à savoir la vérité");
    }
}
