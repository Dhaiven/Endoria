package game;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.scene.SubScene;
import game.GameState;
import game.player.action.Action;
import game.player.action.KeysMapper;
import game.player.ui.UserInterface;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;

public class SettingsOverlayFXGL extends SubScene {

    private final VBox content = new VBox(10);
    private final UserInterface userInterface;
    private boolean waitingForKeyInput = false;

    public SettingsOverlayFXGL(UserInterface ui) {
        this.userInterface = ui;

        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_LEFT);
        content.setPrefWidth(600);
        content.setStyle("-fx-background-color: rgba(0,0,0,0.8);");

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(700);
        scrollPane.setStyle("-fx-background-color: transparent;");

        VBox root = new VBox(scrollPane);
        root.setAlignment(Pos.CENTER);
        root.setPrefSize(600, 700);
        root.setStyle("-fx-background-color: transparent;");

        getContentRoot().getChildren().add(root);

        addSection("En jeu", GameState.PLAY);
        addSection("Menu", GameState.PAUSE);
        addSection("Autre", null);
    }

    private void addSection(String title, GameState state) {
        Label sectionLabel = new Label(title);
        sectionLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold;");
        content.getChildren().add(sectionLabel);

        for (Action action : Action.values()) {
            if (state != action.getState()) continue;

            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);

            Label actionLabel = new Label(action.getName());
            actionLabel.setStyle("-fx-text-fill: white;");
            row.getChildren().add(actionLabel);

            Integer[] keys = userInterface.getPlayer().getSettings().getKeyFromAction(action);
            for (int i = 0; i < keys.length; i++) {
                Button keyButton = new Button(KeysMapper.getKeyText(keys[i]));
                final int index = i;

                keyButton.setOnAction(e -> {
                    if (waitingForKeyInput) return;
                    waitingForKeyInput = true;

                    FXGL.getGameScene().getRoot().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                        KeyCode keyCode = event.getCode();
                        userInterface.getPlayer().getSettings().replaceKeyFromAction(action, index, keyCode.getCode());
                        keyButton.setText(KeysMapper.getKeyText(keyCode.getCode()));
                        waitingForKeyInput = false;

                        //FXGL.getGameScene().getRoot().removeEventHandler(KeyEvent.KEY_PRESSED, this);
                        event.consume();
                    });
                });

                row.getChildren().add(keyButton);
            }

            content.getChildren().add(row);
        }
    }
}
