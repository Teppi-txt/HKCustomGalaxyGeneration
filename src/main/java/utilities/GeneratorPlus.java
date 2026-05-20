package utilities;

import entities.GoalPool;
import entities.ObtainOption;
import entities.PlayerData;
import interface_adapters.Obtainable;

import java.util.ArrayList;

import static utilities.GeneratorCore.RANDOM;
import static utilities.GoalUtility.selectLeastImportantGoal;


public class GeneratorPlus {
    public static final String[] MAJORS = {
            "Monarch Wings", "Crystal Heart", "Lumafly Lantern", "Desolate Dive",
            "Dream Nail", "Dreamgate", "Descending Dark", "Shade Cloak", "Isma's Tear", "Abyss Shriek"};

    static void reduceInflation(ArrayList<PlayerData> board,
                                Obtainable threeK,
                                Obtainable fourK,
                                Obtainable fiveK) {
        Obtainable[] geoGoals = { threeK, fourK, fiveK };
        int[] bounds = { 3000, 4000, 5000 };

        for (int i = 0; i < bounds.length; i++) {
            int bound = bounds[i];
            Obtainable geoGoal = geoGoals[i];

            ArrayList<PlayerData> below = new ArrayList<>();
            PlayerData targetPlayer = null;

            for (PlayerData player : board) {
                int geo = getMaximalSpentGeo(player.getLine());

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
                GoalPool goalPool = getGoalsBeforeGeoLimit(targetPlayer, bound);

                if (goalPool.isEmpty()) {
                    return; // give up
                }

                ArrayList<Obtainable> lineArray = targetPlayer.getLine();
                Obtainable leastImportantGoal = selectLeastImportantGoal(goalPool.getElements());
                lineArray.set(lineArray.indexOf(leastImportantGoal), geoGoal);

                System.out.println("Replacing " + leastImportantGoal.getName()
                        + " with " + (bound / 1000) + "k");

                return;
            }

            // illegal bound
        }
    }

    private static GoalPool getGoalsBeforeGeoLimit(PlayerData player, int limit) {
        GoalPool currentPool = new GoalPool();
        for (Obtainable goal : player.getLine()) {
            currentPool.add(goal);
            if (getMaximalSpentGeo(currentPool.getElements()) > limit) {
                currentPool.remove(goal);
                return currentPool;
            }
        }
        return currentPool;
    }

    private static int getMaximalSpentGeo(ArrayList<Obtainable> goals) {
        ArrayList<Obtainable> graph = TopologicalSort.constructOrderingGraph(new GoalPool(goals), new ArrayList<>()).getElements();
        // loop through the graph, always pick the most expensive option
        int max = 0;
        for (Obtainable goal : graph) {
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

    static void depositTolls(ArrayList<PlayerData> board, Obtainable sixTolls) {
        ArrayList<PlayerData> available = new ArrayList<>();
        PlayerData targetPlayer = null;
        for (PlayerData player : board) {
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

        ArrayList<Obtainable> lineArray = targetPlayer.getLine();
        Obtainable leastImportantGoal = selectLeastImportantGoal(lineArray);
        lineArray.set(lineArray.indexOf(leastImportantGoal), sixTolls);
        System.out.println("Replacing " + leastImportantGoal.getName() + " with 6 tolls");
    }

    private static int getMaximalTollCount(PlayerData player) {
        GoalPool graph = TopologicalSort.constructOrderingGraph(new GoalPool(player.getLine()), new ArrayList<>());
        // loop through the graph, always pick the most toll expensive option
        int max = 0;
        for (Obtainable goal : graph.getElements()) {
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

    static void injectGrubs(ArrayList<PlayerData> board, Obtainable grub15, Obtainable grub20) {
        ArrayList<PlayerData> below15Grubs = new ArrayList<>();
        ArrayList<PlayerData> below20Grubs = new ArrayList<>();

        for (PlayerData player : board) {
            int grubbies = getMaximalGrubsCount(player);

            if (grubbies < 15) {
                below15Grubs.add(player);
                below20Grubs.add(player);
            } else if (grubbies < 20) {
                below20Grubs.add(player);
            }
        }

        boolean use15Grubs = RANDOM.nextDouble() < 0.5;
        PlayerData randomPlayer = use15Grubs
                ? below15Grubs.get(RANDOM.nextInt(below15Grubs.size()))
                : below20Grubs.get(RANDOM.nextInt(below20Grubs.size()));

        ArrayList<Obtainable> lineArray = randomPlayer.getLine();
        Obtainable grub = use15Grubs ? grub15 : grub20;
        Obtainable leastImportantGoal = selectLeastImportantGoal(lineArray);
        lineArray.set(lineArray.indexOf(leastImportantGoal), grub);
    }

    private static int getMaximalGrubsCount(PlayerData player) {
        int max = 0;
        // there are no intermediate grub goals, don't have to recurse
        for (Obtainable goal : player.getLine()) {
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

    public static Obtainable selectMajor(ArrayList<Obtainable> elements) {
        GoalPool majors = GoalPool.poolFromStrings(elements, MAJORS);
        if (!majors.isEmpty()) {
            return majors.getElements().get(RANDOM.nextInt(majors.size()));
        }
        return null;
    }
}
