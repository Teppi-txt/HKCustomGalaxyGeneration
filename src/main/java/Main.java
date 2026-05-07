import entities.*;
import utilities.*;
import interface_adapters.IObtainable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Main {

    public static void printGoals(List<IObtainable> goals) {
        for (IObtainable goal : goals) {
            if (!(goal instanceof Objective g)) {
                System.out.println(goal.getName());
            }
        }
    }

    public static void printAll(List<IObtainable> goals) {
        for (IObtainable goal : goals) {
            if (goal instanceof AchievementGoal) {
                System.out.println("\u001B[31m" + goal.getName() + "\u001B[0m"); // Red
            }
            else if (goal instanceof CollectionGoal) {
                System.out.println("\u001B[32m" + goal.getName() + "\u001B[0m"); // Green
            }
            else if (goal instanceof Objective) {
                System.out.println("\u001B[34m" + goal.getName() + "\u001B[0m"); // Blue
            }
            else {
                System.out.println(goal.getName());
            }
        }
    }


    public static void main(String[] args) {
        ArrayList<IObtainable> goals = GoalParser.parseGoals("src/main/resources/hollow_knight_goals.json");
        BoardGenerator.generateBoardLinear(goals);
//        TopologicalSort ts = new  TopologicalSort();
//        printAll(ts.sort_and_reduce(goals));
    }
}

