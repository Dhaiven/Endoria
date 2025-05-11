package game.pkg_Object;

public enum DrawType {
    UNDER,
    ABOVE;

    public static DrawType from(String id) {
        id = id.toUpperCase();
        for (DrawType type : DrawType.values()) {
            if (type.name().equals(id)) {
                return type;
            }
        }

        return null;
    }
}
