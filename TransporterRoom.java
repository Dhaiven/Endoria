import java.util.List;
import java.util.Random;

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
        return new RoomRandomizer().find(this.aEngine.getRooms());
    }

    public static class RoomRandomizer {

        public Door find(List<Room> rooms) {
            Random random = new Random();
            Room randomRoom = rooms.get(random.nextInt(rooms.size()));
            return new Door(randomRoom);
        }
    }
}
