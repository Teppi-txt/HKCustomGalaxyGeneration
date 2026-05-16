package entities;

import interface_adapters.IObtainable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Board {
    ArrayList<ArrayList<IObtainable>> players;
    IObtainable centerGoal;

    public static Map<String, List<Integer>> glackyIndices = Map.of(
            "player1", List.of(0, 1, 2, 3, 8, 13),
            "player2", List.of(4, 9, 14, 19, 18, 17),
            "player3", List.of(24, 23, 22, 21, 16, 11),
            "player4", List.of(20, 15, 10, 5, 6, 7),
            "neutral", List.of(12)
    );

    public Board(List<ArrayList<IObtainable>> players, IObtainable centerGoal) {
        this.players = (ArrayList<ArrayList<IObtainable>>) players;
        this.centerGoal = centerGoal;
    }

    public String generateBoardJSON() {
        if (players.size() == 4) {
            return generate4PBoard();
        }
        return null;
    }

    private String generate4PBoard() {
        StringBuilder sb = new StringBuilder("[");
        IObtainable[] boardArray = new IObtainable[25];
        int playerCount = 1;

        for (ArrayList<IObtainable> player : players) {
            for (int i = 0; i < player.size(); i++) {
                boardArray[glackyIndices.get("player" + playerCount).get(i)] = player.get(i);
            }
            playerCount += 1;
        }
        boardArray[glackyIndices.get("neutral").getFirst()] = centerGoal;

        for (int i = 0; i < 25; i++) {
            sb.append("{ \"name\" : \"").append(boardArray[i].getName()).append("\" }, \n");
        }
        sb.replace(sb.length() - 3, sb.length(), "]");
        return sb.toString();
    }
}
