 
/**
 * Classe Game - le moteur du jeu d'aventure Zuul.
 *
 * @author DEBELLE Hugo
 */
public class Game
{

    private Parser aParser;
    private Room aCurrentRoom;

    /**
     * Constructors de la classe Game
     */
    public Game() {
        this.aParser = new Parser();
        this.createRooms();
    }

    /**
     * Procédure pour créer toutes les rooms
     */
    private void createRooms() {
        Room main = new Room("Main Room");
        Room prehistoric = new Room("Prehistoric");
        Room moyenAge = new Room("Moyen Age");
        Room antiquity = new Room("Antiquity");
        Room egypte = new Room("Egypte");
        Room romaine = new Room("Romaine");
        Room grece = new Room("Grece");
        Room maya = new Room("Maya");
        Room china = new Room("China");

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
        
        this.aCurrentRoom = main;
    }

    /**
     * Procédure permettant d'aller dans la salle souhaiter
     * après l'execution de la commande {@code go <nom de la salle>}
     * @param pCommand
     */
    private void goRoom(final Command pCommand) {
        if (!pCommand.hasSecondWord()) {
            System.out.println("Go where ?");
            return;
        }
        
        Room vNextRoom = this.aCurrentRoom.getExit(pCommand.getSecondWord());
        if (vNextRoom == this.aCurrentRoom) {
            System.out.println("Unknown direction !");
            return;
        }
        if (vNextRoom == null) {
            System.out.println("There is no door !");
            return;
        }
        
        this.aCurrentRoom = vNextRoom;
        this.printLocationInfo();
    }

    /**
     * procédure affichant le message au marriage du jeu
     */
    private void printWelcome() {
        System.out.println("Welcome to the World of Zuul!\nYour goal: solve puzzles.\nType 'help' if you need help.\n\n");
        this.printLocationInfo();
    }

    /**
     * procédure affichant l'aide
     */
    private void printHelp() {
        System.out.println("\nYou are lost. You are alone.\n\nYour command words are:");
        System.out.println(aParser.getCommands());
    }

    /**
     * procédure affichant la room actualle ansi que toutes les sorties disponibles
     */
    private void printLocationInfo() {
        System.out.println(this.aCurrentRoom.getLongDescription());
    }

    /**
     * Procédure permanent de stopper le jeu
     */
    private boolean quit(final Command pCommand) {
        if (!pCommand.hasSecondWord()) {
            System.out.println("Quit what ?");
            return false;
        }
        
        return true;
    }

    /**
     * procédure affichant la room actualle ansi que toutes les sorties disponibles
     */
    private void look(Command pCommand) {
        if (pCommand.hasSecondWord()) {
            System.out.println("I don't know how to look at something in particular yet.");
            return;
        }

        System.out.println(this.aCurrentRoom.getLongDescription());
    }

    private void eat() {
        System.out.println("You have eaten now and you are not hungry any more.");
    }

    /**
     * procédure permettant d'exevuter une méthode en fonction de la commande tapé
     * @param pCommand
     * @return true si le jeu dois se stoper sinon false
     */
    private boolean processCommand(final Command pCommand) {
        if (pCommand.isUnknown()) {
            System.out.println("I don't know what you mean...");
            return false;
        }
        
        switch (pCommand.getCommandWord()) {
            case "help" -> this.printHelp();
            case "go" -> this.goRoom(pCommand);
            case "quit" -> {
                return this.quit(pCommand);
            }
            case "look" -> this.look(pCommand);
            case "eat" -> this.eat();
            default -> System.out.println("I don't know what you mean...");
        }
        
        return false;
    }

    /**
     * procédure qui lance le jeu et check si une commande a été tapé
     */
    public void play() {
        this.printWelcome();
        boolean vFinished = false;
        
        while (!vFinished) {
            Command command = this.aParser.getCommand();
            vFinished = this.processCommand(command);
        }
        
        System.out.println("Thank you for playing.  Good bye.");
    }
} // Game
