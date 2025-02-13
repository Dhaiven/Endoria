import java.io.*;
import java.util.List;
import java.util.Stack;

/**
 *  This class is part of the "World of Zuul" application.
 *  "World of Zuul" is a very simple, text based adventure game.
 *
 *  This class creates all rooms, creates the parser and starts
 *  the game.  It also evaluates and executes the commands that
 *  the parser returns.
 *
 * @author  Michael Kolling and David J. Barnes
 * @version 1.0 (Jan 2003) DB edited (2019)
 */
public class GameEngine
{
    private Parser        aParser;
    private UserInterface aGui;

    private Player aCurrentPlayer;

    /**
     * Constructor for objects of class GameEngine
     */
    public GameEngine()
    {
        this.aParser = new Parser();
        this.createRooms();
    }

    public void setGUI( final UserInterface pUserInterface )
    {
        this.aGui = pUserInterface;
        this.printWelcome();
    }

    /**
     * procédure affichant le message au marriage du jeu
     */
    private void printWelcome() {
        this.aGui.println("Welcome to the World of Zuul!\nYour goal: solve puzzles.\nType 'help' if you need help.\n\n");
        this.printLocationInfo();
    }

    /**
     * procédure affichant la room actualle ansi que toutes les sorties disponibles
     */
    private void printLocationInfo() {
        this.aGui.println(this.aCurrentPlayer.getCurrentRoom().getLongDescription());
        if (this.aCurrentPlayer.getCurrentRoom().getImageName() != null) {
            this.aGui.showImage(this.aCurrentPlayer.getCurrentRoom().getImageName());
        }
    }

    /**
     * Procédure pour créer toutes les rooms
     */
    private void createRooms() {
        Room main = new Room("Main Room", "images/mainImage.png");
        main.addItem(new Item("test", "1er item test", 2));
        main.addItem(new Item("arme", "une arme", 17));

        Room prehistoric = new Room("Prehistoric", "images/prehistoricImage.png");
        Room moyenAge = new Room("Moyen Age", "images/moyenAgeImage.png");
        Room antiquity = new Room("Antiquity", "images/antiquityImage.png");
        Room egypte = new Room("Egypte", "images/egypteImage.png");
        Room romaine = new Room("Romaine", "images/romanImage.png");
        Room grece = new Room("Grece", "images/greceImage.png");
        Room maya = new Room("Maya", "images/mayaImage.png");
        Room china = new Room("China", "images/chinaImage.png");

        main.setExit("north", moyenAge);
        main.setExit("south", maya);
        main.setExit("east", egypte);
        main.setExit("west", prehistoric);
        main.setExit("up", china);

        prehistoric.setExit("east", main);

        moyenAge.setExit("south", main);
        moyenAge.setExit("east", antiquity);

        antiquity.setExit("south", egypte);

        egypte.setExit("west", main);
        egypte.setExit("north", antiquity);
        egypte.setExit("south", grece);

        grece.setExit("north", egypte);
        grece.setExit("west", romaine);

        romaine.setExit("east", grece);

        maya.setExit("north", main);

        china.setExit("down", main);

        this.aCurrentPlayer = new Player("Joueur 1", main);
    }

    /**
     * Given a command, process (that is: execute) the command.
     * If this command ends the game, true is returned, otherwise false is
     * returned.
     */
    public void interpretCommand( final String pCommandLine ) {
        this.aGui.println( "> " + pCommandLine );
        Command vCommand = this.aParser.getCommand( pCommandLine );

        if ( vCommand.isUnknown() ) {
            this.aGui.println( "I don't know what you mean..." );
            return;
        }

        switch (vCommand.getCommandWord()) {
            case "help" -> this.printHelp();
            case "go" -> this.goRoom(vCommand);
            case "quit" -> {
                if ( vCommand.hasSecondWord() ) {
                    this.aGui.println( "Quit what?" );
                } else {
                    this.endGame();
                }
            }
            case "look" -> this.look(vCommand);
            case "eat" -> this.eat();
            case "back" -> this.back(vCommand);
            case "test" -> this.test(vCommand);
            case "take" -> {
                if (!vCommand.hasSecondWord()) {
                    this.aGui.println("Take what?");
                } else {
                    if (this.aCurrentPlayer.take(vCommand.getSecondWord())) {
                        this.printLocationInfo();
                    } else {
                        this.aGui.println("Cet item n'existe pas");
                    }
                }
            }
            case "drop" -> {
                if (!vCommand.hasSecondWord()) {
                    this.aGui.println("Drop what?");
                } else {
                    if (this.aCurrentPlayer.drop(vCommand.getSecondWord())) {
                        this.printLocationInfo();
                    } else {
                        this.aGui.println("Vous ne possédez pas cet item");
                    }
                }
            }
            default -> this.aGui.println("I don't know what you mean...");
        }
    }


    // implementations of user commands:

    /**
     * procédure affichant l'aide
     */
    private void printHelp() {
        this.aGui.println("\nYou are lost. You are alone.\n\nYour command words are:");
        this.aGui.println(aParser.getCommandString());
    }

    /**
     * Procédure permettant d'aller dans la salle souhaiter
     * après l'execution de la commande {@code go <nom de la salle>}
     * @param pCommand
     */
    private void goRoom(final Command pCommand) {
        if (!pCommand.hasSecondWord()) {
            this.aGui.println("Go where ?");
            return;
        }

        if (this.aCurrentPlayer.goRoom(pCommand.getSecondWord())) {
            this.printLocationInfo();
        } else {
            this.aGui.println("There is no door !");
        }
    }

    /**
     * procédure affichant la room actualle ansi que toutes les sorties disponibles
     */
    private void look(Command pCommand) {
        if (pCommand.hasSecondWord()) {
            Item actualItem = this.aCurrentPlayer.getCurrentRoom().getItemByName(pCommand.getSecondWord());
            if (actualItem != null) {
                this.aGui.println(actualItem.getLongDescription());
                return;
            }

            this.aGui.println("I don't know how to look at something in particular yet.");
            return;
        }

        this.aGui.println(this.aCurrentPlayer.getCurrentRoom().getLongDescription());
    }

    private void eat() {
        this.aGui.println("You have eaten now and you are not hungry any more.");
    }

    private void back(final Command pCommand) {
        if (pCommand.hasSecondWord()) {
            this.aGui.println("Cette commande n'accept pas de second paramètre");
            return;
        }

        if (this.aCurrentPlayer.back()) {
            this.printLocationInfo();
        } else {
            this.aGui.println("Vous ne pouvez plus revenir en arrière");
        }
    }

    private void test(final Command pCommand) {
        if (!pCommand.hasSecondWord()) {
            this.aGui.println("Quel fichier ?");
            return;
        }

        File file = new File(pCommand.getSecondWord() + ".txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;
            while ((line = br.readLine()) != null) {
                this.interpretCommand(line);
            }
        } catch (FileNotFoundException _) {} catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void endGame()
    {
        this.aGui.println( "Thank you for playing.  Good bye." );
        this.aGui.enable( false );
    }

}
