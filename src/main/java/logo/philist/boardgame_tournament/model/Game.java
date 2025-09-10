package logo.philist.boardgame_tournament.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Game {

    private final StringProperty name;
    private final IntegerProperty minPlayers;
    private final IntegerProperty maxPlayers;

    public Game(@JsonProperty("name") String name,
                @JsonProperty("minPlayers") int minPlayers,
                @JsonProperty("maxPlayers") int maxPlayers) {
        this.name = new SimpleStringProperty(name);
        this.minPlayers = new SimpleIntegerProperty(minPlayers);
        this.maxPlayers = new SimpleIntegerProperty(maxPlayers);
    }

    @JsonIgnore
    public StringProperty getNameProperty() {
        return name;
    }

    @JsonIgnore
    public IntegerProperty getMinPlayersProperty() {
        return minPlayers;
    }

    @JsonIgnore
    public IntegerProperty getMaxPlayersProperty() {
        return maxPlayers;
    }

    @JsonProperty("name")
    public String getName() {
        return name.get();
    }

    @JsonProperty("minPlayers")
    public int getMinPlayers() {
        return minPlayers.get();
    }

    @JsonProperty("maxPlayers")
    public int getMaxPlayers() {
        return maxPlayers.get();
    }

    @JsonProperty("name")
    public void setName(String name) { this.name.set(name); }

    @JsonProperty("minPlayers")
    public void setMinPlayers(int minPlayers) { this.minPlayers.set(minPlayers); }

    @JsonProperty("maxPlayers")
    public void setMaxPlayers(int maxPlayers) { this.maxPlayers.set(maxPlayers); }

    @Override
    public String toString() {
        return String.format("%s (%d-%d)", getName(), getMinPlayers(), getMaxPlayers());
    }
}
