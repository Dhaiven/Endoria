import java.util.List;
import java.util.Random;

/**
 *  Cette classe représente une pièce
 *  téléportant dans une pièce aléatoire quand on en sort
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class TransporterRoom extends Room {

    private final GameEngine aEngine;

    public TransporterRoom(final GameEngine gameEngine, final String pDescription) {
        this(gameEngine, pDescription, null);
    }

    public TransporterRoom(final GameEngine gameEngine, final String pDescription, final String pImage)  {
        super(pDescription, pImage);
        this.aEngine = gameEngine;
    }

    @Override
    public Door getExit(String pDirection) {
        return new RoomRandomizer(this.aEngine).find();
    }

    public static class RoomRandomizer {

        private final GameEngine aEngine;

        public RoomRandomizer(GameEngine aEngine) {
            this.aEngine = aEngine;
        }

        /**
         * @return la porte aléatoire si alea est null else la porte vers la pièce spécifiée par aléa
         */
        public Door find() {
            if (aEngine.getAlea() != null) {
                for (Room room : aEngine.getRooms()) {
                    if (room.getDescription().equals(aEngine.getAlea())) {
                        return new Door(room);
                    }
                }

                return null;
            }

            Random random = new Random();
            List<Room> vRooms = this.aEngine.getRooms();
            Room randomRoom = vRooms.get(random.nextInt(vRooms.size()));
            return new Door(randomRoom);
        }
    }
}
