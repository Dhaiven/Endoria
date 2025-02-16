import java.util.ArrayList;
import java.util.List;

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

    private List<Room> aRooms = new ArrayList<>();
    private String alea = null;

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
        this.aCurrentPlayer.setUserInterface(pUserInterface);
        this.printWelcome();
    }

    /**
     * Procédure affichant le message au marriage du jeu
     */
    private void printWelcome() {
        this.aGui.println("Welcome to the World of Zuul!\nYour goal: solve puzzles.\nType 'help' if you need help.\n\n");
        this.printLocationInfo();
    }

    /**
     * Procédure affichant la room actualle ansi que toutes les sorties disponibles
     */
    public void printLocationInfo() {
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
        main.getItemList().addItem(new Item("test", "1er item test", 2));
        main.getItemList().addItem(new Item("arme", "une arme", 17));

        Room prehistoric = new Room("Prehistoric", "images/prehistoricImage.png");
        prehistoric.getItemList().addItem(new MagicCookie());

        Room moyenAge = new Room("Moyen Age", "images/moyenAgeImage.png");
        moyenAge.getItemList().addItem(new Beamer());

        Room antiquity = new Room("Antiquity", "images/antiquityImage.png");
        Room egypte = new Room("Egypte", "images/egypteImage.png");
        Room romaine = new TransporterRoom(this, "Romaine", "images/romanImage.png");
        Room grece = new Room("Grece", "images/greceImage.png");

        Room maya = new Room("Maya", "images/mayaImage.png");
        Item prehistoricKey = new Item("key", "prehistoric key", 1);
        maya.getItemList().addItem(prehistoricKey);

        Room china = new Room("China", "images/chinaImage.png");

        main.setExit("north", moyenAge);
        main.setExit("south", maya);
        main.setExit("east", egypte);
        main.setLockedExit("west", prehistoric, prehistoricKey);
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

        this.aRooms.add(main);
        this.aRooms.add(moyenAge);
        this.aRooms.add(antiquity);
        this.aRooms.add(egypte);
        this.aRooms.add(romaine);
        this.aRooms.add(grece);
        this.aRooms.add(maya);
        this.aRooms.add(china);
        this.aRooms.add(prehistoric);

        this.aCurrentPlayer = new Player("Joueur 1", main, 18);
    }

    /**
     * @return toutes les rooms créent
     */
    public List<Room> getRooms() {
        return this.aRooms;
    }

    /**
     * @return un String si il y a un alea else null
     */
    public String getAlea() {
        return alea;
    }

    /**
     * Given a command, process (that is: execute) the command.
     * If this command ends the game, true is returned, otherwise false is
     * returned.
     */
    public void interpretCommand(final String pCommandLine) {
        this.interpretCommand(pCommandLine, false);
    }

    /**
     * Given a command, process (that is: execute) the command.
     * If this command ends the game, true is returned, otherwise false is
     * returned.
     * @param inTest true si la commande est exécuté par la commande test else false
     */
    public void interpretCommand(final String pCommandLine, boolean inTest) {
        this.aGui.println( "> " + pCommandLine);
        Command vCommand = this.aParser.getCommand(pCommandLine);
        if (vCommand == null) {
            this.aGui.println("I don't know what you mean...");
        } else {
            vCommand.execute(this.aCurrentPlayer, vCommand.getSecondWord());
        }
    }
}
