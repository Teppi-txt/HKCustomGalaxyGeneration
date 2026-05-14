package utilities;

import entities.CollectionGoal;
import entities.ObtainOption;
import entities.PlayerState;
import interface_adapters.IObtainable;
import java.util.ArrayList;
import java.util.HashMap;

enum Color {
    WHITE,
    BLACK,
    GREY
}

public class TopologicalSort {
    public static ArrayList<IObtainable> constructOrderingGraph(ArrayList<IObtainable> goals, ArrayList<IObtainable> exclusions) {
        ArrayList<IObtainable> orderingGraph = new ArrayList<>();
        for (IObtainable o : goals) {
            constructionGraphHelper(orderingGraph, o);
        }
        reduceOrderingGraph(orderingGraph, exclusions);
        return orderingGraph;
    }

    public static void reduceOrderingGraph(ArrayList<IObtainable> orderingGraph, ArrayList<IObtainable> exclusions) {
        for (IObtainable exclusion :  exclusions) {
            BoardGenerator.deepRemove(orderingGraph, exclusion);
        }
    }

    public static void constructionGraphHelper(ArrayList<IObtainable> currentGraph, IObtainable goal) {
        if (!currentGraph.contains(goal)) {
            currentGraph.add(goal);
        }

        for (ObtainOption option : goal.getDependencies()) {
            for (IObtainable child : option.getDependencies()) {
                constructionGraphHelper(currentGraph, child);
            }
        }
    }
}
