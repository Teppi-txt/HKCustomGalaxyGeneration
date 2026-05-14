package utilities;

import entities.*;
import interface_adapters.IObtainable;

import java.util.*;

import static utilities.GoalUtility.*;

public class BoardGenerator {
    // private static final Logger LOGGER = Logger.getLogger(BoardGenerator.class.getName());

    public static final Random RANDOM = new Random();

    private static void printPlayerGoals(String playerName, ArrayList<IObtainable> goals, String color) { final String RESET = "\u001B[0m"; System.out.println(color + "=== " + playerName + " ===" + RESET); for (IObtainable goal : goals) { System.out.println(color + "- " + goal.getName() + RESET); } System.out.println(); }

    public static Board generateBoardRobin(List<IObtainable> goals, int seed) {
        RANDOM.setSeed(seed);
        ArrayList<IObtainable> newGoals = (ArrayList<IObtainable>) deepCopyGoals(goals);
        ArrayList<IObtainable> p1 = new ArrayList<>();
        ArrayList<IObtainable> p2 = new ArrayList<>();
        ArrayList<IObtainable> p3 = new ArrayList<>();
        ArrayList<IObtainable> p4 = new ArrayList<>();
        ArrayList<ArrayList<IObtainable>> board = new ArrayList<>(List.of(p1, p2, p3, p4));
        ArrayList<IObtainable> usedGoals = new ArrayList<>();
        ArrayList<IObtainable> centerSquareTheory = new ArrayList<>();

        for (int round = 0; round < 6; round++) {
            pickGoal(newGoals,  p1, usedGoals);
            pickGoal(newGoals,  p2, usedGoals);
            pickGoal(newGoals,  p3, usedGoals);
            pickGoal(newGoals,  p4, usedGoals);
        }

        // artificially inject geo / grub goals
        // blomsom reference
        boolean possibilityOfGeocitation = RANDOM.nextDouble() < 0.2;
        boolean possibilityOfGrubcipitation = RANDOM.nextDouble() < 1;

        if (possibilityOfGrubcipitation) {
            injectGrubs(board, getGoalByName(goals, "Save 15 grubs"), getGoalByName(goals, "Save 20 grubs"));
        }

        pickGoal(newGoals, centerSquareTheory, usedGoals);
        return new Board(board, centerSquareTheory.getFirst());
    }

    private static void injectGrubs(ArrayList<ArrayList<IObtainable>> board, IObtainable grub15, IObtainable grub20) {
        ArrayList<ArrayList<IObtainable>> below15Grubs = new ArrayList<>();
        ArrayList<ArrayList<IObtainable>> below20Grubs = new ArrayList<>();

        for (ArrayList<IObtainable> player : board) {
            int grubbies = getMaximalGrubsCount(player);

            if (grubbies < 15) {
                below15Grubs.add(player);
                below20Grubs.add(player);
            } else if (grubbies < 20) {
                below20Grubs.add(player);
            }
        }


        boolean use15Grubs = RANDOM.nextDouble() < 0.5;
        ArrayList<IObtainable> randomPlayer = use15Grubs
                ? below15Grubs.get(RANDOM.nextInt(below15Grubs.size()))
                : below20Grubs.get(RANDOM.nextInt(below20Grubs.size()));

        IObtainable grub = use15Grubs ? grub15 : grub20;
        IObtainable leastImportantGoal = selectLeastImportantGoal(randomPlayer);
        randomPlayer.set(randomPlayer.indexOf(leastImportantGoal), grub);
    }

    private static int getMaximalGrubsCount(ArrayList<IObtainable> player) {
        int max = 0;
        // there are no intermediate grub goals, don't have to recurse
        for (IObtainable goal : player) {
            int maxInOptions = 0;
            for (ObtainOption option : goal.getDependencies()) {
                if (option.getEffect().getGrubsCollected() >= maxInOptions) {
                    maxInOptions = option.getEffect().getGrubsCollected();
                }
            }
            max += maxInOptions;
        }
        return max;
    }

    private static void pickGoal(List<IObtainable> pool, List<IObtainable> playerGoals,
                                 List<IObtainable> usedGoals) {
        IObtainable goal = GoalUtility.selectRandomGoal(pool);
        playerGoals.add(goal);
        deepRemove(pool, goal);
        for (IObtainable usedGoal : usedGoals) {
            deepRemove(pool, usedGoal);
        }

        usedGoals.add(goal);
    }

    /**
     * Updates the goals list to only contain goals that can be obtained without the goal
     * and are not required to obtain the goal.
     */
    public static void deepRemove(List<IObtainable> goals, IObtainable goal) {
        goals.remove(goal);

        removeAllDependents(goals, goal);
        removeAllDependencies(goals, goal);
    }

    /**
     * Remove all goals from newPool that are required to obtain i.
     */
    private static void removeAllDependencies(List<IObtainable> newPool, IObtainable i) {

        //System.out.println("\n[Backwards Step] Checking dependencies for: " + i.getName() + ".");

        ArrayList<IObtainable> toBeRemoved = new ArrayList<>();

        if (i.getDependencies().size() == 1) {

            //System.out.println("only one obtain option exists.");
            toBeRemoved.addAll(i.getDependencies().get(0).getDependencies());
        }

        for (IObtainable removed : toBeRemoved) {
            if (removed instanceof Objective) {
                continue;
            }
            //System.out.println("- " + removed.getName());
            newPool.remove(removed);
            removeAllDependencies(newPool, removed);
        }
    }

    /**
     * Remove all options from goals in newPool that are dependent on obtaining i.
     */
    private static void removeAllDependents(List<IObtainable> newPool, IObtainable i) {

        ArrayList<IObtainable> toBeRemoved = new ArrayList<>();

        for (IObtainable goal : newPool) {

            Iterator<ObtainOption> iterator = goal.getDependencies().iterator();

            while (iterator.hasNext()) {

                ObtainOption option = iterator.next();

                if (option.getDependencies().contains(i)) {

                    //System.out.println("[Forward Trace] " + goal.getName() + " lost an option because it depended on: " + i.getName());

                    iterator.remove();

                    if (goal.getDependencies().isEmpty()) {
                        toBeRemoved.add(goal);
                    }
                }
            }
        }

        for (IObtainable removed : toBeRemoved) {
            if (removed instanceof Objective) {
                //System.out.println("[NOT REMOVED OBJECTIVE] " + removed.getName());
                continue;
            }
            //System.out.println("[REMOVED DEPENDENCY] " + removed.getName());
            newPool.remove(removed);
            removeAllDependents(newPool, removed);
        }
    }
}
