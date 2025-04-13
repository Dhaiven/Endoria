package game.pkg_Player.pkg_Ui.pkg_Overlay;

import game.GameState;
import game.pkg_Player.pkg_Action.Action;
import game.pkg_Player.pkg_Action.KeysMapper;
import game.pkg_Player.pkg_Ui.UserInterface;
import game.pkg_Util.InterfaceUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SettingsOverlay extends Overlay {

    private final Component focusPanel;

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

                Integer keyCode = userInterface.getPlayer().getSettings().getKeyFromAction(action);
                String keyText = KeysMapper.getKeyText(keyCode);

                gbc.gridx += 1;
                var editKeyButton = InterfaceUtils.createButton(focusPanel);
                editKeyButton.setText(keyText);
                editKeyButton.addActionListener(e -> {
                    var lastKeyListeners = focusPanel.getKeyListeners();
                    for (var keyListener : lastKeyListeners) {
                        focusPanel.removeKeyListener(keyListener);
                    }

                    focusPanel.addKeyListener(new KeyAdapter() {
                        @Override
                        public void keyPressed(KeyEvent e) {
                            System.out.println(e.getKeyCode());
                            userInterface.getPlayer().getSettings().setKeyFromAction(action, e.getKeyCode());

                            var actualKeyListeners = focusPanel.getKeyListeners();
                            for (var keyListener : actualKeyListeners) {
                                focusPanel.removeKeyListener(keyListener);
                            }

                            for (var keyListener : lastKeyListeners) {
                                focusPanel.addKeyListener(keyListener);
                            }

                            editKeyButton.setText(KeysMapper.getKeyText(e.getKeyCode()));
                        }
                    });
                });
                add(editKeyButton, gbc);

                gbc.gridx = 0;
                gbc.gridy += 1;
            }
        }
    }
}
