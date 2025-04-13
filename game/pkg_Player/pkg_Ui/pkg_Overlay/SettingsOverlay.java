package game.pkg_Player.pkg_Ui.pkg_Overlay;

import game.GameState;
import game.pkg_Player.pkg_Action.Action;
import game.pkg_Player.pkg_Action.KeysMapper;
import game.pkg_Player.pkg_Ui.UserInterface;
import game.pkg_Util.InterfaceUtils;

import java.awt.*;
import java.awt.event.*;

public class SettingsOverlay extends Overlay {

    private boolean hasClickedOnEditButton = false;

    public SettingsOverlay(Component focusPanel, UserInterface userInterface) {
        super(userInterface);

        setSize(600, 700);
        setOpaque(false);
        setLayout(new GridBagLayout()); // Pour centrer les composants dans le panel

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

        for (GameState state : GameState.values()) {
            var label = InterfaceUtils.createTitleLabel();
            label.setText(switch(state) {
                case PLAY -> "En jeu";
                default -> "Interface";
            });

            add(label, gbc);
            gbc.gridy += 1;

            for (Action action : Action.values()) {
                if (!action.getState().equals(state)) continue;

                var actionLabel = InterfaceUtils.createLabel();
                actionLabel.setText(action.getName());
                add(actionLabel, gbc);
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

                    add(editKeyButton, gbc);
                    gbc.gridx += 1;
                }

                gbc.gridx = 0;
                gbc.gridy += 1;
            }
        }
    }
}
