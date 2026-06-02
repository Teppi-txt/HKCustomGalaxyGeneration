"use strict";

import { RNG } from "./random";
import { contains_obt, Obtainable, PlayerData, removeAllDependencies, removeAllDependents } from "./entities";

// public static GoalPool constructOrderingGraph(GoalPool goals, ArrayList<Obtainable> exclusions) {
//         GoalPool orderingGraph = new GoalPool();
//         for (Obtainable o : goals.getElements()) {
//             constructionGraphHelper(orderingGraph, o);
//         }
//         reduceOrderingGraph(orderingGraph, exclusions);
//         return orderingGraph;
//     }

//     public static void reduceOrderingGraph(GoalPool orderingGraph, ArrayList<Obtainable> exclusions) {
//         for (Obtainable exclusion :  exclusions) {
//             removeAllDependencies(orderingGraph, exclusion);
//             removeAllDependents(orderingGraph, exclusion);
//         }
//     }

//     public static void constructionGraphHelper(GoalPool currentGraph, Obtainable goal) {
//         if (!currentGraph.contains(goal)) {
//             currentGraph.add(goal);
//         }

//         for (ObtainOption option : goal.getDependencies()) {
//             for (Obtainable child : option.getDependencies().getElements()) {
//                 constructionGraphHelper(currentGraph, child);
//             }
//         }
//     }

export function constructOrderingGraph(goals: Array<Obtainable>, exclusions: Array<Obtainable>) : Array<Obtainable> {
    let currentPool: Array<Obtainable> = Array();
    for (const obtainable of goals) {
        constructionGraphHelper(currentPool, obtainable);
    }

    for (const exclusion of exclusions) {
        currentPool = removeAllDependencies(currentPool, exclusion);
        currentPool = removeAllDependents(currentPool, exclusion);   
    }
    return currentPool;
}

export function constructionGraphHelper(currentGraph: Array<Obtainable>, goal: Obtainable) {
    if (!currentGraph.includes(goal)) {
        currentGraph.push(goal);
    }

    for (const obtainOption of goal.options) {
        for (const child of obtainOption.dependencies) {
            constructionGraphHelper(currentGraph, child);
        }
    }
}