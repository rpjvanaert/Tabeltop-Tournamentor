package logo.philist.boardgame_tournament.shuffle;

import logo.philist.boardgame_tournament.model.Game;
import logo.philist.boardgame_tournament.model.GameGroup;
import logo.philist.boardgame_tournament.model.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class GroupShuffler implements Shuffler {

    private static final int MAX_ATTEMPTS = 1000;

    @Override
    public String toString() {
        return "Group Shuffle";
    }

    @Override
    public List<GameGroup> shuffle(List<Player> players, List<Game> games) {
        players = new ArrayList<>(players);
        Random rand = new Random();
        List<GameGroup> groups = new ArrayList<>();

        int attempts = 0;
        while (attempts++ < MAX_ATTEMPTS) {
            groups.clear();
            List<Player> tempPlayers = new ArrayList<>(players);
            List<Game> tempGames = new ArrayList<>(games);
            Collections.shuffle(tempPlayers, rand);
            Collections.shuffle(tempGames, rand);

            while (!tempPlayers.isEmpty() && !tempGames.isEmpty()) {
                List<Game> possibleGames = new ArrayList<>();
                List<Integer> weights = new ArrayList<>();
                int totalWeight = 0;
                for (Game game : tempGames) {
                    int maxGroupSize = Math.min(game.getMaxPlayers(), tempPlayers.size());
                    if (maxGroupSize >= game.getMinPlayers()) {
                        possibleGames.add(game);
                        int weight = (int) Math.pow(maxGroupSize, 3);
                        weights.add(weight);
                        totalWeight += weight;
                    }
                }
                if (possibleGames.isEmpty()) break;

                int r = rand.nextInt(totalWeight);
                int idx = 0;
                for (; idx < weights.size(); idx++) {
                    r -= weights.get(idx);
                    if (r < 0) break;
                }
                Game selectedGame = possibleGames.get(idx);
                tempGames.remove(selectedGame);

                int groupSize = Math.min(selectedGame.getMaxPlayers(), tempPlayers.size());
                groupSize = Math.max(selectedGame.getMinPlayers(), groupSize);

                List<Player> groupPlayers = new ArrayList<>();
                for (int i = 0; i < groupSize; i++) {
                    groupPlayers.add(tempPlayers.remove(0));
                }
                groups.add(new GameGroup(groupPlayers, selectedGame));
            }

            if (tempPlayers.isEmpty()) {
                return groups;
            }
            // otherwise try again with a new random shuffle
        }

        // fallback deterministic best-effort grouping to avoid infinite loop / heavy CPU
        return bestEffortGrouping(players, games);
    }

    private List<GameGroup> bestEffortGrouping(List<Player> players, List<Game> games) {
        List<Player> tempPlayers = new ArrayList<>(players);
        List<Game> tempGames = new ArrayList<>(games);
        // prefer games with smaller minPlayers first, and among those prefer larger maxPlayers
        tempGames.sort(Comparator
                .comparingInt(Game::getMinPlayers)
                .thenComparing((g1, g2) -> Integer.compare(g2.getMaxPlayers(), g1.getMaxPlayers()))
        );

        List<GameGroup> groups = new ArrayList<>();

        // greedy create groups while possible
        while (true) {
            Game chosen = null;
            for (Game g : tempGames) {
                if (g.getMinPlayers() <= tempPlayers.size()) {
                    chosen = g;
                    break;
                }
            }
            if (chosen == null) break;

            int groupSize = Math.min(chosen.getMaxPlayers(), tempPlayers.size());
            groupSize = Math.max(chosen.getMinPlayers(), groupSize);

            List<Player> groupPlayers = new ArrayList<>();
            for (int i = 0; i < groupSize; i++) {
                groupPlayers.add(tempPlayers.remove(0));
            }
            groups.add(new GameGroup(groupPlayers, chosen));
        }

        // try to distribute any remaining players into existing groups up to each game's maxPlayers
        if (!tempPlayers.isEmpty() && !groups.isEmpty()) {
            List<Player> leftovers = new ArrayList<>(tempPlayers);
            tempPlayers.clear();

            List<GameGroup> rebuilt = new ArrayList<>();
            int li = 0;
            for (GameGroup gg : groups) {
                List<Player> copy = new ArrayList<>(gg.getPlayers());
                int space = gg.getGame().getMaxPlayers() - copy.size();
                while (space > 0 && li < leftovers.size()) {
                    copy.add(leftovers.get(li++));
                    space--;
                }
                rebuilt.add(new GameGroup(copy, gg.getGame()));
            }

            // if any leftovers still remain, append them to the last group (may exceed min but won't create infinite attempts)
            if (li < leftovers.size()) {
                List<Player> lastPlayers = new ArrayList<>(rebuilt.get(rebuilt.size() - 1).getPlayers());
                while (li < leftovers.size()) {
                    lastPlayers.add(leftovers.get(li++));
                }
                rebuilt.set(rebuilt.size() - 1, new GameGroup(lastPlayers, rebuilt.get(rebuilt.size() - 1).getGame()));
            }

            return rebuilt;
        }

        // if nothing could be created, return empty list
        return groups;
    }
}