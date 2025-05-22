package game.pkg_Player.pkg_Ui;

import game.pkg_Command.CommandManager;
import game.pkg_Player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TerminalInterface implements ActionListener {

    private final Player player;
    private final CommandManager commandManager;

    private final JDialog aDialog;
    private final JTextField aEntryField;
    private final JTextArea aLog;

    private boolean enable = true;

    public TerminalInterface(JFrame ownerFrame, Player player, CommandManager commandManager) {
        this.player = player;
        this.commandManager = commandManager;

        // Création de la fenêtre secondaire (JDialog)
        this.aDialog = new JDialog(ownerFrame, "Terminal", false);
        this.aDialog.setSize(400, 300);
        this.aDialog.setLocationRelativeTo(ownerFrame); // Centre sur le premier JFrame
        this.aDialog.addKeyListener(player.getEventManager());

        // Zone de texte et champ d'entrée
        this.aEntryField = new JTextField(34);
        this.aEntryField.addActionListener(this);

        this.aLog = new JTextArea();
        this.aLog.setEditable(false);

        JScrollPane vListScroller = new JScrollPane(this.aLog);
        vListScroller.setPreferredSize(new Dimension(200, 200));
        vListScroller.setMinimumSize(new Dimension(100, 100));

        var aButton = new JButton("Help");
        aButton.addActionListener(e -> commandManager.getCommandExecutor().interpretCommand(player, "help"));

        // Création du panel pour la zone de log et champ d'entrée
        JPanel vPanel = new JPanel();
        vPanel.setLayout(new BorderLayout());
        vPanel.add(vListScroller, BorderLayout.CENTER); // Met le log dans le centre
        vPanel.add(this.aEntryField, BorderLayout.SOUTH);
        vPanel.add(aButton, BorderLayout.EAST);

        // Ajout des composants au JFrame
        this.aDialog.add(vPanel);

        this.aDialog.setVisible(true);
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;

        this.aDialog.setEnabled(enable);
        this.aDialog.setVisible(enable);
    }

    public void print(final String pText)
    {
        this.aLog.append(pText);
        this.aLog.setCaretPosition(this.aLog.getDocument().getLength());
    } // print(.)

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!enable) return;

        if (e.getSource() instanceof JButton) {
            //this.aEngine.interpretCommand("help");
            return;
        }
        // no need to check the type of action at the moment
        // because there is only one possible action (text input) :
        this.processCommand(); // never suppress this line
    }

    /**
     * A command has been entered in the entry field.
     * Read the command and do whatever is necessary to process it.
     */
    private void processCommand()
    {
        String vInput = this.aEntryField.getText();
        this.aEntryField.setText("");

        this.commandManager.getCommandExecutor().interpretCommand(this.player, vInput);
    } // processCommand()
}
