package game.component.player;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.input.virtual.VirtualButton;
import game.GameApp;
import game.Season;
import game.component.EntityMovement;
import javafx.geometry.HorizontalDirection;
import javafx.geometry.VerticalDirection;
import javafx.scene.input.KeyCode;

public class PlayerInputKey extends Component {


    @Override
    public void onAdded() {
        FXGL.getInput().addAction(new UserAction("Change season") {
            @Override
            protected void onAction() {
                GameApp.getInstance().setSeason(switch (GameApp.getInstance().getSeason()) {
                    case SPRING -> Season.SUMMER;
                    case SUMMER -> Season.AUTUMN;
                    case AUTUMN -> Season.WINTER;
                    case WINTER -> Season.SPRING;
                });
            }
        }, KeyCode.C);

        FXGL.getInput().addAction(new UserAction("Move Up") {
            @Override
            protected void onActionBegin() {
                entity.getComponent(EntityMovement.class).startMovement(VerticalDirection.UP);
            }

            @Override
            protected void onActionEnd() {
                entity.getComponent(EntityMovement.class).endMovement(VerticalDirection.UP);
            }
        }, KeyCode.Z, VirtualButton.RB);
        FXGL.getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onActionBegin() {
                entity.getComponent(EntityMovement.class).startMovement(HorizontalDirection.LEFT);
            }

            @Override
            protected void onActionEnd() {
                entity.getComponent(EntityMovement.class).endMovement(HorizontalDirection.LEFT);
            }
        }, KeyCode.Q);
        FXGL.getInput().addAction(new UserAction("Move Bottom") {
            @Override
            protected void onActionBegin() {
                entity.getComponent(EntityMovement.class).startMovement(VerticalDirection.DOWN);
            }

            @Override
            protected void onActionEnd() {
                entity.getComponent(EntityMovement.class).endMovement(VerticalDirection.DOWN);
            }
        }, KeyCode.S);
        FXGL.getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onActionBegin() {
                entity.getComponent(EntityMovement.class).startMovement(HorizontalDirection.RIGHT);
            }

            @Override
            protected void onActionEnd() {
                entity.getComponent(EntityMovement.class).endMovement(HorizontalDirection.RIGHT);
            }
        }, KeyCode.D);
    }
}
