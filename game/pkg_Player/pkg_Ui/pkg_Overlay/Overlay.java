package game.pkg_Player.pkg_Ui.pkg_Overlay;

import game.pkg_Player.pkg_Ui.UserInterface;

import javax.swing.*;

/**
 * Classe de base pour les Overlay
 * Ce sont les éléments qui s'affichent au dessus de l'interface
 * du joueur mais dans la même fenêtre. Elle bloque le plus souvent
 * la scene de jeu.
 * Par example, on retrouve le menu pause, les options...
 */
public abstract class Overlay extends JPanel {

    protected final UserInterface userInterface;

    public Overlay(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    @Override
    public void setVisible(boolean aFlag) {
        System.out.println(aFlag);
        if (aFlag == isVisible()) return;

        super.setVisible(aFlag);
        if (aFlag) {
            onShow();
        } else {
            onHide();
        }
    }

    protected void onShow() {
        userInterface.addOverlay(this);
    }

    protected void onHide() {
        userInterface.removeOverlay(this);
    }
}
