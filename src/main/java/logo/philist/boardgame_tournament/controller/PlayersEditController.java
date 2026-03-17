package logo.philist.boardgame_tournament.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import logo.philist.boardgame_tournament.model.Player;
import logo.philist.boardgame_tournament.storage.Storage;

public class PlayersEditController {

    private BaseController baseController;

    @FXML
    private TableView<Player> playerTable;

    @FXML
    private TableColumn<Player, String> nameColumn;

    public void setPlayers(ObservableList<Player> players) {
        playerTable.setItems(players);
    }

    public void setBaseController(BaseController controller) {
        this.baseController = controller;
    }

    @FXML
    public void initialize() {
        // Custom cell factory: show placeholder text and style when name is empty
        nameColumn.setCellFactory(col -> new TextFieldTableCell<Player, String>(new StringConverter<>() {
            @Override
            public String toString(String object) {
                return object;
            }

            @Override
            public String fromString(String string) {
                return string;
            }
        }) {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    if (item == null || item.trim().isEmpty()) {
                        setText("Enter name...");
                        // subtle styling for placeholder
                        setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
                    } else {
                        setText(item);
                        setStyle("");
                    }
                }
            }
        });

        // Bind cell value to player's nameProperty
        nameColumn.setCellValueFactory(cd -> cd.getValue().nameProperty());

        // Commit edits to the underlying Player instance
        nameColumn.setOnEditCommit(event -> {
            Player player = event.getRowValue();
            player.nameProperty().set(event.getNewValue());
        });

        // Row factory: add/remove a CSS class on rows with an empty player name,
        // and listen to nameProperty changes to keep the style in sync while editing.
        playerTable.setRowFactory(tv -> {
            TableRow<Player> row = new TableRow<>();

            ChangeListener<String> nameListener = (obs, oldName, newName) -> {
                updateRowEmptyStyle(row, newName);
            };

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (oldItem != null) {
                    oldItem.nameProperty().removeListener(nameListener);
                }
                if (newItem != null) {
                    newItem.nameProperty().addListener(nameListener);
                    updateRowEmptyStyle(row, newItem.getName());
                } else {
                    // no item -> remove class
                    row.getStyleClass().remove("empty-player-row");
                }
            });

            return row;
        });

        // Key handling for adding/removing rows
        playerTable.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DOWN -> {
                    int selectedIndex = playerTable.getSelectionModel().getSelectedIndex();
                    if (selectedIndex == playerTable.getItems().size() - 1) {
                        int rounds = getCurrentRoundCount();
                        playerTable.getItems().add(createPlayerWithRounds(rounds));
                        selectAndEditLastRow();
                    }
                }
                case DELETE -> {
                    Player selected = playerTable.getSelectionModel().getSelectedItem();
                    if (selected != null) {
                        playerTable.getItems().remove(selected);
                    }
                }
                default -> { /* ignore other keys */ }
            }
        });

        playerTable.setEditable(true);
    }

    private void updateRowEmptyStyle(TableRow<Player> row, String name) {
        if (name == null || name.trim().isEmpty()) {
            if (!row.getStyleClass().contains("empty-player-row")) {
                row.getStyleClass().add("empty-player-row");
            }
        } else {
            row.getStyleClass().remove("empty-player-row");
        }
    }

    private int getCurrentRoundCount() {
        if (playerTable.getItems() != null && !playerTable.getItems().isEmpty()) {
            return playerTable.getItems().get(0).getRoundScores().size();
        }
        return 1;
    }

    private Player createPlayerWithRounds(int rounds) {
        Player p = new Player("");
        for (int i = 1; i < rounds; i++) {
            p.addRound();
        }
        return p;
    }

    @FXML
    public void onSave() {
        Storage.savePlayersObjects(playerTable.getItems());
        if (baseController != null) {
            baseController.loadPlayers();
        }
    }

    public void onAdd() {
        int rounds = getCurrentRoundCount();
        playerTable.getItems().add(createPlayerWithRounds(rounds));
        selectAndEditLastRow();
    }

    private void selectAndEditLastRow() {
        int idx = playerTable.getItems().size() - 1;
        if (idx < 0) return;
        Platform.runLater(() -> {
            playerTable.getSelectionModel().select(idx);
            playerTable.scrollTo(idx);
            // start editing the name cell
            playerTable.requestFocus();
            playerTable.edit(idx, nameColumn);
        });
    }
}
