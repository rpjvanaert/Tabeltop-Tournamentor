package logo.philist.boardgame_tournament.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logo.philist.boardgame_tournament.controller.distribution.PointDistributionController;
import logo.philist.boardgame_tournament.controller.round.RoundController;
import logo.philist.boardgame_tournament.model.Player;
import logo.philist.boardgame_tournament.storage.Storage;

import java.io.IOException;

public class BaseController {

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

        addRound();
        table.setEditable(true);

        table.setItems(players);

        loadPlayers();
    }

    public void loadPlayers() {
        players.setAll(Storage.loadPlayers().stream().map(Player::new).toList());
    }

    private void addRound() {
        this.players.forEach(Player::addRound);

        int roundIndex = table.getColumns().size() - 2;
        TableColumn<Player, Number> roundCol = new TableColumn<>("Round " + (roundIndex + 1));
        roundCol.setCellValueFactory(data -> data.getValue().getRoundScores().get(roundIndex));
        roundCol.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.NumberStringConverter()));
        roundCol.setOnEditCommit(event -> {
            Player player = event.getRowValue();
            player.setRoundScore(roundIndex, event.getNewValue().intValue());
        });
        roundCol.setEditable(true);
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
            table.getColumns().remove(table.getColumns().size() - 1);

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
}
