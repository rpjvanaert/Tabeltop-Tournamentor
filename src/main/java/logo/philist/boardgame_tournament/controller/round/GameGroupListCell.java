package logo.philist.boardgame_tournament.controller.round;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import logo.philist.boardgame_tournament.model.GameGroup;

public class GameGroupListCell extends ListCell<GameGroup> {
    private Node root;
    private GameGroupCellController controller;

    @Override
    protected void updateItem(GameGroup group, boolean empty) {
        super.updateItem(group, empty);
        if (empty || group == null) {
            setGraphic(null);
        } else {
            if (root == null) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/logo/philist/boardgame_tournament/game-group-cell.fxml"));
                    root = loader.load();
                    controller = loader.getController();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            controller.setGameGroup(group);
            setGraphic(root);
        }
    }
}
