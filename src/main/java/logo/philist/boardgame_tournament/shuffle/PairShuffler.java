package logo.philist.boardgame_tournament.shuffle;

import logo.philist.boardgame_tournament.model.Game;
import logo.philist.boardgame_tournament.model.GameGroup;
import logo.philist.boardgame_tournament.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PairShuffler implements Shuffler {

    @Override
    public String toString() {
        return "Pair Shuffle";
    }

    @Override
    public List<GameGroup> shuffle(List<Player> players, List<Game> games) {
        players = new ArrayList<>(players);
        Collections.shuffle(players);
        List<GameGroup> groups = new ArrayList<>();
        Random rand = new Random();

        List<Game> shuffledGames = new ArrayList<>(games);
        Collections.shuffle(shuffledGames, rand);

        boolean done = false;
        while (!done) {
            groups.clear();
            List<Player> tempPlayers = new ArrayList<>(players);
            List<Game> tempGames = new ArrayList<>(shuffledGames);

            int numPlayers = tempPlayers.size();
            int pairs = numPlayers / 2;
            boolean hasGroupOfThree = numPlayers % 2 == 1;

            // Form pairs
            for (int i = 0; i < pairs - (hasGroupOfThree ? 1 : 0); i++) {
                if (tempGames.isEmpty()) break;
                Game game = tempGames.remove(0);
                if (game.getMinPlayers() <= 2 && game.getMaxPlayers() >= 2) {
                    List<Player> groupPlayers = new ArrayList<>();
                    groupPlayers.add(tempPlayers.remove(0));
                    groupPlayers.add(tempPlayers.remove(0));
                    groups.add(new GameGroup(groupPlayers, game));
                }
            }

            // If needed, form one group of 3
            if (hasGroupOfThree && !tempGames.isEmpty()) {
                Game groupOfThreeGame = null;
                int gameIdx = -1;
                for (int i = 0; i < tempGames.size(); i++) {
                    Game g = tempGames.get(i);
                    if (g.getMinPlayers() <= 3 && g.getMaxPlayers() >= 3) {
                        groupOfThreeGame = g;
                        gameIdx = i;
                        break;
                    }
                }
                if (groupOfThreeGame != null) {
                    tempGames.remove(gameIdx);
                    List<Player> groupPlayers = new ArrayList<>();
                    groupPlayers.add(tempPlayers.remove(0));
                    groupPlayers.add(tempPlayers.remove(0));
                    groupPlayers.add(tempPlayers.remove(0));
                    groups.add(new GameGroup(groupPlayers, groupOfThreeGame));
                } else {
                    // fallback: break and reshuffle
                    break;
                }
            }

            if (tempPlayers.isEmpty()) {
                done = true;
            }
        }

        return groups;
    }
}
