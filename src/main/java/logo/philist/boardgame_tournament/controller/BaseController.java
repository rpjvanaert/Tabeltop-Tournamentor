package logo.philist.boardgame_tournament.controller;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logo.philist.boardgame_tournament.controller.distribution.PointDistributionController;
import logo.philist.boardgame_tournament.controller.round.RoundController;
import logo.philist.boardgame_tournament.model.Player;
import logo.philist.boardgame_tournament.storage.Storage;

import java.io.IOException;

public class BaseController {

    @FXML
    private VBox rootVbox;
    @FXML
    private HBox topHeader;
    @FXML
    private HBox bottomHeader;

    @FXML
    private TableView<Player> table;

    @FXML
    private TableColumn<Player, String> playerNameColumn;

    @FXML
    private TableColumn<Player, Integer> playerScoreColumn;

    private ObservableList<Player> players = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        playerNameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        playerScoreColumn.setCellValueFactory(data -> data.getValue().totalScoreProperty().asObject());

        setupTable();

        loadPlayers();

        int rounds = players.getLast().getRoundScoresArray().length;
        for (int i = 0; i < rounds; i++) {
            addRoundColumn();
        }
    }

    private void setupTable() {
        table.setEditable(true);
        table.setMinHeight(0);

        table.setFixedCellSize(24);
        table.fixedCellSizeProperty().bind(Bindings.createDoubleBinding(() -> {
            int rows = Math.max(1, players.size());
            double headerReserve = 50.0;
            double available = Math.max(0, table.getHeight() - headerReserve);
            double size = available / rows;
            double minRowHeight = 24.0;
            return Math.max(minRowHeight, size);
        }, table.heightProperty(), Bindings.size(players)));

        final double fontFactor = 0.45;
        final double minFont = 10.0;
        final double maxFont = 24.0;

        Runnable applyFontSize = () -> {
            double cellSize = table.getFixedCellSize();
            if (cellSize <= 0) return;
            double fontSize = Math.clamp(cellSize * fontFactor, minFont, maxFont);
            table.setStyle("-fx-font-size: " + String.format("%.1f", fontSize) + "px;");
        };

        applyFontSize.run();

        table.fixedCellSizeProperty().addListener((obs, oldV, newV) -> applyFontSize.run());

        table.setItems(players);
    }

    public void loadPlayers() {
        players.setAll(Storage.loadPlayerObjects());
    }

    private void addRound() {
        this.players.forEach(Player::addRound);

        addRoundColumn();
    }

    private void addRoundColumn() {
        int roundIndex = table.getColumns().size() - 2;
        TableColumn<Player, Number> roundCol = new TableColumn<>("R#" + (roundIndex + 1));
        roundCol.setCellValueFactory(data -> data.getValue().getRoundScores().get(roundIndex));
        roundCol.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.NumberStringConverter()));
        roundCol.setOnEditCommit(event -> {
            Player player = event.getRowValue();
            player.setRoundScore(roundIndex, event.getNewValue().intValue());
        });
        roundCol.setEditable(true);

        roundCol.minWidthProperty().bind(table.fixedCellSizeProperty());

        table.getColumns().add(roundCol);
    }

    @FXML
    private void onGenerateRound() {
        String fxml = "/logo/philist/boardgame_tournament/round-view.fxml";
        String title = "Round shuffle";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            RoundController controller = loader.getController();
            controller.setInfo(players, this.table.getColumns().size() - 2);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initOwner(table.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditPlayers() {
        String fxml = "/logo/philist/boardgame_tournament/players-edit-view.fxml";
        String title = "Edit Players";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            PlayersEditController controller = loader.getController();
            controller.setPlayers(players);
            controller.setBaseController(this);

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(table.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setAlwaysOnTop(true);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openWindow(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(table.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setAlwaysOnTop(true);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onEditGames() {
        String fxml = "/logo/philist/boardgame_tournament/games-edit-view.fxml";
        String title = "Edit Games";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(table.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.setAlwaysOnTop(true);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onAddRound() {
        addRound();
    }

    @FXML
    private void onRemoveLastRound() {
        int roundCount = table.getColumns().size() - 2;
        if (roundCount > 1) {
            table.getColumns().removeLast();

            for (Player player : players) {
                player.removeLastRound();
            }
            table.refresh();
        }
    }


    @FXML
    private void onShowStandings() {
        String fxml = "/logo/philist/boardgame_tournament/standings-view.fxml";
        String title = "Standings";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            StandingsController controller = loader.getController();
            controller.setPlayers(players);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initOwner(table.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onShowPointDistribution() {
        String fxml = "/logo/philist/boardgame_tournament/point-distribution-view.fxml";
        String title = "Point Distribution";

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            PointDistributionController controller = loader.getController();
            controller.setPlayerAmount(this.players.size());
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initOwner(table.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onFillRoundScores() {
        int i = 0;
        for (Player player : players) {
            player.setRoundScore(player.getRoundScores().size() - 1, i++);
            if (i > 10) i = 0;
        }
    }

    public void onSaveTournament() {
        Storage.savePlayersObjects(players);
    }

    public void onResetScores() {
        int roundCount = table.getColumns().size() - 2;
        while (roundCount > 1) {
            table.getColumns().removeLast();
            roundCount--;

            for (Player player : players) {
                player.removeLastRound();
            }
            table.refresh();
        }

        for (Player player : players) {
            player.resetScores();
        }
        table.refresh();
    }
}
