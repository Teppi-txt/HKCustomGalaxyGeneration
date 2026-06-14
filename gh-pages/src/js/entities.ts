"use strict"

export type PlayerData = {
  line: Array<Obtainable>;
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
  dependencies: Array<Obtainable>;
  /** Effect on the player state to execute this option */
  effect: PlayerStateEffect;
};

export interface Obtainable {
  name: string;
  /** Array of possible ways to obtain this goal */
  options: Array<ObtainOption>;
  goalKind: string;
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

function requires_opt_direct(opt: ObtainOption, dep: Obtainable): boolean {
  return opt.dependencies.some(o_dep => equal_obtainable(o_dep, dep));
}


/**
 * Remove all options of goals that are dependent on obtaining i.
 * 
 * Returns the new pool.
 */
export function removeAllDependents(pool: Array<Obtainable>, i: Obtainable): Array<Obtainable> {
  const toBeRemoved = Array();
  for (const goal of pool) {
    // placeholder until collectionGoal logic gets added
    if (goal.goalKind == "CollectionGoal") {
      continue;
    }

    const newGoal: Obtainable = {
      name: goal.name,
      options: goal.options.filter(opt => !requires_opt_direct(opt, i)),
      goalKind: "unknown",
    };
    if (newGoal.options.length === 0) {
      toBeRemoved.push(newGoal)
    }
  }
  let newPool = pool.filter(g => !contains_obt(toBeRemoved, g));
  for (const toRemove of toBeRemoved) {
    newPool = removeAllDependents(newPool, toRemove);
  }
  return newPool;
}

/**
 * Remove all goals from newPool that are required to obtain i.
 */
export function removeAllDependencies(pool: Array<Obtainable>, i: Obtainable): Array<Obtainable> {
  const toBeRemoved: Array<Obtainable> = Array();

  // case 1: only one way to obtain i, so all those goals cannot be in the pool
  if (i.options.length == 1) {
    const singleOption = i.options[0];
    toBeRemoved.push(...singleOption.dependencies);
  }
  // case 2: multiple ways, but all those ways have one shared goal
  else if (i.options.length > 1) {
    const firstOption = i.options[0];

    for (const obtainable of firstOption.dependencies) {
      // loop through all the options
      let isInAllOptions = true;
      for (const o2 of i.options) {
        if (!requires_opt_direct(o2, obtainable)) {
          isInAllOptions = false;
          break;
        }
      }
      if (isInAllOptions) {
        toBeRemoved.push(obtainable);
      }
    }
  }

  let newPool = pool.filter(g => !contains_obt(toBeRemoved, g));
  for (const toRemove of toBeRemoved) {
    newPool = removeAllDependencies(newPool, toRemove);
  }
  return newPool;
}
