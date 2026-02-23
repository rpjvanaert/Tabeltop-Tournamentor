package logo.philist.boardgame_tournament.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import logo.philist.boardgame_tournament.model.Player;
import logo.philist.boardgame_tournament.storage.Storage;

import java.util.List;
import java.util.stream.Collectors;

public class PlayersEditController {

    private BaseController baseController;

    @FXML
    private TableView<String> playerTable;

    @FXML
    private TableColumn<String, String> nameColumn;

    public void setPlayers(ObservableList<Player> players) {
        ObservableList<String> playerNames = players.stream()
                .map(player -> player.nameProperty().getValue())
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        playerTable.setItems(playerNames);
    }

    public void setBaseController(BaseController controller) {
        this.baseController = controller;
    }

    @FXML
    public void initialize() {
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue()));

        playerTable.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.DOWN) {
                int selectedIndex = playerTable.getSelectionModel().getSelectedIndex();
                if (selectedIndex == playerTable.getItems().size() - 1) {
                    playerTable.getItems().add("");
                }
            } else if (event.getCode() == javafx.scene.input.KeyCode.DELETE) {
                int selectedIndex = playerTable.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    playerTable.getItems().remove(selectedIndex);
                }
            }
        });

        nameColumn.setOnEditCommit(event -> {
            int row = event.getTablePosition().getRow();
            playerTable.getItems().set(row, event.getNewValue());
        });
        playerTable.setEditable(true);
    }

    @FXML
    public void onSave() {
        List<Player> players = playerTable.getItems().stream().map(Player::new).toList();
        Storage.savePlayersObjects(players);
        baseController.loadPlayers();
    }

    public void onAdd() {
        playerTable.getItems().add("");
    }
}
