package logo.philist.boardgame_tournament.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import logo.philist.boardgame_tournament.model.Game;
import logo.philist.boardgame_tournament.storage.Storage;

public class GamesEditController {

    @FXML
    private TableView<Game> gameTable;

    @FXML
    private TableColumn<Game, String> nameColumn;

    @FXML
    private TableColumn<Game, Number> minPlayersColumn;
    @FXML
    private TableColumn<Game, Number> maxPlayersColumn;

    private final ObservableList<Game> games = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(cd -> cd.getValue().getNameProperty());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event ->
                event.getRowValue().setName(event.getNewValue())
        );

        minPlayersColumn.setCellValueFactory(cd -> cd.getValue().getMinPlayersProperty());
        minPlayersColumn.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.NumberStringConverter()));
        minPlayersColumn.setOnEditCommit(event ->
                event.getRowValue().setMinPlayers(event.getNewValue().intValue())
        );

        maxPlayersColumn.setCellValueFactory(cd -> cd.getValue().getMaxPlayersProperty());
        maxPlayersColumn.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.NumberStringConverter()));
        maxPlayersColumn.setOnEditCommit(event ->
                event.getRowValue().setMaxPlayers(event.getNewValue().intValue())
        );
        this.games.setAll(FXCollections.observableArrayList(Storage.loadGames()));
        gameTable.setItems(this.games);
        gameTable.setEditable(true);
    }


    @FXML
    public void onSave() {
        Storage.saveGames(gameTable.getItems());
    }

    @FXML
    public void onAddGame() {
        games.add(new Game("New Game", 2, 4)); // Default values
    }

    @FXML
    public void onRemoveGame() {
        Game selected = gameTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            games.remove(selected);
        }
    }
}
