package logo.philist.boardgame_tournament;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class BaseApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(BaseApplication.class.getResource("base-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1600, 1080);
        stage.setTitle("Tabletop Tournamentor");
        stage.setScene(scene);
        stage.show();
    }
}
