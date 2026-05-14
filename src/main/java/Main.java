import entities.*;
import utilities.*;
import interface_adapters.IObtainable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Main {
    public static void printAll(List<IObtainable> goals) {
        for (IObtainable goal : goals) {
            if (goal instanceof AchievementGoal) {
                System.out.println("\u001B[31m" + goal.getName() +  "\u001B[0m"); // Red
                if (goal.getDependencies().size() > 0) {
                    System.out.println(goal.getDependencies().getFirst().getEffect().getGrubsCollected());
                }
            }
            else if (goal instanceof CollectionGoal) {
                System.out.println("\u001B[32m" + goal.getName() + "\u001B[0m"); // Green
            }
            else if (goal instanceof Objective) {
                System.out.println("\u001B[34m" + goal.getName() + "\u001B[0m"); // Blue
                if (goal.getDependencies().size() > 0) {
                    System.out.println(goal.getDependencies().getFirst().getEffect().getGrubsCollected());
                }
            }
            else {
                System.out.println(goal.getName());
            }
        }
    }

    public static void main(String[] args) {
        ArrayList<IObtainable> goals = GoalParser.parseGoals("src/main/resources/hollow_knight_goals.json");
        Board board = BoardGenerator.generateBoardRobin(goals, 127);
        System.out.println(board.generateBoardJSON());
        //printAll(goals);
    }
}

