public class Beamer extends Item {

    private boolean isFired = false;
    private Room aFiredRoom;

    public Beamer() {
        super("beamer", "Permet de se téléporter", 100);
    }

    public boolean isFired() {
        return this.isFired;
    }

    public void setFired(boolean fired) {
        this.isFired = fired;
    }

    public Room getFiredRoom() {
        return this.aFiredRoom;
    }

    public void setFiredRoom(Room aFiredRoom) {
        this.aFiredRoom = aFiredRoom;
    }
}
