package utilities;

import entities.Objective;
import entities.ObtainOption;
import interface_adapters.IObtainable;

import java.util.*;

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
            if (!(allItems.get(i) instanceof Objective)) {
                validIndices.add(i);
            }
        }

        Collections.shuffle(validIndices);
        return allItems.get(validIndices.get(0));
    }

    public static IObtainable selectMostImportantGoal(List<IObtainable> allItems) {
        IObtainable result = null;
        int d_number = 0;
        for (int i = 0; i < allItems.size(); i++) {
            if (allItems.get(i) instanceof Objective) {
                continue;
            }
            if (result == null) {
                result = allItems.get(i);
                d_number = getDependentsCount(allItems, allItems.get(i));
            } else if (getDependentsCount(allItems, allItems.get(i)) > d_number) {
                result = allItems.get(i);
                d_number = getDependentsCount(allItems, allItems.get(i));
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
}
