package logo.philist.boardgame_tournament.controller.round;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import logo.philist.boardgame_tournament.model.GameGroup;

public class GameGroupCellController {

    @FXML
    private Label groupNameLabel;

    @FXML
    private Label playerCountLabel;

    @FXML
    private ListView<String> playersListView;

    public void setGameGroup(GameGroup group) {
        groupNameLabel.setText(group.getGame().getName());
        playerCountLabel.setText("(" + group.getPlayers().size() + " players)");

        var items = FXCollections.observableArrayList(
                group.getPlayers().stream()
                        .map(Object::toString)
                        .toList()
        );
        playersListView.setItems(items);

        double cellHeight = 24; // Adjust if your cell height is different
        playersListView.setFixedCellSize(cellHeight);
        playersListView.setPrefHeight(items.size() * cellHeight + 2);

        // Optional: update height if items change
        items.addListener((javafx.collections.ListChangeListener<String>) c -> {
            playersListView.setPrefHeight(items.size() * cellHeight + 2);
        });
    }
}
