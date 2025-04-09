/**
 *  Cette classe représente une porte fermée.
 *  Elle peut s'ouvrit que si le joueur possède la bonne clé
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class LockDoor extends Door {

    private final Item aKey;

    public LockDoor(final Room pFrom, final Item pKey) {
        super(pFrom);
        this.aKey = pKey;
    }

    /**
     * Fonction permettant de savoir si le joueur peut passer la porte ou non
     *
     * @param player Le joueur qui veut passer
     * @return true si le joueur peut prendre la poerte else false
     */
    @Override
    public boolean canPass(final Player player) {
        Item vPlayerKey = player.getItemList().getItemByName(this.aKey.getName());
        if (vPlayerKey != null) {
            player.use(vPlayerKey);
            return true;
        }

        return false;
    }
}
