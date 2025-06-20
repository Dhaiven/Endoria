package game.tile;

import java.awt.Shape;

public record Collision(Shape shape, CollisionType type) {
}
