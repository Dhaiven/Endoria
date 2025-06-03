package game.player.ui.overlay;

import game.GameState;
import game.player.action.Action;
import game.player.action.KeysMapper;
import game.player.ui.UserInterface;
import game.util.InterfaceUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SettingsOverlay extends Overlay {

    private final JPanel content;
    private boolean hasClickedOnEditButton = false;

    public SettingsOverlay(Component focusPanel, UserInterface userInterface) {
        super(userInterface);

        setSize(600, 700);
        setMaximumSize(new Dimension(600, 700));
        setLayout(new BorderLayout());

        content = new JPanel(new GridBagLayout());

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        focusPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setLocation(
                        focusPanel.getWidth() / 2 - getWidth() / 2,
                        focusPanel.getHeight() / 2 - getHeight() / 2
                );
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH; // Remplir l'espace disponible
        gbc.weightx = 1.0; // Poids horizontal
        gbc.weighty = 1.0; // Poids vertical
        gbc.gridx = 0;
        gbc.gridy = 0;

        JLabel inGameLabel = InterfaceUtils.createTitleLabel();
        inGameLabel.setText("En jeu");
        content.add(inGameLabel, gbc);
        gbc.gridy += 1;

        generateContentForState(GameState.PLAY, focusPanel, gbc);

        JLabel interfaceLabel = InterfaceUtils.createTitleLabel();
        interfaceLabel.setText("Menu");
        content.add(interfaceLabel, gbc);
        gbc.gridy += 1;

        generateContentForState(GameState.PAUSE, focusPanel, gbc);

        JLabel otherLabel = InterfaceUtils.createTitleLabel();
        otherLabel.setText("Autre");
        content.add(otherLabel, gbc);
        gbc.gridy += 1;

        generateContentForState(null, focusPanel, gbc);
    }

    private void generateContentForState(GameState state, Component focusPanel, GridBagConstraints gbc) {
        for (Action action : Action.values()) {
            if (state != action.getState()) continue;

            var actionLabel = InterfaceUtils.createLabel();
            actionLabel.setText(action.getName());
            content.add(actionLabel, gbc);
            gbc.gridx += 1;

            Integer[] keysCode = userInterface.getPlayer().getSettings().getKeyFromAction(action);
            for (int index = 0; index < keysCode.length; index++) {
                var editKeyButton = InterfaceUtils.createButton(focusPanel);

                Integer keyCode = keysCode[index];
                if (keyCode != null) {
                    String keyText = KeysMapper.getKeyText(keyCode);
                    editKeyButton.setText(keyText);
                }

                final int actualIndex = index;

                editKeyButton.addActionListener(e -> {
                    if (hasClickedOnEditButton) return;
                    hasClickedOnEditButton = true;

                    focusPanel.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            userInterface.getPlayer().getSettings().replaceKeyFromAction(action, actualIndex, e.getKeyCode());

                            editKeyButton.setText(KeysMapper.getKeyText(e.getKeyCode()));
                            focusPanel.removeKeyListener(this);
                            hasClickedOnEditButton = false;
                        }
                    });
                });

                content.add(editKeyButton, gbc);
                gbc.gridx += 1;
            }

            gbc.gridx = 0;
            gbc.gridy += 1;
        }
    }
}
