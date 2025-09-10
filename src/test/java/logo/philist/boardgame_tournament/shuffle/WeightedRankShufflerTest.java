package logo.philist.boardgame_tournament.shuffle;

import logo.philist.boardgame_tournament.model.Game;
import logo.philist.boardgame_tournament.model.GameGroup;
import logo.philist.boardgame_tournament.model.Player;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class WeightedRankShufflerTest {
    @Test
    void testWeightedGrouping() {
        List<Player> players = Arrays.asList(
                new Player("A"),
                new Player("B"),
                new Player("C"),
                new Player("D")
        );
        players.get(0).setRoundScore(0, 100);
        players.get(1).setRoundScore(0, 90);
        players.get(2).setRoundScore(0, 10);
        players.get(3).setRoundScore(0, 5);
        List<Game> games = List.of(new Game("Test", 2, 2), new Game("Test2", 2, 2));
        WeightedRankShuffler shuffler = new WeightedRankShuffler();

        int togetherCount = 0;
        int runs = 1000;
        for (int i = 0; i < runs; i++) {
            List<GameGroup> groups = shuffler.shuffle(players, games);
            List<Player> group = groups.get(0).getPlayers();
            if (group.contains(players.get(0)) && group.contains(players.get(1))) {
                togetherCount++;
            }
        }
        double probability = togetherCount / (double) runs;
        // Should be significantly higher than random (which is 1/6 for 2 out of 4)
        assertTrue(probability > 0.25, "High-ranked players should be grouped together more often");
    }
}