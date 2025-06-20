package game.component;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.PhysicsComponent;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.VerticalDirection;

public class EntityMovement extends Component {

    private double speed;

    public EntityMovement(double speed) {
        this.speed = speed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void startMovement(VerticalDirection direction) {
        tryMove(direction);
    }

    public void endMovement(VerticalDirection direction) {
        entity.getComponent(PhysicsComponent.class).setVelocityY(0);
    }

    public void startMovement(HorizontalDirection direction) {
        entity.getComponent(PhysicsComponent.class).setVelocityX(direction == HorizontalDirection.LEFT ? -speed : speed);
        //entity.translateX(direction == HorizontalDirection.LEFT ? -speed : speed);
        //onMove(direction == HorizontalDirection.LEFT ? -speed : speed, 0);
    }

    public void endMovement(HorizontalDirection direction) {
        entity.getComponent(PhysicsComponent.class).setVelocityX(0);
    }

    public void tryMove(VerticalDirection direction) {
        double y = direction == VerticalDirection.UP ? -speed : speed;

        var entities = new java.util.ArrayList<>(entity.getWorld().getEntitiesByComponent(CollidableComponent.class)
                .stream()
                .filter(entity1 -> entity.getY() < entity1.getY() && direction == VerticalDirection.DOWN ||
                        entity.getY() > entity1.getY() && direction == VerticalDirection.UP)
                .sorted((e1, e2) -> (int) (entity.distanceBBox(e1) - entity.distanceBBox(e2)))
                .toList());

        for (var otherEntity : entities) {
            if (otherEntity == entity) {
                continue;
            }

            // Si entity passe devant otherEntity, la collision est détectée
            var condition = otherEntity.getY() < entity.getY();

            otherEntity.getComponent(CollidableComponent.class).setValue(condition);
        }

        entity.getComponent(PhysicsComponent.class).setVelocityY(y);
        //entity.translateY(y * 100);
    }

    private void onMove(double x, double y) {
        var entities = entity.getWorld().getCollidingEntities(entity);
        for (var collidingEntity : entities) {
            // Si entity passe devant collidingEntity, la collision est détectée
            if (collidingEntity.getY() < entity.getY()) {
                entity.translate(-x, -y);
                var distance = entity.distanceBBox(collidingEntity);
                System.out.println( distance);
                if (x != 0) {
                    entity.translate(distance, 0);
                } else if (y != 0) {
                    entity.translate(0, distance);
                }
                return;
            }
        }
    }
}
