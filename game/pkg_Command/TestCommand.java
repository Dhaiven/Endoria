package game.pkg_Command;

import game.GameEngineV2;
import game.pkg_Player.Player;

import java.io.*;

/**
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class TestCommand extends Command {

    public TestCommand() {
        super("test", "Permet de tester le bon fonctionnement du jeu");
    }

    @Override
    public boolean execute(Player player, String[] args) {
        if (args.length == 0) {
            player.getUserInterface().println("Quel fichier ?");
            return false;
        }

        File file = new File("game/" + args[0] + ".txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = br.readLine()) != null) {
                GameEngineV2.getInstance().getCommandManager().getCommandExecutor().interpretCommand(player, line, true);
            }
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}
