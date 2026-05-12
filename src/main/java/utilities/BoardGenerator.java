package utilities;

import entities.*;
import interface_adapters.IObtainable;

import java.lang.reflect.Array;
import java.util.*;

public class BoardGenerator {
    public static ArrayList<IObtainable> deepCopyGoals(ArrayList<IObtainable> goals) {
        Map<String, IObtainable> byName = new HashMap<>();
        ArrayList<IObtainable> result = new ArrayList<>();

        // first pass, just the new goal objects
        for (IObtainable goal : goals) {
            IObtainable new_goal;

            if (goal instanceof CollectionGoal collectionGoal) {
                new_goal = new CollectionGoal(
                        collectionGoal.getName(),
                        new ArrayList<>(),
                        collectionGoal.getCollectionCount()
                );
            } else if (goal instanceof Objective) {
                new_goal = new Objective(
                        goal.getName(),
                        new ArrayList<>()
                );
            } else {
               new_goal = new AchievementGoal(
                        goal.getName(),
                        new ArrayList<>()
                );
            }

            byName.put(goal.getName(), new_goal);
            result.add(new_goal);
        }

        // second pass, new dependencies
        for (IObtainable goal : goals) {
            if (goal instanceof CollectionGoal collectionGoal) {
                for (IObtainable i : collectionGoal.getCollectionItems()) {
                    // terrible terrible code
                    ((CollectionGoal) byName.get(goal.getName())).getCollectionItems().add(byName.get(i.getName()));
                }
            } else {
                for (ObtainOption o : goal.getDependencies()) {
                    HashSet<IObtainable> set = new HashSet<>();
                    for (IObtainable dependency : o.getDependencies()) {
                        set.add(byName.get(dependency.getName()));
                    }

                    byName.get(goal.getName()).getDependencies().add(new ObtainOption(set, o.getEffect()));
                }
            }
        }
        return result;
    }

    public static ArrayList<IObtainable> getOnePath(ArrayList<IObtainable> goals) {
        ArrayList<IObtainable> result = deepCopyGoals(goals);

        for (IObtainable goal : result) {
            if (goal.getDependencies().size() < 1) {

            }
        }
        return null;
    }

    private static void printPlayerGoals(String playerName, ArrayList<IObtainable> goals, String color) { final String RESET = "\u001B[0m"; System.out.println(color + "=== " + playerName + " ===" + RESET); for (IObtainable goal : goals) { System.out.println(color + "- " + goal.getName() + RESET); } System.out.println(); }

    public static void generateBoardRobin(ArrayList<IObtainable> goals) {
        ArrayList<IObtainable> p1_result = new ArrayList<>();
        ArrayList<IObtainable> p2_result = new ArrayList<>();
        ArrayList<IObtainable> p3_result = new ArrayList<>();
        ArrayList<IObtainable> p4_result = new ArrayList<>();
        ArrayList<IObtainable> usedGoals = new ArrayList<>();

        for (int round = 0; round < 6; round++) {
            IObtainable randomGoal = GoalUtility.selectRandomGoal(goals);
            p1_result.add(randomGoal);
            deepRemove(goals, randomGoal);

            for (IObtainable goal : usedGoals) {
                deepRemove(goals, goal);
            }
            usedGoals.add(randomGoal);


            randomGoal = GoalUtility.selectRandomGoal(goals);
            p2_result.add(randomGoal);
            deepRemove(goals, randomGoal);

            for (IObtainable goal : usedGoals) {
                deepRemove(goals, goal);
            }
            usedGoals.add(randomGoal);

            randomGoal = GoalUtility.selectRandomGoal(goals);
            p3_result.add(randomGoal);
            deepRemove(goals, randomGoal);

            for (IObtainable goal : usedGoals) {
                deepRemove(goals, goal);
            }
            usedGoals.add(randomGoal);

            randomGoal = GoalUtility.selectRandomGoal(goals);
            p4_result.add(randomGoal);
            deepRemove(goals, randomGoal);

            for (IObtainable goal : usedGoals) {
                deepRemove(goals, goal);
            }
            usedGoals.add(randomGoal);
        }

        System.out.println("\n----------------------------------------------");
        printPlayerGoals("Player 1", p1_result, "\u001B[31m"); // Red
        printPlayerGoals("Player 2", p2_result, "\u001B[32m"); // Green
        printPlayerGoals("Player 3", p3_result, "\u001B[34m"); // Blue
        printPlayerGoals("Player 4", p4_result, "\u001B[33m"); // Yellow
    }

    private static void deepRemove(ArrayList<IObtainable> goals, IObtainable goal) {
        goals.remove(goal);

        removeAllDependents(goals, goal);
        removeAllDependencies(goals, goal);
    }

    /**
     * Remove all goals from newPool that are required to obtain i.
     */
    private static void removeAllDependencies(ArrayList<IObtainable> newPool, IObtainable i) {

        System.out.println("\n[removeAllDependencies] Checking dependencies for: " + i.getName());

        ArrayList<IObtainable> toBeRemoved = new ArrayList<>();

        if (i.getDependencies().size() == 1) {

            System.out.println("Only one obtain option exists.");
            toBeRemoved.addAll(i.getDependencies().get(0).getDependencies());
        }

        for (IObtainable removed : toBeRemoved) {
            if (removed instanceof Objective) {
                System.out.println("[NOT REMOVED OBJECTIVE] " + removed.getName());
                continue;
            }
            System.out.println("[REMOVED DEPENDENCY] " + removed.getName());
            newPool.remove(removed);
            removeAllDependencies(newPool, removed);
        }
    }

    /**
     * Remove all options from goals in newPool that are dependent on obtaining i.
     */
    private static void removeAllDependents(ArrayList<IObtainable> newPool, IObtainable i) {

        ArrayList<IObtainable> toBeRemoved = new ArrayList<>();

        for (IObtainable goal : newPool) {

            Iterator<ObtainOption> iterator = goal.getDependencies().iterator();

            while (iterator.hasNext()) {

                ObtainOption option = iterator.next();

                if (option.getDependencies().contains(i)) {

                    System.out.println(
                            "[REMOVED OPTION] " + goal.getName() + " lost an obtain option because it depended on: " + i.getName()
                    );

                    iterator.remove();

                    if (goal.getDependencies().isEmpty()) {
                        toBeRemoved.add(goal);
                    }
                }
            }
        }

        for (IObtainable removed : toBeRemoved) {
            if (removed instanceof Objective) {
                System.out.println("[NOT REMOVED OBJECTIVE] " + removed.getName());
                continue;
            }
            System.out.println("[REMOVED DEPENDENCY] " + removed.getName());
            newPool.remove(removed);
            removeAllDependents(newPool, removed);
        }
    }
}
