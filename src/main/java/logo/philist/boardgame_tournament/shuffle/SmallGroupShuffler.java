package logo.philist.boardgame_tournament.shuffle;

import logo.philist.boardgame_tournament.model.Game;
import logo.philist.boardgame_tournament.model.GameGroup;
import logo.philist.boardgame_tournament.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class SmallGroupShuffler implements Shuffler {

    public static final double DOUBLE_CHANCE = 0.66;
    private final Random rand = new Random();

    @Override
    public String toString() {
        return "Small Group Shuffle";
    }

    @Override
    public List<GameGroup> shuffle(List<Player> players, List<Game> games) {
        players = new ArrayList<>(players);
        Collections.shuffle(players);
        List<GameGroup> groups = new ArrayList<>();

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

                // Randomly decide: 50% chance for size 2, 50% chance for size 3
                // But respect remaining players
                int[] sizes;
                if (remainingPlayers >= 3) {
                    // Randomly choose which to try first: 2 or 3
                    sizes = getDoubleChance() ? new int[]{2, 3} : new int[]{3, 2};
                } else if (remainingPlayers == 2) {
                    sizes = new int[]{2};
                } else {
                    // Only 1 player left - shouldn't happen in normal flow
                    break;
                }

                // Try the randomly ordered sizes
                for (int groupSize : sizes) {
                    if (groupSize > remainingPlayers) continue;

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

        // Fallback: if we still have unassigned players after max retries,
        // use a greedy approach to ensure everyone gets assigned
        if (!done) {
            groups = fallbackAssignment(players, games);
        }

        return groups;
    }

    /**
     * Fallback greedy assignment to ensure all players are assigned.
     * Still tries to favor smaller groups when possible.
     */
    private List<GameGroup> fallbackAssignment(List<Player> players, List<Game> games) {
        List<Player> tempPlayers = new ArrayList<>(players);
        List<Game> tempGames = new ArrayList<>(games);
        List<GameGroup> groups = new ArrayList<>();

        // Shuffle for randomness
        Collections.shuffle(tempPlayers);

        // Sort games: prefer those with smaller minPlayers first,
        // then by larger maxPlayers (more flexible)
        tempGames.sort(Comparator
                .comparingInt(Game::getMinPlayers)
                .thenComparing((g1, g2) -> Integer.compare(g2.getMaxPlayers(), g1.getMaxPlayers()))
        );

        while (!tempPlayers.isEmpty() && !tempGames.isEmpty()) {
            int remainingPlayers = tempPlayers.size();

            // Find the best game for current situation
            Game chosenGame = null;
            int chosenSize = 0;

            for (Game game : tempGames) {
                if (game.getMinPlayers() <= remainingPlayers) {
                    // Randomly prefer 2 or 3, but respect game constraints
                    int idealSize;
                    if (remainingPlayers >= 3 && game.getMaxPlayers() >= 3) {
                        // 50-50 chance between 2 and 3
                        idealSize = getDoubleChance() ? 2 : 3;
                        idealSize = Math.max(game.getMinPlayers(), idealSize);
                        idealSize = Math.min(game.getMaxPlayers(), idealSize);
                    } else if (remainingPlayers >= 2 && game.getMaxPlayers() >= 2) {
                        idealSize = 2;
                    } else {
                        // Use whatever fits when running low
                        idealSize = Math.min(game.getMaxPlayers(), remainingPlayers);
                        idealSize = Math.max(game.getMinPlayers(), idealSize);
                    }

                    chosenGame = game;
                    chosenSize = idealSize;
                    break;
                }
            }

            if (chosenGame == null) {
                // No suitable game found - shouldn't happen with proper test data
                // but handle gracefully by using any available game with max players
                if (!tempGames.isEmpty()) {
                    chosenGame = tempGames.get(0);
                    chosenSize = Math.min(chosenGame.getMaxPlayers(), remainingPlayers);
                } else {
                    break;
                }
            }

            tempGames.remove(chosenGame);
            List<Player> groupPlayers = new ArrayList<>();
            for (int i = 0; i < chosenSize && !tempPlayers.isEmpty(); i++) {
                groupPlayers.add(tempPlayers.remove(0));
            }

            if (!groupPlayers.isEmpty()) {
                groups.add(new GameGroup(groupPlayers, chosenGame));
            }
        }

        // If there are still remaining players, distribute them into existing groups
        if (!tempPlayers.isEmpty() && !groups.isEmpty()) {
            for (Player p : tempPlayers) {
                // Find a group that can accommodate one more player
                for (GameGroup group : groups) {
                    if (group.getPlayers().size() < group.getGame().getMaxPlayers()) {
                        List<Player> updatedPlayers = new ArrayList<>(group.getPlayers());
                        updatedPlayers.add(p);
                        int idx = groups.indexOf(group);
                        groups.set(idx, new GameGroup(updatedPlayers, group.getGame()));
                        break;
                    }
                }
            }
        }

        return groups;
    }

    private boolean getDoubleChance() {
        return rand.nextDouble() < DOUBLE_CHANCE;
    }
}