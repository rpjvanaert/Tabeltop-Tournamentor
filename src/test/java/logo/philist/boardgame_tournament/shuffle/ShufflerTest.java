package logo.philist.boardgame_tournament.shuffle;

import logo.philist.boardgame_tournament.model.Game;
import logo.philist.boardgame_tournament.model.GameGroup;
import logo.philist.boardgame_tournament.model.Player;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ShufflerTest {

    private static final int NUM_ITERATIONS = 1000;
    private static final long MAX_DURATION_MS = 10;

    /**
     * Provides all shuffler implementations to test
     */
    static Stream<Arguments> shufflerProvider() {
        return Stream.of(
                Arguments.of(new PureShuffler()),
                Arguments.of(new GroupShuffler()),
                Arguments.of(new SmallGroupShuffler()),
                Arguments.of(new BasicRankShuffler()),
                Arguments.of(new WeightedRankShuffler())
        );
    }

    /**
     * Test that each shuffler:
     * - Uses each game at most once
     * - Assigns each player at most once (no duplicates)
     * - Completes within the time limit
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("shufflerProvider")
    void testShufflerConstraints(Shuffler shuffler) {
        // Test with various player and game configurations
        int[][] testCases = {
                {10, 5},   // 10 players, 5 games
                {20, 10},  // 20 players, 10 games
                {15, 8},   // 15 players, 8 games
                {30, 15},  // 30 players, 15 games
        };

        for (int[] testCase : testCases) {
            int numPlayers = testCase[0];
            int numGames = testCase[1];

            for (int iteration = 0; iteration < NUM_ITERATIONS; iteration++) {
                List<Player> players = createPlayers(numPlayers);
                List<Game> games = createGames(numGames);

                // Measure execution time
                long startTime = System.nanoTime();
                List<GameGroup> result = shuffler.shuffle(players, games);
                long endTime = System.nanoTime();
                long durationMs = (endTime - startTime) / 1_000_000;

                // Assertion 1: Execution time should be within limit
                assertTrue(durationMs <= MAX_DURATION_MS,
                        String.format("%s exceeded time limit for %d players, %d games: %dms (iteration %d)",
                                shuffler, numPlayers, numGames, durationMs, iteration));

                // Assertion 2: Each game is used at most once
                Set<Game> usedGames = new HashSet<>();
                for (GameGroup group : result) {
                    Game game = group.getGame();
                    assertFalse(usedGames.contains(game),
                            String.format("%s used game '%s' multiple times (iteration %d)",
                                    shuffler, game.getName(), iteration));
                    usedGames.add(game);
                }

                // Assertion 3: Each player is assigned at most once (no duplicates)
                Set<Player> assignedPlayers = new HashSet<>();
                for (GameGroup group : result) {
                    for (Player player : group.getPlayers()) {
                        assertFalse(assignedPlayers.contains(player),
                                String.format("%s assigned player '%s' multiple times (iteration %d)",
                                        shuffler, player.getName(), iteration));
                        assignedPlayers.add(player);
                    }
                }

                // Note: We don't require all players to be assigned, as some shufflers
                // may not be able to assign everyone due to game constraints
                // We just ensure no player is assigned more than once

                // Verify that each group respects game min/max player constraints
                for (GameGroup group : result) {
                    int playerCount = group.getPlayers().size();
                    Game game = group.getGame();
                    assertTrue(playerCount >= game.getMinPlayers(),
                            String.format("%s: Game '%s' has %d players, but min is %d (iteration %d)",
                                    shuffler, game.getName(), playerCount, game.getMinPlayers(), iteration));
                    assertTrue(playerCount <= game.getMaxPlayers(),
                            String.format("%s: Game '%s' has %d players, but max is %d (iteration %d)",
                                    shuffler, game.getName(), playerCount, game.getMaxPlayers(), iteration));
                }
            }
        }
    }

    /**
     * Test edge cases: small number of players and games
     */
    @ParameterizedTest(name = "{0}")
    @MethodSource("shufflerProvider")
    void testShufflerEdgeCases(Shuffler shuffler) {
        // Test with minimal setup
        for (int iteration = 0; iteration < 100; iteration++) {
            List<Player> players = createPlayers(4);
            List<Game> games = createGames(2);

            long startTime = System.nanoTime();
            List<GameGroup> result = shuffler.shuffle(players, games);
            long endTime = System.nanoTime();
            long durationMs = (endTime - startTime) / 1_000_000;

            assertTrue(durationMs <= MAX_DURATION_MS,
                    String.format("%s exceeded time limit for edge case: %dms (iteration %d)",
                            shuffler, durationMs, iteration));

            // Verify basic constraints
            Set<Player> assignedPlayers = new HashSet<>();
            for (GameGroup group : result) {
                for (Player player : group.getPlayers()) {
                    assertFalse(assignedPlayers.contains(player),
                            String.format("%s assigned player multiple times in edge case (iteration %d)",
                                    shuffler, iteration));
                    assignedPlayers.add(player);
                }
            }
        }
    }

    /**
     * Creates test players with varying scores
     */
    private List<Player> createPlayers(int count) {
        List<Player> players = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            Player player = new Player("Player" + (i + 1));
            // Give players varying scores for rank-based shufflers
            int score = rand.nextInt(100);
            player.setRoundScore(0, score);
            players.add(player);
        }
        return players;
    }

    /**
     * Creates test games with varying min/max player counts
     */
    private List<Game> createGames(int count) {
        List<Game> games = new ArrayList<>();

        // Create games with various player count ranges
        int[] minPlayers = {2, 2, 3, 4, 2, 3, 2, 4, 3, 2};
        int[] maxPlayers = {4, 6, 5, 6, 8, 7, 5, 8, 6, 4};

        for (int i = 0; i < count; i++) {
            int minIdx = i % minPlayers.length;
            int min = minPlayers[minIdx];
            int max = maxPlayers[minIdx];
            games.add(new Game("Game" + (i + 1), min, max));
        }

        return games;
    }
}
