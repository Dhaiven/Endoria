package game.pkg_Room;

import game.GameEngineV2;
import game.pkg_Entity.FacingDirection;
import game.pkg_Object.Vector2;
import game.pkg_Object.Vector2i;
import game.pkg_Tile.Tile;

import java.awt.Shape;
import java.util.*;

/**
 *  Cette classe représente une pièce
 *  téléportant dans une pièce aléatoire quand on en sort
 *
 * @author  DEBELLE Hugp
 * @version 2.0 (Février 2025)
 */
public class TransporterRoom extends Room {

    public TransporterRoom(Shape shape, String name, Vector2 roomScale, Vector2 spawn) {
        this(shape, name, roomScale, new HashMap<>(), spawn);
    }

    public TransporterRoom(Shape shape, String name, Vector2 roomScale, Map<Integer, java.util.List<Map<Vector2i, Tile>>> tiles) {
        this(shape, name, roomScale, tiles, null);
    }

    public TransporterRoom(Shape shape, String name, Vector2 roomScale, Map<Integer, java.util.List<Map<Vector2i, Tile>>> tiles, Vector2 spawn) {
        super(shape, name, roomScale, tiles, spawn);
    }

    @Override
    public boolean isExit(Room pRoom) {
        return true;
    }

    @Override
    public List<Door> getExits(FacingDirection direction) {
        List<Door> exits = new ArrayList<>();
        exits.add(new RoomRandomizer().find());
        return exits;
    }

    @Override
    public Map<FacingDirection, List<Door>> getExits() {
        Set<FacingDirection> directions = super.getExits().keySet();
        Map<FacingDirection, List<Door>> exits = new HashMap<>();
        for (FacingDirection direction : directions) {
            List<Door> doors = new ArrayList<>();
            doors.add(new RoomRandomizer().find());
            exits.put(direction, doors);
        }

        return exits;
    }

    @Override
    public Door getExit(Vector2 position, FacingDirection direction) {
        return new RoomRandomizer().find();
    }

    public static class RoomRandomizer {
        public RoomRandomizer() { }

        /**
         * @return la porte aléatoire si alea est null else la porte vers la pièce spécifiée par aléa
         */
        public Door find() {
            if (GameEngineV2.getInstance().getAlea() != null) {
                for (Room room : GameEngineV2.getInstance().getPlayer().getWorld().getRooms()) {
                    if (room.getName().equals(GameEngineV2.getInstance().getAlea())) {
                        return new Door(null, GameEngineV2.getInstance().getPlayer().getPosition().vector2(), room);
                    }
                }

                return null;
            }

            Random random = new Random();
            List<Room> vRooms = GameEngineV2.getInstance().getPlayer().getWorld().getRooms();
            Room randomRoom = vRooms.get(random.nextInt(vRooms.size()));
            return new Door(null, GameEngineV2.getInstance().getPlayer().getPosition().vector2(), randomRoom);
        }
    }
}
