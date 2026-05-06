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
            if (goal instanceof AchievementGoal g) {
                System.out.println(g.getName());
            }
        }
    }

    public static ArrayList<IObtainable> selectRandomGoals(List<IObtainable> allItems, int count) {
        List<Integer> validIndices = new ArrayList<>();

        for (int i = 0; i < allItems.size(); i++) {
            if (!(allItems.get(i) instanceof Objective)) {
                validIndices.add(i);
            }
        }

        Collections.shuffle(validIndices);
        Set<Integer> selected = new HashSet<>(
            validIndices.subList(0, Math.min(count, validIndices.size()))
        );

        ArrayList<IObtainable> result = new ArrayList<>();

        for (int i = 0; i < allItems.size(); i++) {
            if (selected.contains(i)) {
                result.add(allItems.get(i));
            }
        }

        return result;
    }

    public static void main(String[] args) {
        ArrayList<IObtainable> goals = GoalParser.parseGoals("src/main/resources/hollow_knight_goals.json");
        
        TopologicalSort topologicalSort = new TopologicalSort();
        ArrayList<IObtainable> player1 = selectRandomGoals(topologicalSort.sort_and_reduce(goals), 20); //null if cycle detected
        printGoals(player1);
    }
}

