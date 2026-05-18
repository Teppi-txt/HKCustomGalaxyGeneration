package utilities;

import entities.*;
import interface_adapters.Obtainable;

import java.util.*;

import static entities.GenerationSettings.INCREASED_MAJOR_CHANCE;
import static utilities.GoalUtility.*;

public class GeneratorCore {
    public static final Random RANDOM = new Random();
    public static final String[] MAJORS = {
            "Monarch Wings", "Crystal Heart", "Lumafly Lantern", "Desolate Dive",
            "Dream Nail", "Dreamgate", "Abyss Shriek", "Howling Wraiths",
            "Descending Dark", "Shade Cloak", "Isma's Tear"};

    public static Board generateBoardRobin(List<Obtainable> goals, int seed, GenerationSettings generationSettings) {
        RANDOM.setSeed(seed);
        ArrayList<PlayerData> players = createPlayers(4, (ArrayList<Obtainable>) goals);
        Obtainable centerSquare = null;

        if (generationSettings.isCustomCenter()) {
            centerSquare = getGoalByName(goals, generationSettings.getCenterGoal());
        }

        for (int round = 0; round < 6; round++) {
            for (PlayerData player : players) {
                Obtainable playerGoal = selectGoal(player.getGoalPool().getElements(), generationSettings);

                // after p1 picks a goal g, p2, p3, and p4 pools cannot contain:
                // 1. any goal that is required to get g
                // 2. any goal that needs g to get
                // 3. g
                System.out.println("Added " + playerGoal.getName() + " to " + player.name);

                for (PlayerData otherPlayer : players) {
                    // after p1 picks a goal g, p2, p3, and p4 pools cannot contain:
                    // 1. any goal that is required to get g
                    // 2. any goal that needs g to get
                    // 3. g

                    // after p1 picks a goal g, p1 pool cannot contain
                    // 1. g
                    // 2. any goals that are required to get g
                    System.out.println(otherPlayer.name + ": ");

                    if (otherPlayer != player) {
                        removeAllDependents(otherPlayer.getGoalPool(), playerGoal); // any goal that needs g to get
                    } else {
                        otherPlayer.getLine().add(playerGoal);
                    }
                    otherPlayer.getGoalPool().remove(playerGoal); //g
                    removeAllDependencies(otherPlayer.getGoalPool(), playerGoal); // any goals that are required to get g
                }
            }

        }

        if (centerSquare == null) {
            GoalPool obtainableByAll = players.get(0).getGoalPool();
            for (PlayerData player : players) {
                obtainableByAll = obtainableByAll.intersection(player.getGoalPool());
            }
            centerSquare = selectGoal(obtainableByAll.getElements(), generationSettings);
        }

        return new Board(players, centerSquare);
    }

    private static Obtainable selectGoal(ArrayList<Obtainable> elements, GenerationSettings generationSettings) {
        if (generationSettings.isMajorAbilities() || RANDOM.nextDouble() < INCREASED_MAJOR_CHANCE) {
            GoalPool majors = GoalPool.poolFromStrings(elements, MAJORS);
            if (!majors.isEmpty()) {
                return majors.getElements().get(RANDOM.nextInt(majors.size()));
            }
        }
        return selectRandomGoal(elements);
    }

    /**
     * Remove all goals from newPool that are required to obtain i.
     */
    static void removeAllDependencies(GoalPool newPool, Obtainable i) {
        ArrayList<Obtainable> toBeRemoved = new ArrayList<>();

        // case 1: only one way to obtain i, so all those goals cannot be in the pool
        if (i.getDependencies().size() == 1) {
            ObtainOption singleOption = i.getDependencies().get(0);
            toBeRemoved.addAll(singleOption.getDependencies().getElements());
        }

        //case 2: multiple ways, but all those ways have one shared goal
        else if (i.getDependencies().size() > 1) {
            ObtainOption firstOption = i.getDependencies().get(0);

            for (Obtainable obtainable : firstOption.getDependencies().getElements()) {
                // loop through all the options
                boolean isInAllOptions = true;
                for (ObtainOption o2 : i.getDependencies()) {
                    if (!o2.requires(obtainable)) {
                        isInAllOptions = false;
                        break;
                    }
                }
                if (isInAllOptions) {
                    toBeRemoved.add(obtainable);
                }
            }
        }

        for (Obtainable removed : toBeRemoved) {
            //System.out.println("Removing all goals needed to obtain " + i.getName());
            removeAllDependencies(newPool, removed);
            if (!(removed instanceof Objective)) {
                newPool.remove(removed);
                System.out.println("B: - " + removed.getName());
            }
        }
    }

    /**
     * Remove all options from goals in newPool that are dependent on obtaining i.
     */
    static void removeAllDependents(GoalPool newPool, Obtainable i) {
        ArrayList<Obtainable> toBeRemoved = new ArrayList<>();

        for (Obtainable goal : newPool.getElements()) {
            Iterator<ObtainOption> iterator = goal.getDependencies().iterator();
            while (iterator.hasNext()) {
                ObtainOption option = iterator.next();
                if (option.getDependencies().contains(i)) {
                    iterator.remove();

                    System.out.println(goal.getName() + " lost the dependency " + option.toString());

                    if (goal.getDependencies().isEmpty()) {
                        toBeRemoved.add(goal);
                    }
                }
            }
        }

        for (Obtainable removed : toBeRemoved) {
            //System.out.println("Removing all goals needing " + i.getName() + " to obtain.");
            newPool.remove(removed);
            removeAllDependents(newPool, removed);
            System.out.println("F: - " + removed.getName());
        }
    }

    private static ArrayList<PlayerData> createPlayers(int count, ArrayList<Obtainable> goals) {
        ArrayList<PlayerData> players = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            PlayerData player = new PlayerData("Player " + i);
            player.setGoalPool((ArrayList<Obtainable>) deepCopyGoals(goals));
            players.add(player);
        }

        return players;
    }
}
