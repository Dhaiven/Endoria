package game.pkg_Entity;

import game.pkg_Entity.pkg_Player.Player;

public class Character {

    private String aName;

    public Character(String pName) {
        this.aName = pName;
    }

    public String getName() {
        return this.aName;
    }

    public void onInteract(Player player) {

    }
}
