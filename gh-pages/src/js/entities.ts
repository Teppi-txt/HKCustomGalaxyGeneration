"use strict"

import { hasUnchoosable, hasValidOption } from "./goal_utility";

export type PlayerData = {
    line: Array<string>;
    goalPool: Array<Obtainable>;
    name: string;
}

export type PlayerState = {
    grubs_collected: number;
    geo_spent: number;
    essence_collected: number;
    tolls_collected: number;
    objectives: Array<Objective>;
    all_obtained: Array<Obtainable>;
}

export type PlayerStateEffect = {
    grubs_collected: number;
    geo_spent: number;
    essence_collected: number;
    tolls_collected: number;
}

export type ObtainOption = {
    /** Array of stuff needed to execute this option */
    dependencies: Array<String>;
    /** Effect on the player state to execute this option */
    effect: PlayerStateEffect;
};

export interface Obtainable {
    name: string;
    /** Array of possible ways to obtain this goal */
    options: Array<ObtainOption>;
    goalKind: string;
    choosable: boolean;
}

export type CollectionGoal = Obtainable & {
    goalKind: "CollectionGoal";
    count: number;
    collectionItems: Array<Obtainable>;
}
export type Objective = Obtainable & { goalKind: "Objective"; }
export type AchievementGoal = Obtainable & { goalKind: "AchievementGoal"; }
export type MilestoneGoal = Obtainable & {
    goalKind: "MilestoneGoal";
    milestoneType: string;
    milestone: number;
}

export function equal_obtainable(a: Obtainable, b: Obtainable): boolean {
    return a.name === b.name;
}

export function contains_obt(pool: Array<Obtainable>, goal: Obtainable): boolean {
    return pool.some(g => equal_obtainable(g, goal))
}

export function contains_obt_named(pool: Array<Obtainable>, goalName: String): boolean {
    return pool.some(g => g.name === goalName);
}

function requires_opt_direct(opt: ObtainOption, dep: Obtainable): boolean {
    return opt.dependencies.some(o_dep => o_dep == dep.name);
}


/**
 * Remove all options of goals that are dependent on obtaining i. 
 * If a goal runs out of options, mark it as unchoosable.
 * 
 * Returns the new pool.
 */
export function removeAllDependents(pool: Obtainable[], target: Obtainable): Obtainable[] {
    const removed: Obtainable[] = [];

    let newPool = pool.map(goal => {
        if (goal.goalKind === "CollectionGoal") {
            return goal;
        }

        const options = goal.options.filter(
            opt => !requires_opt_direct(opt, target)
        );
        const unchoosable = goal.options.length > 0 && options.length === 0;

        const newGoal: Obtainable = {
            ...goal,
            options,
            choosable: unchoosable ? false : goal.choosable,
        };

        if (unchoosable) {
            removed.push(newGoal);
        }

        return newGoal;
    });

    for (const goal of removed) {
        newPool = removeAllDependents(newPool, goal);
    }

    return newPool;
}

/**
 * Mark all goals from newPool that are required to obtain i as unchoosable.
 */
export function removeAllDependencies(
    pool: Array<Obtainable>,
    i: Obtainable
): Array<Obtainable> {

    const toBeRemoved = getStrictDependencies(pool, i);
    
    return pool.map(g => {
        if (contains_obt(toBeRemoved, g)) {
            return { ...g, choosable: false };
        }
        return g;
    });
}

export function getStrictDependencies(pool: Obtainable[], i: Obtainable): Obtainable[] {
    const result: Obtainable[] = [];

    if (i.options.length === 1) {
        const singleOption = i.options[0];

        // if (hasUnchoosable(pool, i.options[0].dependencies)) {
        //     console.error("cannot obtain: " + i.name);
        //     return [];
        // }

        for (const dep of singleOption.dependencies) {
            const depObject = pool.find(g => g.name === dep);

            if (!contains_obt(result, depObject)) {
                result.push(depObject);
            }

            for (const transitive of getStrictDependencies(pool, depObject)) {
                if (!contains_obt(result, transitive)) {
                    result.push(transitive);
                }
            }
        }
    }

    else if (i.options.length > 1) {
        let commonInAll: Obtainable[] = [...pool];
        let validOptionCount = 0;

        for (const option of i.options) {
            const commonInOption: Obtainable[] = [];

            if (hasUnchoosable(pool, option.dependencies)) {
                continue;
            }

            validOptionCount++;

            for (const dep of option.dependencies) {
                const depAccurate = pool.find(g => g.name === dep);
                if (!depAccurate) continue;

                if (!contains_obt(commonInOption, depAccurate)) {
                    commonInOption.push(depAccurate);
                }

                for (const transitive of getStrictDependencies(pool, depAccurate)) {
                    if (!contains_obt(commonInOption, transitive)) {
                        commonInOption.push(transitive);
                    }
                }
            }

            commonInAll = commonInAll.filter(obt =>
                contains_obt(commonInOption, obt)
            );
        }

        if (validOptionCount === 0) {
            // console.error(
            //     `All options for "${i.name}" contain an unchoosable dependency`
            // );
        } else {
            result.push(...commonInAll);
        }
    }
    return result;
}