package utilities;

import entities.*;
import interface_adapters.Obtainable;

import java.lang.reflect.Array;
import java.util.*;

import static entities.GenerationSettings.INCREASED_MAJOR_CHANCE;
import static utilities.GeneratorPlus.*;
import static utilities.GoalUtility.*;

public class GeneratorCore {
    public static final Random RANDOM = new Random();

    public static Board generateBoardRobin(List<Obtainable> goals, int seed, GenerationSettings generationSettings) {
        RANDOM.setSeed(seed);
        ArrayList<PlayerData> players = createPlayers(4, (ArrayList<Obtainable>) goals);
        Obtainable centerSquare = null;

        if (generationSettings.useCustomCenter()) {
            centerSquare = getGoalByName(goals, generationSettings.getCenterGoal());
            for (PlayerData player : players) {
                removeAllDependents(player.getGoalPool(), centerSquare);
                player.getGoalPool().remove(centerSquare);
            }
        }

        for (int round = 0; round < 6; round++) {
            for (PlayerData player : players) {
                System.out.println("------------------------------------------------------------------");
                Obtainable playerGoal = pickGoal(generationSettings, player);
                // picks a random goal from the pool with majors and exclusions settings on

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
                    removeAllDependencies(otherPlayer.getLineAsGoalPool(), playerGoal);
                }

                for (PlayerData p1 : players) {
                    for (PlayerData p2 : players) {
                        if (p1 != p2) {
                            for (Obtainable goal : p1.getLine()) {
                                System.out.println("removing prereq of " +
                                        goal.getName() +
                                        " from " + p2.name +
                                        "'s pool because it is assigned to " +
                                        p1.name);

                                removeAllDependencies(p2.getGoalPool(), goal);
                            }
                        }
                    }
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

        injectMilestoneGoals(generationSettings, players, (ArrayList<Obtainable>) goals);

        return new Board(players, centerSquare);
    }

    public static Board generateBoardRobinDebugInput(
            List<Obtainable> goals,
            int seed,
            GenerationSettings generationSettings
    ) {
        RANDOM.setSeed(seed);

        Scanner scanner = new Scanner(System.in);
        ArrayList<PlayerData> players = createPlayers(4, (ArrayList<Obtainable>) goals);
        Obtainable centerSquare = null;

        if (generationSettings.useCustomCenter()) {
            centerSquare = getGoalByName(goals, generationSettings.getCenterGoal());

            for (PlayerData player : players) {
                removeAllDependents(player.getGoalPool(), centerSquare);
                player.getGoalPool().remove(centerSquare);
            }
        }

        for (int round = 0; round < 6; round++) {
            System.out.println("\n================ ROUND " + (round + 1) + " ================");

            for (PlayerData player : players) {
                System.out.println("\nChoosing goal for " + player.name);

                Obtainable playerGoal = promptForLegalGoal(
                        scanner,
                        goals,
                        generationSettings,
                        player
                );

                printGoal(playerGoal);

                System.out.println("Added " + playerGoal.getName() + " to " + player.name);

                for (PlayerData otherPlayer : players) {
                    if (otherPlayer != player) {
                        removeAllDependents(otherPlayer.getGoalPool(), playerGoal);
                    } else {
                        otherPlayer.getLine().add(playerGoal);
                    }

                    otherPlayer.getGoalPool().remove(playerGoal);
                    removeAllDependencies(otherPlayer.getGoalPool(), playerGoal);

                    // update the dependencies of the goals in player's lines
                    removeAllDependents(otherPlayer.getLineAsGoalPool(), playerGoal);
                }

                // if the new goal caused any of the goals in the current lines to drop to 1 dependency
                // remove those fromt he other palyer's pools
                for (PlayerData p1 : players) {
                    for (PlayerData p2 : players) {
                        if (p1 != p2) {
                            for (Obtainable goal : p1.getLine()) {
                                System.out.println("Ensuring " + goal.getName() + " from " + p1.name + " stays obtainable.");
                                removeAllDependencies(p2.getGoalPool(), goal);
                            }
                        }
                    }
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

        injectMilestoneGoals(generationSettings, players, (ArrayList<Obtainable>) goals);

        return new Board(players, centerSquare);
    }

    private static Obtainable promptForLegalGoal(
            Scanner scanner,
            List<Obtainable> allGoals,
            GenerationSettings generationSettings,
            PlayerData player
    ) {
        while (true) {
            System.out.print("Enter goal for " + player.name + ": ");
            String input = scanner.nextLine().trim();

            Obtainable chosenGoal = getGoalByName(allGoals, input);

            if (chosenGoal == null) {
                System.out.println("No goal found with name: " + input);
                continue;
            }

            if (!player.getGoalPool().contains(chosenGoal)) {
                System.out.println("Illegal choice: goal is not in " + player.name + "'s current pool.");
                continue;
            }


            return chosenGoal;
        }
    }


    private static void injectMilestoneGoals(GenerationSettings generationSettings, ArrayList<PlayerData> players,
                                                ArrayList<Obtainable> goals) {

        double geoLimitChance = (double) 3 / goals.size();

        if (generationSettings.isGeoLimits()) {
            geoLimitChance = 1;
        }

        // artificially inject geo / grub goals
        // blomsom reference
        boolean possibilityOfGeocitation = RANDOM.nextDouble() < geoLimitChance;
        boolean possibilityOfGrubcipitation = RANDOM.nextDouble() < (double) 2 / goals.size();
        boolean possibilityOfTollicitation = RANDOM.nextDouble() < (double) 1 / goals.size();

        if (possibilityOfGeocitation) {
            reduceInflation(players, getGoalByName(goals, "Spend 3000 geo"),
                    getGoalByName(goals, "Spend 4000 geo"),
                    getGoalByName(goals, "Spend 5000 geo"));
        }

        if (possibilityOfGrubcipitation) {
            injectGrubs(players, getGoalByName(goals, "Save 15 grubs"),
                    getGoalByName(goals, "Save 20 grubs"));
        }

        if (possibilityOfTollicitation) {
            depositTolls(players, getGoalByName(goals, "Pay for 6 tolls"));
        }
    }

    private static Obtainable pickGoal(GenerationSettings generationSettings, PlayerData player) {
        Obtainable playerGoal = selectGoal(player.getGoalPool().getElements(), generationSettings);

        int testLimit = 0;
        //check if its a legal goal
        while (needsMultipleSaves(player.getGoalPool().getElements(), playerGoal) && testLimit < 50) {
            playerGoal = selectGoal(player.getGoalPool().getElements(), generationSettings);
            testLimit += 1;
        }
        return playerGoal;
    }

    private static Obtainable selectGoal(ArrayList<Obtainable> elements, GenerationSettings generationSettings) {
        if (generationSettings.isMajorAbilities() && RANDOM.nextDouble() < INCREASED_MAJOR_CHANCE) {
            Obtainable major = GeneratorPlus.selectMajor(elements);
            if (major != null) {
                return major;
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

            for (Obtainable dep : singleOption.getDependencies().getElements()) {
                System.out.println(
                        "[removeAllDependencies] "
                                + dep.getName()
                                + " removed because it is a dependency of "
                                + i.getName()
                );

                toBeRemoved.add(dep);
            }
        }

        // case 2: multiple ways, but all those ways have one shared goal
        else if (i.getDependencies().size() > 1) {
            ObtainOption firstOption = i.getDependencies().get(0);

            for (Obtainable obtainable : firstOption.getDependencies().getElements()) {
                boolean isInAllOptions = true;

                for (ObtainOption o2 : i.getDependencies()) {
                    if (!o2.requires(obtainable)) {
                        isInAllOptions = false;
                        break;
                    }
                }

                if (isInAllOptions) {
                    System.out.println(
                            "[removeAllDependencies] "
                                    + obtainable.getName()
                                    + " removed because it is required by ALL obtain paths for "
                                    + i.getName()
                    );

                    toBeRemoved.add(obtainable);
                }
            }
        }

        for (Obtainable removed : toBeRemoved) {
            removeAllDependencies(newPool, removed);
            if (!(removed instanceof Objective)) {
                if (newPool.contains(removed)) {
                    System.out.println("B: - " + removed.getName());
                }
                newPool.remove(removed);

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

                    //System.out.println(goal.getName() + " lost the dependency " + option.toString());

                    if (goal.getDependencies().isEmpty()) {
                        toBeRemoved.add(goal);
                    }
                }
            }
        }

        for (Obtainable removed : toBeRemoved) {
            //System.out.println("Removing all goals needing " + i.getName() + " to obtain.");
            if (newPool.contains(removed)) {
                System.out.println("F: - " + removed.getName());
            }
            newPool.remove(removed);
            removeAllDependents(newPool, removed);
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
