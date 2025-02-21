package game.pkg_Entity;

import game.pkg_Object.PlaceableGameObject;
import game.pkg_Object.Position;

import javax.swing.*;
import java.awt.*;

public class Entity extends PlaceableGameObject {

    protected FacingDirection facing;

    protected Position lastPosition = null;

    protected boolean isSpawned = false;

    public Entity(JComponent paintedOn, Sprite sprite, Position position, FacingDirection facing) {
        super(paintedOn, sprite, position);
        this.facing = facing;
    }

    public int getLayer() {
        return position.getLayer();
    }

    public void spawn() {
        isSpawned = true;
        position.getRoom().getEntities().add(this);

        getPaintedOn().repaint(position.getX(), position.getY(), sprite.getWidth(), sprite.getHeight());
    }

    public void despawn() {
        position.getRoom().getEntities().remove(this);
        isSpawned = false;

        getPaintedOn().repaint(position.getX(), position.getY(), sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public boolean canPaint(Graphics2D g2d) {
        return isSpawned;
    }

    public int getMovementFactor(FacingDirection facing) {
        /**
         * 7 est choisi arbitrairement
         * Permet un mouvement pas trop rapide ni trop lent
         */
        return switch (facing) {
            case NORTH, SOUTH -> sprite.getHeight();
            case EAST, WEST -> sprite.getWidth();
        } / 7;
    }

    /**
     * Pour un repaint totale, moyenne d'execution: 42123 nanosecondes
     * Pour un repaint partiel, moyenne d'execution: 33888 nanosecondes
     */
    public void move(FacingDirection facing) {
        this.lastPosition = position;

        int movementFactor = getMovementFactor(facing);
        switch (facing) {
            case NORTH -> {
                this.position = new Position(position.getX(), position.getY() - movementFactor, position.getLayer(), position.getRoom());
                getPaintedOn().repaint(position.getX(), position.getY(), sprite.getWidth(), sprite.getHeight() + movementFactor + 1);
            }
            case SOUTH -> {
                this.position = new Position(position.getX(), position.getY() + movementFactor, position.getLayer(), position.getRoom());
                getPaintedOn().repaint(position.getX(), lastPosition.getY(), sprite.getWidth(), sprite.getHeight() + movementFactor + 1);
            }
            case EAST -> {
                this.position = new Position(position.getX() + movementFactor, position.getY(), position.getLayer(), position.getRoom());
                getPaintedOn().repaint(lastPosition.getX(), position.getY(), sprite.getWidth() + movementFactor + 1, sprite.getHeight());
            }
            case WEST -> {
                this.position = new Position(position.getX() - movementFactor, position.getY(), position.getLayer(), position.getRoom());
                getPaintedOn().repaint(position.getX(), position.getY(), sprite.getWidth() + movementFactor + 1, sprite.getHeight());
            }
        }
    }
}
