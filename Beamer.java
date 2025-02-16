/**
 *  Cette classe implémente le Beamer, un item qu'on doit charger dans une salle
 *  et quand il est chargé, on tire et il nous téléporte dans la salle dans Raquel,
 *  on l'a chargé.
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class Beamer extends Item {

    private boolean isFired = false;
    private Room aFiredRoom;

    public Beamer() {
        super("beamer", "Permet de se téléporter", 100);
    }

    /**
     * @return {@code true} si le beamer est chargé else {@code false}
     */
    public boolean isFired() {
        return this.isFired;
    }

    /**
     * Cette méthode doit être appellé en même temps que {@link #setFiredRoom(Room)}
     * @param fired true charge le beamer, false le décharge
     */
    public void setFired(boolean fired) {
        this.isFired = fired;
    }

    /**
     * Pour savoir si un beamer est chargé, {@link #isFired()}
     * @return {@code Room} si le beamer est chargé else {@code null}
     */
    public Room getFiredRoom() {
        return this.aFiredRoom;
    }

    /**
     * Cette méthode doit être appellé en même temps que {@link #setFired(boolean)}
     * @param aFiredRoom Room met le room, null reset la room
     */
    public void setFiredRoom(Room aFiredRoom) {
        this.aFiredRoom = aFiredRoom;
    }
}
