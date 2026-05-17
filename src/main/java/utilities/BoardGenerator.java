package utilities;

import entities.*;
import interface_adapters.IObtainable;

import java.util.*;

import static utilities.GoalUtility.*;

public class BoardGenerator {
    public static final Random RANDOM = new Random();
    public static boolean PREVENT_MULTIPLE_SAVES = true;
    public static double GEO_LIMIT_CHANCE = -1.0;

    public static final String[] MAJORS = {
            "Monarch Wings", "Crystal Heart", "Lumafly Lantern", "Desolate Dive",
            "Dream Nail", "Dreamgate", "Abyss Shriek", "Howling Wraiths",
            "Descending Dark", "Shade Cloak", "Isma's Tear"};

    public static final double INCREASED_MAJOR_CHANCE = 0.15;
    public static boolean INCREASE_MAJOR_CHANCE = false;

    private static void printPlayerGoals(String playerName, ArrayList<IObtainable> goals, String color) {
        final String RESET = "\u001B[0m";
        System.out.println(color + "=== " + playerName + " ===" + RESET);
        for (IObtainable goal : goals) {
            System.out.println(color + "- " + goal.getName() + RESET);
        }
        System.out.println();
    }

    public static Board generateBoardRobin(List<IObtainable> goals, int seed) {
        RANDOM.setSeed(seed);

        if (GEO_LIMIT_CHANCE == -1.0) {
            GEO_LIMIT_CHANCE = (double) 3 / goals.size();
        }

        // guess what list implementation i like the most
        ArrayList<IObtainable> newGoals = (ArrayList<IObtainable>) deepCopyGoals(goals);
        ArrayList<IObtainable> p1 = new ArrayList<>();
        ArrayList<IObtainable> p2 = new ArrayList<>();
        ArrayList<IObtainable> p3 = new ArrayList<>();
        ArrayList<IObtainable> p4 = new ArrayList<>();

        ArrayList<ArrayList<IObtainable>> board = new ArrayList<>();
        board.add(p1);
        board.add(p2);
        board.add(p3);
        board.add(p4);

        ArrayList<IObtainable> usedGoals = new ArrayList<>();

        for (int round = 0; round < 6; round++) {
            pickGoal(newGoals,  p1, usedGoals);
            pickGoal(newGoals,  p2, usedGoals);
            pickGoal(newGoals,  p3, usedGoals);
            pickGoal(newGoals,  p4, usedGoals);
        }

        // by gori
        ArrayList<IObtainable> centerSquareTheory = new ArrayList<>();

        // artificially inject geo / grub goals
        // blomsom reference
        boolean possibilityOfGeocitation = RANDOM.nextDouble() < GEO_LIMIT_CHANCE;
        boolean possibilityOfGrubcipitation = RANDOM.nextDouble() < (double) 2 / goals.size();
        boolean possibilityOfTollicitation = RANDOM.nextDouble() < (double) 1 / goals.size();

        if (possibilityOfGeocitation) {
            reduceInflation(board, getGoalByName(goals, "Spend 3000 geo"),
                                   getGoalByName(goals, "Spend 4000 geo"),
                                   getGoalByName(goals, "Spend 5000 geo"));
        }

        if (possibilityOfGrubcipitation) {
            injectGrubs(board, getGoalByName(goals, "Save 15 grubs"),
                               getGoalByName(goals, "Save 20 grubs"));
        }

        if (possibilityOfTollicitation) {
            depositTolls(board, getGoalByName(goals, "Pay for 6 tolls"));
        }

        pickGoal(newGoals, centerSquareTheory, usedGoals);
        printPlayerGoals("player1", p1, "");
        printPlayerGoals("player2", p2, "");
        printPlayerGoals("player3", p3, "");
        printPlayerGoals("player4", p4, "");
        GoalUtility.printGoals(newGoals);
        return new Board(board, centerSquareTheory.get(0));
    }
    private static void reduceInflation(ArrayList<ArrayList<IObtainable>> board,
                                        IObtainable threeK,
                                        IObtainable fourK,
                                        IObtainable fiveK) {
        IObtainable[] geoGoals = { threeK, fourK, fiveK };
        int[] bounds = { 3000, 4000, 5000 };

        for (int i = 0; i < bounds.length; i++) {
            int bound = bounds[i];
            IObtainable geoGoal = geoGoals[i];

            ArrayList<ArrayList<IObtainable>> below = new ArrayList<>();
            ArrayList<IObtainable> targetPlayer = null;

            for (ArrayList<IObtainable> player : board) {
                int geo = getMaximalSpentGeo(player);

                if (geo < bound) {
                    below.add(player);
                } else if (targetPlayer == null) {
                    targetPlayer = player;
                } else {
                    // more than one player above this bound, so this goal is illegal
                    targetPlayer = null;
                    break;
                }
            }

            // all players below geo bound
            if (below.size() == board.size()) {
                targetPlayer = below.get(RANDOM.nextInt(below.size()));
            }

            // only one below geo bound
            if (targetPlayer != null) {
                ArrayList<IObtainable> goalPool = getGoalsBeforeGeoLimit(targetPlayer, bound);
                printPlayerGoals("before geo", goalPool, "");

                if (goalPool.isEmpty()) {
                    return; // give up
                }

                IObtainable leastImportantGoal = selectLeastImportantGoal(goalPool);
                targetPlayer.set(targetPlayer.indexOf(leastImportantGoal), geoGoal);

                System.out.println("Replacing " + leastImportantGoal.getName()
                        + " with " + (bound / 1000) + "k");

                return;
            }

            // illegal bound
        }
    }

