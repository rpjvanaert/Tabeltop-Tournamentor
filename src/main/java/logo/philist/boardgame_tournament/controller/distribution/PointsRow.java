package logo.philist.boardgame_tournament.controller.distribution;

import java.util.List;

public class PointsRow {
    private final Integer playerCount;
    private final List<Integer> points;

    public PointsRow(Integer playerCount, List<Integer> points) {
        this.playerCount = playerCount;
        this.points = points;
    }

    public Integer getPlayerCount() { return playerCount; }
    public List<Integer> getPoints() { return points; }
}
