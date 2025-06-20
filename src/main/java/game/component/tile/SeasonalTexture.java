package game.component.tile;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.Texture;
import game.GameApp;
import game.Season;
import game.event.EventType;
import javafx.scene.Node;

import java.util.Map;
import java.util.function.Supplier;

public class SeasonalTexture extends Component {

    private final Map<Season, Supplier<Texture>> seasonalTextures;

    public SeasonalTexture(Map<Season, Supplier<Texture>> seasonalTextures) {
        this.seasonalTextures = seasonalTextures;
    }

    @Override
    public void onAdded() {
        changeTexture(GameApp.getInstance().getSeason());

        FXGL.getEventBus().addEventHandler(EventType.SEASON_CHANGE, event -> {
            Season newSeason = event.getNewSeason();
            changeTexture(newSeason);
        });
    }

    private void changeTexture(Season season) {
        var texture = seasonalTextures.get(season);
        if (texture != null) {
            for (Node n : entity.getViewComponent().getChildren()) {
                if (n instanceof AnimatedTexture anim) {
                    anim.stop();
                }
            }

            entity.getViewComponent().clearChildren();
            entity.getViewComponent().addChild(texture.get());
        }
    }
}
