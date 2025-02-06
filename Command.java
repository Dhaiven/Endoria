 
/**
 * Classe Command - une commande du jeu d'aventure Zuul.
 *
 * @author Alban Fournier--Cibray
 */
public class Command
{ 
    private String aCommandWord;
    private String aSecondWord;
    
    public Command(final String pCommandWord, final String pSecondWord) {
        this.aCommandWord = pCommandWord;
        this.aSecondWord = pSecondWord;
    }

    /**
     * @return le premier mot taper dans la commande
     */
    public String getCommandWord() {
        return this.aCommandWord;
    }

    /**
     * @return le deuxième mot taper dans la commande
     */
    public String getSecondWord() {
        return this.aSecondWord;
    }

    /**
     * @return true si la commande contient un deuxieme mot
     */
    public boolean hasSecondWord() {
        return this.aSecondWord != null;
    }

    /**
     * @return true si la commande ne contient pas de premier mot
     */
    public boolean isUnknown() {
        return this.aCommandWord == null;
    }
}// Command
