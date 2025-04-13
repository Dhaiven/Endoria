package game.pkg_Util;

import javax.swing.*;
import java.awt.*;

public class InterfaceUtils {

    public static JButton createButton(Component focusPanel) {
        JButton button = new JButton();
        button.setFocusable(false);
        button.setFont(new Font("Times New Roman", Font.BOLD, 20));

        button.setBackground(Color.GRAY);
        button.addChangeListener(evt -> {
            if (button.getModel().isPressed()) {
                button.setBackground(Color.DARK_GRAY);
            } else if (button.getModel().isRollover()) {
                button.setBackground(Color.DARK_GRAY);
            } else {
                button.setBackground(Color.GRAY);
            }
        });

        // Dès qu'on appuie sur le boutton, on se remet dans la entry de base
        // Permet d'éviter les bugs qui après avoir click sur un boutton,
        // fais que plus rien est détecté
        button.addActionListener(e -> focusPanel.requestFocusInWindow());

        return button;
    }

    public static JLabel createLabel() {
        JLabel text = new JLabel();
        text.setFocusable(false);
        text.setOpaque(false);
        Font font = new Font("Arial", Font.PLAIN, 30);
        text.setFont(font);
        text.setForeground(Color.BLACK);

        return text;
    }

    public static JLabel createTitleLabel() {
        JLabel text = new JLabel();
        text.setFocusable(false);
        text.setOpaque(false);
        Font font = new Font("Arial", Font.BOLD, 50);
        text.setFont(font);
        text.setForeground(Color.BLACK);

        return text;
    }
}
