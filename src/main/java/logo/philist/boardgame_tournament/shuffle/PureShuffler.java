package logo.philist.boardgame_tournament.shuffle;

import logo.philist.boardgame_tournament.model.Game;
import logo.philist.boardgame_tournament.model.GameGroup;
import logo.philist.boardgame_tournament.model.Player;

import java.util.*;

public class PureShuffler implements Shuffler {

    @Override
    public String toString() {
        return "Pure Shuffle";
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
            int gameIndex;
            while (!tempPlayers.isEmpty()) {
                try {
                    gameIndex = rand.nextInt(tempGames.size());
                } catch (IllegalArgumentException e) {
                    break;
                }

                Game game = tempGames.remove(gameIndex);
                int playerCount = rand.nextInt(game.getMinPlayers(), game.getMaxPlayers() + 1);
                playerCount = Math.min(playerCount, tempPlayers.size());

                if (playerCount < game.getMinPlayers())
                    break;

                List<Player> groupPlayers = new ArrayList<>();
                for (int i = 0; i < playerCount; i++) {
                    groupPlayers.add(tempPlayers.remove(0));
                }
                groups.add(new GameGroup(groupPlayers, game));
            }

            if (tempPlayers.isEmpty()) {
                done = true;
            }
        }

        return groups;
    }
}