    private static ArrayList<IObtainable> getGoalsBeforeGeoLimit(ArrayList<IObtainable> player, int limit) {
        ArrayList<IObtainable> currentPool = new ArrayList<>();
        for (IObtainable goal : player) {
            currentPool.add(goal);
            if (getMaximalSpentGeo(currentPool) > limit) {
                currentPool.remove(getGoalByName(currentPool, goal.getName()));
                return currentPool;
            }
        }
        return player;
    }

    private static int getMaximalSpentGeo(ArrayList<IObtainable> player) {
        ArrayList<IObtainable> graph = TopologicalSort.constructOrderingGraph(player, new ArrayList<>());
        // loop through the graph, always pick the most expensive option
        int max = 0;
        for (IObtainable goal : graph) {
            int maxInOptions = 0;
            for (ObtainOption option : goal.getDependencies()) {
                if (option.getEffect().getGeoSpent() >= maxInOptions) {
                    maxInOptions = option.getEffect().getGeoSpent();
                }
            }
            max += maxInOptions;
        }
        return max;
    }

    private static void depositTolls(ArrayList<ArrayList<IObtainable>> board, IObtainable sixTolls) {
        ArrayList<ArrayList<IObtainable>> available = new ArrayList<>();
        ArrayList<IObtainable> targetPlayer = null;
        for (ArrayList<IObtainable> player : board) {
            int tolls = getMaximalTollCount(player);

            if (tolls < 6) {
                available.add(player);
            } else if (targetPlayer == null) {
                targetPlayer = player;
            } else {
                return;
            }
        }

        // all players can have 6 tolls
        if (available.size() == board.size()) {
            targetPlayer = available.get(RANDOM.nextInt(available.size()));
        }

        IObtainable leastImportantGoal = selectLeastImportantGoal(targetPlayer);
        targetPlayer.set(targetPlayer.indexOf(leastImportantGoal), sixTolls);
        System.out.println("Replacing " + leastImportantGoal.getName() + " with 6 tolls");
    }

    private static int getMaximalTollCount(ArrayList<IObtainable> player) {
        ArrayList<IObtainable> graph = TopologicalSort.constructOrderingGraph(player, new ArrayList<>());
        // loop through the graph, always pick the most toll expensive option
        int max = 0;
        for (IObtainable goal : graph) {
            int maxInOptions = 0;
            for (ObtainOption option : goal.getDependencies()) {
                if (option.getEffect().getTolls() >= maxInOptions) {
                    maxInOptions = option.getEffect().getTolls();
                }
            }
            max += maxInOptions;
        }
        return max;
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
        int limit = 50;
        System.out.println(needsMultipleSaves(usedGoals, goal));
        while (PREVENT_MULTIPLE_SAVES && needsMultipleSaves(usedGoals, goal) && limit > 0) {
            goal = GoalUtility.selectRandomGoal(pool);

            limit -= 1; // prevent inf. loop
        }

        if (INCREASE_MAJOR_CHANCE && RANDOM.nextDouble() < INCREASED_MAJOR_CHANCE) {
            ArrayList<IObtainable> majors = getGoals(pool, MAJORS);
            if (!majors.isEmpty()) {
                goal = majors.get(RANDOM.nextInt(majors.size()));
            }
        }
        playerGoals.add(goal);
        deepRemove(pool, goal);
        for (IObtainable usedGoal : usedGoals) {
            deepRemove(pool, usedGoal);
        }

        usedGoals.add(goal);
    }

