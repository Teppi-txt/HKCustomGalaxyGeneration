package utilities;

import entities.Objective;
import entities.ObtainOption;
import interface_adapters.IObtainable;

import java.lang.reflect.Array;
import java.util.*;

public class BoardGenerator {
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

    public static void generateBoardLinear(ArrayList<IObtainable> goals) {
        TopologicalSort sorter = new  TopologicalSort();
        ArrayList<IObtainable> p1_goals = selectRandomGoals(sorter.sort_and_reduce(goals), 12);

        ArrayList<IObtainable> pool = new ArrayList<>(goals);
        for (IObtainable i : p1_goals) {
            removeGoalAndDependents(pool, i);
        }
        ArrayList<IObtainable> p2_goals = selectRandomGoals(sorter.sort_and_reduce(pool), 12);

        for (IObtainable i : p2_goals) {
            removeGoalAndDependents(pool, i);
        }
        ArrayList<IObtainable> p3_goals = selectRandomGoals(sorter.sort_and_reduce(pool), 12);

        for (IObtainable i : p3_goals) {
            removeGoalAndDependents(pool, i);
        }
        ArrayList<IObtainable> p4_goals = selectRandomGoals(sorter.sort_and_reduce(pool), 6);

        // Print all players with colors
        printPlayerGoals("Player 1", p1_goals, "\u001B[31m"); // Red
        printPlayerGoals("Player 2", p2_goals, "\u001B[32m"); // Green
        printPlayerGoals("Player 3", p3_goals, "\u001B[34m"); // Blue
        printPlayerGoals("Player 4", p4_goals, "\u001B[35m"); // Purple
    }

    private static void printPlayerGoals(String playerName,
                                         ArrayList<IObtainable> goals,
                                         String color) {
        final String RESET = "\u001B[0m";
        System.out.println(color + "=== " + playerName + " ===" + RESET);
        for (IObtainable goal : goals) {
            System.out.println(color + "- " + goal.getName() + RESET);
        }
        System.out.println();
    }

    private static void removeGoalAndDependents(ArrayList<IObtainable> pool, IObtainable i) {
        ArrayList<IObtainable> to_be_removed = new ArrayList<>();
        removeAllDependencies(pool, i);

        for (IObtainable p : pool) {
            p.getDependencies().removeIf(branch -> branch.getDependencies().contains(i));

            if (p.getDependencies().isEmpty()) {
                to_be_removed.add(p);
            }
        }
        to_be_removed.add(i);

        pool.removeAll(to_be_removed);
    }

    private static void removeAllDependencies(ArrayList<IObtainable> newPool, IObtainable i) {
        ArrayList<IObtainable> to_be_removed = new ArrayList<>();

        for (ObtainOption o : i.getDependencies()) {
            for (IObtainable p : o.getDependencies()) {
                if (!to_be_removed.contains(p)) {
                    to_be_removed.add(p);
                }
                removeAllDependencies(newPool, p);
            }
        }
        newPool.removeAll(to_be_removed);
    }
}
