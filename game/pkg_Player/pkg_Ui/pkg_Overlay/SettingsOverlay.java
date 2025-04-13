package game.pkg_Player.pkg_Ui.pkg_Overlay;

import game.GameState;
import game.pkg_Player.pkg_Action.Action;
import game.pkg_Player.pkg_Action.KeysMapper;
import game.pkg_Player.pkg_Ui.UserInterface;
import game.pkg_Util.InterfaceUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SettingsOverlay extends Overlay {

    private final Component focusPanel;

    private boolean hasClickedOnEditButton = false;

    public SettingsOverlay(Component focusPanel, UserInterface userInterface) {
        super(userInterface);
        this.focusPanel = focusPanel;
    }

    @Override
    public String getId() {
        return "settings";
    }

    @Override
    public void addNotify() {
        super.addNotify();

        create();
    }

    private void create() {
        setSize(500, 700);
        setOpaque(false);
        setLayout(new GridBagLayout()); // Pour centrer les composants dans le panel

        getParent().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setLocation(
                        getParent().getWidth() / 2 - getWidth() / 2,
                        getParent().getHeight() / 2 - getHeight() / 2
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
            var label = InterfaceUtils.createLabel();
            label.setText(switch(state) {
                case PLAY -> "En jeu";
                default -> "Interface";
            });

            add(label, gbc);
            gbc.gridy += 1;

            for (Action action : Action.values()) {
                if (!action.getState().equals(state)) continue;

                var button = InterfaceUtils.createButton(focusPanel);
                button.setText(action.getName());
                add(button, gbc);
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