    private static ArrayList<IObtainable> getGoals(List<IObtainable> pool, String[] majors) {
        ArrayList<IObtainable> returnList = new ArrayList<>();
        for (String major : majors) {
            IObtainable goal = getGoalByName(pool, major);
            if (goal == null) {
                continue;
            } else {
                returnList.add(goal);
            }
        }
        return returnList;
    }

    /**
     * Updates the goals list to only contain goals that can be obtained without the goal
     * and are not required to obtain the goal.
     */
    public static void deepRemove(List<IObtainable> goals, IObtainable goal) {
        goals.remove(getGoalByName(goals, goal.getName()));

        removeAllDependents(goals, goal);
        removeAllDependencies(goals, goal);
    }

    /**
     * Remove all goals from newPool that are required to obtain i.
     */
    private static void removeAllDependencies(List<IObtainable> newPool, IObtainable i) {

        System.out.println("\n[Backwards Step] Checking dependencies for: " + i.getName() + ".");

        ArrayList<IObtainable> toBeRemoved = new ArrayList<>();

        // case 1: only one way to obtain i, so all those goals cannot be in the pool
        if (i.getDependencies().size() == 1) {
            System.out.println("only one obtain option exists.");
            toBeRemoved.addAll(i.getDependencies().get(0).getDependencies());

        }

        //case 2: multiple ways, but all those ways have one shared goal
        if (i.getDependencies().size() > 1) {
            Set<IObtainable> shared = new HashSet<>(i.getDependencies().get(0).getDependencies());

            for (ObtainOption option : i.getDependencies()) {
                shared.retainAll(option.getDependencies());
            }
            toBeRemoved.addAll(shared);
        }


        for (IObtainable removed : toBeRemoved) {
            if (removed instanceof Objective) {
                continue;
            }
            System.out.println("- " + removed.getName());
            newPool.remove(getGoalByName(newPool, removed.getName()));
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

                    System.out.println("[Forward Trace] " + goal.getName() + " lost an option because it depended on: " + i.getName());

                    iterator.remove();

                    if (goal.getDependencies().isEmpty()) {
                        toBeRemoved.add(goal);
                    }
                }
            }
        }

        for (IObtainable removed : toBeRemoved) {
            System.out.println("[REMOVED DEPENDENCY] " + removed.getName());
            newPool.remove(getGoalByName(newPool, removed.getName()));
            removeAllDependents(newPool, removed);
        }
    }

    public static Board generateRGO(ArrayList<IObtainable> goals, int seed) {
        RANDOM.setSeed(seed);

        if (GEO_LIMIT_CHANCE == -1.0) {
            GEO_LIMIT_CHANCE = (double) 3 / goals.size();
        }

        // guess what list implementation i like the most
        ArrayList<IObtainable> newGoals = (ArrayList<IObtainable>) deepCopyGoals(goals);
        ArrayList<IObtainable> p1 = new ArrayList<>();
        ArrayList<ArrayList<IObtainable>> board = new ArrayList<>();
        board.add(p1);
        ArrayList<IObtainable> usedGoals = new ArrayList<>();

        for (int round = 0; round < 25; round++) {
            pickGoal(newGoals,  p1, usedGoals);
        }
        // artificially inject geo / grub goals
        // blomsom reference
        boolean possibilityOfGeocitation = RANDOM.nextDouble() < GEO_LIMIT_CHANCE;
        boolean possibilityOfGrubcipitation = RANDOM.nextDouble() < (double) 2 / goals.size();
        boolean possibilityOfTollicitation = RANDOM.nextDouble() < (double) 1 / goals.size();

        if (possibilityOfGeocitation) {
            reduceInflation(board, getGoalByName(goals, "Spend 3000 geo"),
                    getGoalByName(goals, "Spend 4000 geo"),
                    getGoalByName(goals, "Spend 5000 geo"));
        }

        if (possibilityOfGrubcipitation) {
            injectGrubs(board, getGoalByName(goals, "Save 15 grubs"),
                    getGoalByName(goals, "Save 20 grubs"));
        }

        if (possibilityOfTollicitation) {
            depositTolls(board, getGoalByName(goals, "Pay for 6 tolls"));
        }

        return new Board(board);
    }
}
