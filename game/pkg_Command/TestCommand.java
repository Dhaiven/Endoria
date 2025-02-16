package game.pkg_Command;

import game.pkg_Entity.pkg_Player.Player;

import java.io.*;

public class TestCommand extends Command {

    @Override
    public boolean execute(Player player, String secondWord) {
        if (secondWord == null) {
            player.getUserInterface().println("Quel fichier ?");
            return false;
        }

        File file = new File(secondWord + ".txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = br.readLine()) != null) {
                player.getGameEngine().interpretCommand(line);
            }
        } catch (FileNotFoundException e) {

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
