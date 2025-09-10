package logo.philist.boardgame_tournament.model;

import java.util.List;

public class GameGroup {
    private List<Player> players;
    private Game game;

    public GameGroup(List<Player> players, Game game) {
        this.players = players;
        this.game = game;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Game getGame() {
        return game;
    }
}
