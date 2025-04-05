package game.pkg_Util;

import javax.swing.*;
import java.awt.*;

public class InterfaceUtils {

    public static JButton createButton(Component focusPanel) {
        JButton button = new JButton();
        button.setFont(new Font("Times New Roman", Font.BOLD, 20));
        button.setBackground(Color.DARK_GRAY);

        button.addActionListener(e -> focusPanel.requestFocusInWindow());

        return button;
    }

    public static JLabel createLabel() {
        JLabel text = new JLabel();
        text.setFocusable(false);
        text.setOpaque(false);
        Font font = new Font("Arial", Font.BOLD, 50);
        text.setFont(font);
        text.setForeground(Color.BLACK);

        return text;
    }
}
