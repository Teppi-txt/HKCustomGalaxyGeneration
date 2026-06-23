"use strict";

import { RNG } from "./random";
import { contains_obt, Obtainable, PlayerData, removeAllDependencies, removeAllDependents } from "./entities";

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
            //constructionGraphHelper(currentGraph, child);
        }
    }
}