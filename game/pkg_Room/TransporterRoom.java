package game.pkg_Room;

import game.GameEngine;
import game.pkg_Entity.FacingDirection;
import game.pkg_Object.Vector2;
import game.pkg_World.Layers;
import game.pkg_World.World;

import java.awt.*;
import java.util.List;
import java.util.Random;

/**
 *  Cette classe représente une pièce
 *  téléportant dans une pièce aléatoire quand on en sort
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
/**public class TransporterRoom extends Room {

    private GameEngine aEngine;

    /**public TransporterRoom(final GameEngine gameEngine, final String pDescription) {
        this(gameEngine, pDescription, null);
    }

    public TransporterRoom(final GameEngine gameEngine, final String pDescription, final String pImage)  {
        super(pDescription, pImage);
        this.aEngine = gameEngine;
    }

    public TransporterRoom(Shape shape, String name, Layers layers) {
        super(shape, name, layers);
    }

    @Override
    public Door getExit(Vector2 position, FacingDirection direction) {
        return new RoomRandomizer()
    }

    public static class RoomRandomizer {

        private final GameEngine aEngine;

        public RoomRandomizer(GameEngine aEngine) {
            this.aEngine = aEngine;
        }

        /**
         * @return la porte aléatoire si alea est null else la porte vers la pièce spécifiée par aléa

        public Door find() {
            if (aEngine.getAlea() != null) {
                for (Room room : aEngine.getRooms()) {
                    if (room.getName().equals(aEngine.getAlea())) {
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
*/