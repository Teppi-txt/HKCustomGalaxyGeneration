package utilities;

import entities.GoalPool;
import entities.ObtainOption;
import interface_adapters.Obtainable;
import java.util.ArrayList;

import static utilities.GeneratorCore.removeAllDependencies;
import static utilities.GeneratorCore.removeAllDependents;

enum Color {
    WHITE,
    BLACK,
    GREY
}

public class TopologicalSort {
    public static GoalPool constructOrderingGraph(GoalPool goals, ArrayList<Obtainable> exclusions) {
        GoalPool orderingGraph = new GoalPool();
        for (Obtainable o : goals.getElements()) {
            constructionGraphHelper(orderingGraph, o);
        }
        reduceOrderingGraph(orderingGraph, exclusions);
        return orderingGraph;
    }

    public static void reduceOrderingGraph(GoalPool orderingGraph, ArrayList<Obtainable> exclusions) {
        for (Obtainable exclusion :  exclusions) {
            removeAllDependencies(orderingGraph, exclusion);
            removeAllDependents(orderingGraph, exclusion);
        }
    }

    public static void constructionGraphHelper(GoalPool currentGraph, Obtainable goal) {
        if (!currentGraph.contains(goal)) {
            currentGraph.add(goal);
        }

        for (ObtainOption option : goal.getDependencies()) {
            for (Obtainable child : option.getDependencies().getElements()) {
                constructionGraphHelper(currentGraph, child);
            }
        }
    }
}
