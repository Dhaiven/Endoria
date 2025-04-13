package game.pkg_Player.pkg_Ui.pkg_Widget;

import game.pkg_Player.Player;

import javax.swing.*;

/**
 * Classe de base pour les Widgets
 * Ce sont les éléments qui s'affichent en permanence sur l'interface
 * du joueur comme la hotbar, des points de vies...
 */
public class Widget extends JPanel {

    protected final Player player;

    public Widget(Player player) {
        this.player = player;
    }

    @Override
    public void setVisible(boolean aFlag) {
        if (aFlag == isVisible()) return;

        super.setVisible(aFlag);
        if (aFlag) {
            onDisplayed();
        } else {
            onHidden();
        }
    }

    protected void onDisplayed() {
    }

    protected void onHidden() {
    }
}