module logo.philist.boardgame_tournament {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;


    opens logo.philist.boardgame_tournament to javafx.fxml;
    exports logo.philist.boardgame_tournament;
    exports logo.philist.boardgame_tournament.controller;
    opens logo.philist.boardgame_tournament.controller to javafx.fxml;
    exports logo.philist.boardgame_tournament.controller.round;
    opens logo.philist.boardgame_tournament.controller.round to javafx.fxml;
    exports logo.philist.boardgame_tournament.controller.distribution;
    opens logo.philist.boardgame_tournament.controller.distribution to javafx.fxml;
    exports logo.philist.boardgame_tournament.model;
    opens logo.philist.boardgame_tournament.model to com.fasterxml.jackson.databind;
}