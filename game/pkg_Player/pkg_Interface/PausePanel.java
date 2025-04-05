package game.pkg_Player.pkg_Interface;

import game.pkg_Util.InterfaceUtils;

import javax.swing.*;
import java.awt.*;

public class PausePanel extends JPanel {

    public PausePanel(Component focusPanel) {
        setSize(200, 300);
        setOpaque(false);
        setLayout(new GridBagLayout()); // Pour centrer les composants dans le panel

        JButton bouton1 = InterfaceUtils.createButton(focusPanel);
        bouton1.setText("Bouton 1");

        JButton bouton2 = InterfaceUtils.createButton(focusPanel);
        bouton2.setText("Bouton 2");

        JLabel label = InterfaceUtils.createLabel();
        label.setText("PAUSE");
        label.setHorizontalAlignment(JTextField.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH; // Remplir l'espace disponible
        gbc.weightx = 1.0; // Poids horizontal
        gbc.weighty = 1.0; // Poids vertical
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(label, gbc);

        gbc.gridy = 1;
        add(bouton1, gbc);

        gbc.gridy = 2;
        add(bouton2, gbc);
    }
}