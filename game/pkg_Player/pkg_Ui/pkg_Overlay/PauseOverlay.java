package game.pkg_Player.pkg_Ui.pkg_Overlay;

import game.GameEngineV2;
import game.pkg_Player.pkg_Ui.UserInterface;
import game.pkg_Util.InterfaceUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class PauseOverlay extends Overlay {

    public PauseOverlay(Component focusPanel, UserInterface userInterface) {
        super(userInterface);

        setSize(200, 300);
        setOpaque(false);
        setLayout(new GridBagLayout());

        focusPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setLocation(
                        focusPanel.getWidth() / 2 - getWidth() / 2,
                        focusPanel.getHeight() / 2 - getHeight() / 2
                );
            }
        });

        JLabel label = InterfaceUtils.createTitleLabel();
        label.setText("PAUSE");
        label.setHorizontalAlignment(JTextField.CENTER);

        JButton resumeButton = InterfaceUtils.createButton(focusPanel);
        resumeButton.setText("Reprendre");
        resumeButton.addActionListener(e -> setVisible(false));

        JButton settingsButton = InterfaceUtils.createButton(focusPanel);
        settingsButton.setText("Paramètres");
        settingsButton.addActionListener(e -> userInterface.getSettingsOverlay().setVisible(true));

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