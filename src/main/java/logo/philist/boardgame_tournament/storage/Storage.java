package logo.philist.boardgame_tournament.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import logo.philist.boardgame_tournament.model.Game;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Storage {

    private static final String PLAYERS_PATH = "players.json";
    private static final String GAMES_PATH = "games.json";

    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<String> loadPlayers() {
        try {
            File file = new File(PLAYERS_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return Arrays.asList(mapper.readValue(file, String[].class));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void savePlayers(List<String> players) {
        players.removeIf(String::isEmpty);

        try {
            File file = new File(PLAYERS_PATH);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, players);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Game> loadGames() {
        try {
            File file = new File(GAMES_PATH);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            return Arrays.asList(mapper.readValue(file, Game[].class));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void saveGames(List<Game> games) {
        try {
            File file = new File(GAMES_PATH);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, games);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
