package logo.philist.boardgame_tournament.shuffle;

import logo.philist.boardgame_tournament.model.Game;
import logo.philist.boardgame_tournament.model.GameGroup;
import logo.philist.boardgame_tournament.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SmallGroupShuffler implements Shuffler {

    @Override
    public String toString() {
        return "Small Group Shuffle";
    }

    @Override
    public List<GameGroup> shuffle(List<Player> players, List<Game> games) {
        System.out.println(players);
        System.out.println(games);
        players = new ArrayList<>(players);
        Collections.shuffle(players);
        List<GameGroup> groups = new ArrayList<>();
        Random rand = new Random();

        List<Game> shuffledGames = new ArrayList<>(games);
        Collections.shuffle(shuffledGames, rand);

        int maxRetries = 100;
        int retries = 0;
        boolean done = false;

        while (!done && retries < maxRetries) {
            retries++;
            groups.clear();
            List<Player> tempPlayers = new ArrayList<>(players);
            List<Game> tempGames = new ArrayList<>(shuffledGames);

            // Keep forming groups until all players are assigned or we run out of games
            while (!tempPlayers.isEmpty() && !tempGames.isEmpty()) {
                int remainingPlayers = tempPlayers.size();
                boolean groupFormed = false;

                // Try group sizes from 2 up to min(6, remainingPlayers)
                // This favors smaller groups (pairs first)
                for (int groupSize = 2; groupSize <= Math.min(6, remainingPlayers); groupSize++) {
                    // Find a game that supports this group size
                    Game suitableGame = null;
                    int gameIdx = -1;

                    for (int i = 0; i < tempGames.size(); i++) {
                        Game g = tempGames.get(i);
                        if (g.getMinPlayers() <= groupSize && g.getMaxPlayers() >= groupSize) {
                            suitableGame = g;
                            gameIdx = i;
                            break;
                        }
                    }

                    if (suitableGame != null) {
                        // Form the group
                        tempGames.remove(gameIdx);
                        List<Player> groupPlayers = new ArrayList<>();
                        for (int i = 0; i < groupSize; i++) {
                            groupPlayers.add(tempPlayers.remove(0));
                        }
                        groups.add(new GameGroup(groupPlayers, suitableGame));
                        groupFormed = true;
                        break; // Found a group, move to next iteration
                    }
                }

                // If no group could be formed, we're stuck
                if (!groupFormed) {
                    break;
                }
            }

            if (tempPlayers.isEmpty()) {
                done = true;
            } else {
                // Reshuffle and try again
                Collections.shuffle(shuffledGames, rand);
            }
        }

        return groups;
    }
}
