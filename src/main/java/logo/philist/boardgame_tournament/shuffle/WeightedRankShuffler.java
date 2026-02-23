package logo.philist.boardgame_tournament.shuffle;

import logo.philist.boardgame_tournament.model.Game;
import logo.philist.boardgame_tournament.model.GameGroup;
import logo.philist.boardgame_tournament.model.Player;

import java.util.*;

public class WeightedRankShuffler implements Shuffler {

    @Override
    public String toString() {
        return "Weighted Rank Shuffle";
    }

    private Player weightedRandomSelect(List<Player> players, Random rand) {
        int totalWeight = players.stream().mapToInt(Player::totalScore).map(WeightedRankShuffler::calcWeight).sum();
        if (totalWeight <= 0) {
            return players.get(rand.nextInt(players.size()));
        }
        int r = rand.nextInt(totalWeight);
        int cumulative = 0;
        for (Player p : players) {
            cumulative += calcWeight(p.totalScore());
            if (r < cumulative) {
                return p;
            }
        }
        return players.get(players.size() - 1);
    }

    private static int calcWeight(int i) {
        return (int) Math.pow(i, 2.5);
    }

    @Override
    public List<GameGroup> shuffle(List<Player> players, List<Game> games) {
        players = new ArrayList<>(players);
        List<GameGroup> groups = new ArrayList<>();
        Random rand = new Random();

        List<Game> shuffledGames = new ArrayList<>(games);
        Collections.shuffle(shuffledGames, rand);

        boolean done = false;
        while (!done) {
            groups.clear();
            List<Player> tempPlayers = new ArrayList<>(players);
            List<Game> tempGames = new ArrayList<>(shuffledGames);

            while (!tempPlayers.isEmpty() && !tempGames.isEmpty()) {
                Game game = tempGames.remove(rand.nextInt(tempGames.size()));
                int playerCount = rand.nextInt(game.getMinPlayers(), game.getMaxPlayers() + 1);
                playerCount = Math.min(playerCount, tempPlayers.size());

                if (playerCount < game.getMinPlayers())
                    break;

                List<Player> groupPlayers = new ArrayList<>();
                for (int i = 0; i < playerCount; i++) {
                    Player selected = weightedRandomSelect(tempPlayers, rand);
                    groupPlayers.add(selected);
                    tempPlayers.remove(selected);
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
