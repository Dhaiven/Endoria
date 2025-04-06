package game.pkg_Player.pkg_Interface;

import game.GameEngine;
import game.GameEngineV2;
import game.pkg_Util.InterfaceUtils;

import javax.swing.*;
import java.awt.*;

public class PausePanel extends JPanel {

    public PausePanel(Component focusPanel) {
        setSize(200, 300);
        setOpaque(false);
        setLayout(new GridBagLayout()); // Pour centrer les composants dans le panel



        JLabel label = InterfaceUtils.createLabel();
        label.setText("PAUSE");
        label.setHorizontalAlignment(JTextField.CENTER);

        JButton resumeButton = InterfaceUtils.createButton(focusPanel);
        resumeButton.setText("Reprendre");
        resumeButton.addActionListener(e -> GameEngineV2.getInstance().resume());

        JButton settingsButton = InterfaceUtils.createButton(focusPanel);
        settingsButton.setText("Paramètres");

        JButton quitButton = InterfaceUtils.createButton(focusPanel);
        quitButton.setText("QUITTER");
        quitButton.addActionListener(e -> GameEngineV2.getInstance().stop());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH; // Remplir l'espace disponible
        gbc.weightx = 1.0; // Poids horizontal
        gbc.weighty = 1.0; // Poids vertical
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(label, gbc);

        gbc.gridy = 1;
        add(resumeButton, gbc);

        gbc.gridy = 2;
        add(settingsButton, gbc);

        gbc.gridy = 3;
        add(quitButton, gbc);
    }
}