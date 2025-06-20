package game;

import game.util.FileUtils;

import java.nio.file.Path;

public enum Season {
    SPRING(Path.of(FileUtils.SPRITES_RESOURCES + "forest"), "forest"),
    SUMMER(Path.of(FileUtils.SPRITES_RESOURCES + "desert"), "desert"),
    AUTUMN(Path.of(FileUtils.SPRITES_RESOURCES + "taiga"), "taiga"),
    WINTER(Path.of(FileUtils.SPRITES_RESOURCES + "tundra"), "tundra");

    private final Path path;
    private final String type;

    Season(Path path, String type) {
        this.path = path;
        this.type = type;
    }

    public Path getPath() {
        return path;
    }

    public String getType() {
        return type;
    }

    public static Season fromString(String season) {
        return switch (season.toUpperCase()) {
            case "SPRING" -> SPRING;
            case "SUMMER" -> SUMMER;
            case "AUTUMN" -> AUTUMN;
            case "WINTER" -> WINTER;
            default -> throw new IllegalArgumentException("Unknown season: " + season);
        };
    }
}
