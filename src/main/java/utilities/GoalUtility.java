package utilities;

import entities.*;
import interface_adapters.IObtainable;

import java.util.*;

import static utilities.BoardGenerator.RANDOM;

public class GoalUtility {
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

    public static IObtainable selectRandomGoal(List<IObtainable> allItems) {
        List<Integer> validIndices = new ArrayList<>();

        for (int i = 0; i < allItems.size(); i++) {
            if (!(allItems.get(i) instanceof Objective) && !(allItems.get(i) instanceof MilestoneGoal)) {
                validIndices.add(i);
            }
        }

        return allItems.get(validIndices.get(RANDOM.nextInt(validIndices.size())));
    }

    public static IObtainable selectMostImportantGoal(List<IObtainable> allItems) {
        IObtainable result = null;
        int dNumber = 0;
        for (int i = 0; i < allItems.size(); i++) {
            IObtainable currentGoal = allItems.get(i);
            int goalImportance = getDependentsCount(allItems, currentGoal) + getDependenciesCount(currentGoal);
            if (currentGoal instanceof Objective) {
                continue;
            }
            if (result == null || goalImportance > dNumber) {
                result = currentGoal;
                dNumber = goalImportance;
            }
        }
        return result;
    }

    public static IObtainable selectLeastImportantGoal(List<IObtainable> allItems) {
        IObtainable result = null;
        int dNumber = 0;
        for (int i = 0; i < allItems.size(); i++) {
            IObtainable currentGoal = allItems.get(i);
            int goalImportance = getDependentsCount(allItems, currentGoal) + getDependenciesCount(currentGoal);
            if (currentGoal instanceof Objective) {
                continue;
            }
            if (result == null || goalImportance < dNumber) {
                result = currentGoal;
                dNumber = goalImportance;
            }
        }
        return result;
    }

    private static int getDependentsCount(List<IObtainable> allItems, IObtainable i) {
        int count = 0;

        for (IObtainable goal : allItems) {
            if (goal instanceof Objective) {
                continue;
            }

            Iterator<ObtainOption> iterator = goal.getDependencies().iterator();

            while (iterator.hasNext()) {

                ObtainOption option = iterator.next();

                if (option.getDependencies().contains(i)) {
                    count += 1;
                }
            }
        }
        return count;
    }

    private static int getDependenciesCount(IObtainable i) {
        int count = 0;

        for (ObtainOption o : i.getDependencies()) {
            count += o.getDependencies().size();
        }
        return count;
    }

    public static List<IObtainable> deepCopyGoals(List<IObtainable> goals) {
        Map<String, IObtainable> byName = new HashMap<>();
        ArrayList<IObtainable> result = new ArrayList<>();

        // first pass, just the new goal objects
        for (IObtainable goal : goals) {
            IObtainable newGoal;

            if (goal instanceof CollectionGoal collectionGoal) {
                newGoal = new CollectionGoal(
                        collectionGoal.getName(),
                        new ArrayList<>(),
                        collectionGoal.getCollectionCount()
                );
            } else if (goal instanceof Objective) {
                newGoal = new Objective(
                        goal.getName(),
                        new ArrayList<>()
                );
            } else {
                newGoal = new AchievementGoal(
                        goal.getName(),
                        new ArrayList<>()
                );
            }

            byName.put(goal.getName(), newGoal);
            result.add(newGoal);
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

    public static IObtainable getGoalByName(List<IObtainable> goals, String s) {
        for (IObtainable goal : goals) {
            if (goal.getName().equals(s)) {
                return goal;
            }
        }
        return null;
    }
}
