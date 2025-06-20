package game.event;

import game.Season;
import javafx.event.Event;

public class SeasonChangeEvent extends Event {

    private final Season newSeason;

    public SeasonChangeEvent(Season newSeason) {
        super(EventType.SEASON_CHANGE);
        this.newSeason = newSeason;
    }

    public Season getNewSeason() {
        return newSeason;
    }
}
