package entities;

import interface_adapters.IObtainable;

import java.util.*;

public class Board {
    ArrayList<ArrayList<IObtainable>> players;
    IObtainable centerGoal;

    public static Map<String, List<Integer>> glackyIndices = new HashMap<String, List<Integer>>() {{
        put("player1", Arrays.asList(0, 1, 2, 3, 8, 13));
        put("player2", Arrays.asList(4, 9, 14, 19, 18, 17));
        put("player3", Arrays.asList(24, 23, 22, 21, 16, 11));
        put("player4", Arrays.asList(20, 15, 10, 5, 6, 7));
        put("neutral", Arrays.asList(12));
    }};

    public Board(List<ArrayList<IObtainable>> players, IObtainable centerGoal) {
        this.players = (ArrayList<ArrayList<IObtainable>>) players;
        this.centerGoal = centerGoal;
    }

    public Board(List<ArrayList<IObtainable>> players) {
        this.players = (ArrayList<ArrayList<IObtainable>>) players;
        this.centerGoal = null;
    }

    public String generateBoardJSON() {
        if (players.size() == 4) {
            return generate4PBoard();
        }
        return generateRGOBoard();
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
        boardArray[glackyIndices.get("neutral").get(0)] = centerGoal;

        for (int i = 0; i < 25; i++) {
            sb.append("{ \"name\" : \"").append(boardArray[i].getName()).append("\" }, \n");
        }
        sb.replace(sb.length() - 3, sb.length(), "]");
        return sb.toString();
    }

    private String generateRGOBoard() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < 25; i++) {
            sb.append("{ \"name\" : \"").append(players.get(0).get(i).getName()).append("\" }, \n");
        }
        sb.replace(sb.length() - 3, sb.length(), "]");
        return sb.toString();
    }
}
