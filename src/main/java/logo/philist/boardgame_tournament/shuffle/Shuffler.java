package logo.philist.boardgame_tournament.shuffle;

import logo.philist.boardgame_tournament.model.Game;
import logo.philist.boardgame_tournament.model.GameGroup;
import logo.philist.boardgame_tournament.model.Player;

import java.util.List;

public interface Shuffler {
    List<GameGroup> shuffle(List<Player> players, List<Game> games);
}
