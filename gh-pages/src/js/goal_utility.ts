"use strict";

import { RNG } from "./random";
import { contains_obt, Obtainable, ObtainOption, PlayerData } from "./entities";
import { constructOrderingGraph } from "./topological_sort";

const DEBUG = false;

export function selectRandomGoal(playerGoals: Obtainable[], RANDOM: RNG): Obtainable {
  let goalName = "#default";
  if (DEBUG) {
    console.log("Options for the goal:");
    console.log(playerGoals.map(g => g.name));
    while (goalName !== "") {
      goalName = prompt(
        "Pick the next goal for generation (empty for random)" + (
          goalName !== "#default" ? `\nError: no goal with name ${goalName}` : ""
        ));
      if (goalName === "") {
        break;
      }
      const goal = playerGoals.find((g) => g.name === goalName);
      if (goal) {
        return goal
      }
    }
  }

  const validIndices: number[] = [];

  for (let i = 0; i < playerGoals.length; i++) {
    const goal = playerGoals[i];

    if (goal.goalKind === "Objective" || goal.goalKind === "MilestoneGoal") {
      continue;
    }

    validIndices.push(i);
  }

  const randomIndex = validIndices[RANDOM.nextInt(validIndices.length)];
  return playerGoals[randomIndex];
}

// =============================================================================
// needsMultipleSaves

// hardcoded for now
const ZOTE_ALIVE_GOALS: Array<string> = [
  "Defeat Colosseum Zote",
  "Rescue Zote in Deepnest",
  "Vengefly King + Massive Moss Charger"
];

export function needsMultipleSaves(playerGoals: Array<Obtainable>, goal: Obtainable): boolean {
  if (goal.name === "Slash Zote's corpse in Greenpath") {
    for (const i of playerGoals) {
      if (ZOTE_ALIVE_GOALS.includes(i.name)) {
        return true;
      }
    }
  }

  if (ZOTE_ALIVE_GOALS.includes(goal.name)) {
    for (const i of playerGoals) {
      if (i.name === "Slash Zote's corpse in Greenpath") {
        return true;
      }
    }
  }

  return false;
}

// =============================================================================
// selectLeastImportantGoal

function getDependentsCount(allItems: Array<Obtainable>, i: Obtainable): number {
  let count = 0;

  for (const goal of allItems) {
    if (goal.goalKind === "Objective") {
      continue;
    }

    for (const opt of goal.options) {
      if (opt.dependencies.includes(i.name)) {
        count += 1;
      }
    }
  }
  return count;
}

function getDependenciesCount(i: Obtainable): number {
  let count = 0;

  for (const opt of i.options) {
    count += opt.dependencies.length;
  }
  return count;
}


function selectLeastImportantGoal(goals: Array<Obtainable>): Obtainable {
  let result = null;
  let dNumber = 0;
  for (const currentGoal of goals) {
    const goalImportance = getDependentsCount(goals, currentGoal) + getDependenciesCount(currentGoal);
    if (currentGoal.goalKind === "Objective" || currentGoal.goalKind === "MilestoneGoal") {
      continue;
    }
    if (result === null || goalImportance < dNumber) {
      result = currentGoal;
      dNumber = goalImportance;
    }
  }
  return result;
}

// =============================================================================
// inflation
export function reduceInflation(
  players: Array<PlayerData>,
  goals: Array<Obtainable>,
  RANDOM: RNG
) {
  const geoGoals = [
    { bound: 3000, geoGoal: goals.find(g => g.name === "Spend 3000 geo") },
    { bound: 4000, geoGoal: goals.find(g => g.name === "Spend 4000 geo") },
    { bound: 5000, geoGoal: goals.find(g => g.name === "Spend 5000 geo") },
  ];

  for (const { bound, geoGoal } of geoGoals) {
  console.log(`\n=== Evaluating geo goal ${bound / 1000}k ===`);

  const below: Array<PlayerData> = [];
  let targetPlayer: PlayerData | null = null;

  for (const player of players) {
    let lineAsObject = getLine(goals, player.line)
    const geo = getMaximalSpentGeo(lineAsObject);

    console.log(
      `Player ${player.name ?? "<unknown>"}: max geo=${geo}, bound=${bound}`
    );

    if (geo < bound) {
      console.log(`  -> below bound`);
      below.push(player);
    } else if (!targetPlayer) {
      console.log(`  -> first player at/above bound, candidate target`);
      targetPlayer = player;
    } else {
      console.log(
        `  -> second player at/above bound found, geo goal is illegal`
      );
      targetPlayer = null;
      break;
    }
  }

  console.log(
    `Players below bound: ${below.length}/${players.length}`
  );

  // all players below geo bound
  if (below.length === players.length) {
    targetPlayer = below[RANDOM.nextInt(below.length)];

    console.log(
      `All players below bound; randomly selected ${
        targetPlayer.name ?? "<unknown>"
      }`
    );
  }

  // only one below geo bound
  if (!!targetPlayer) {
    console.log(
      `Target player: ${targetPlayer.name ?? "<unknown>"}`
    );

    const goalPool: Array<Obtainable> = getGoalsBeforeGeoLimit(
      goals,
      targetPlayer,
      bound
    );

    console.log(
      `Goal pool size before geo limit: ${goalPool.length}`
    );

    if (goalPool.length === 0) {
      console.log(
        `No obtainable goals before geo limit ${bound}; giving up`
      );
      return;
    }

    const lineArray: Array<Obtainable> = getLine(goals, targetPlayer.line)
    const leastImportantGoal: Obtainable =
      selectLeastImportantGoal(goalPool);

    console.log(
      `Selected least important goal: ${leastImportantGoal.name}`
    );

    targetPlayer.line[targetPlayer.line.indexOf(leastImportantGoal.name)] = geoGoal.name;

    console.log(
      `Replacing ${leastImportantGoal.name} with geo ${bound / 1000}k`
    );

    return;
  }

  console.log(`No valid target player for ${bound / 1000}k geo goal`);
}
}

function getMaximalSpentGeo(line: Array<Obtainable>): number {
  // not sure if this is correct tbh...
  const graph = constructOrderingGraph(line, []);
  let geo_spent = 0;
  for (const goal of graph) {
    if (goal.goalKind != "MilestoneGoal") {
      let geo = Math.max(...goal.options.map(opt => opt.effect.geo_spent));
      if (geo > 0) { // Math.max for empty array may return -inf
        geo_spent += geo;
      }
    } else if (goal.name == "Pay for 6 tolls") {
      geo_spent += 1000;
    }
  }
  return geo_spent;
}

function getGoalsBeforeGeoLimit(goals: Array<Obtainable>, player: PlayerData, limit: number): Array<Obtainable> {
  let currentPool = [];
  for (const goal of player.line) {
    currentPool.push(goals.find(g => g.name == goal));
    if (getMaximalSpentGeo(currentPool) > limit) {
      currentPool.pop();
      return currentPool;
    }
  }
  return currentPool;
}

// =============================================================================
// toll deposit or something idk
export function depositTolls(
  players: Array<PlayerData>,
  goals: Array<Obtainable>,
  RANDOM: RNG
) {
  const bound = 6;

  const available: Array<PlayerData> = [];
  let targetPlayer: PlayerData | null = null;
  for (const player of players) {
    const tolls = getMaximalTollCount(goals, player);

    if (tolls < bound) {
      available.push(player);
    } else if (targetPlayer === null) {
      targetPlayer = player;
    } else {
      return;
    }
  }

  // all players can have 6 tolls
  if (available.length === players.length) {
    targetPlayer = available[RANDOM.nextInt(available.length)];
  }

  let lineAsObject = getLine(goals, targetPlayer.line);
  const leastImportantGoal = selectLeastImportantGoal(lineAsObject);

  if (leastImportantGoal == null) {
    console.log(`leastImportantGoal is null`);
    return;
  }
  targetPlayer.line[targetPlayer.line.indexOf(leastImportantGoal.name)] = "Pay for 6 tolls";

  console.log(`Replacing ${leastImportantGoal.name} with 6 tolls`);

  return;
}

function getMaximalTollCount(goals: Array<Obtainable>, player: PlayerData): number {
  // TODO this is omega-borked rn, implement the toposort...
  // ArrayList < Obtainable > graph = TopologicalSort.constructOrderingGraph(new GoalPool(goals), new ArrayList<>()).getElements();
  // loop through the graph, always pick the most toll expensive option
  let lineAsObject = getLine(goals, player.line)
  const graph = constructOrderingGraph(lineAsObject, []);
  let tolls_spent = 0;
  for (const goal of graph) {
    tolls_spent += Math.max(...goal.options.map(opt => opt.effect.tolls_collected));
  }
  return tolls_spent;
}

// =============================================================================
// Grubcipitation or something idk
export function injectGrubs(
  players: Array<PlayerData>,
  goals: Array<Obtainable>,
  RANDOM: RNG
) {
  const grub15 = goals.find(g => g.name === "Save 15 grubs");
  const grub20 = goals.find(g => g.name === "Save 20 grubs");

  const below15Grubs: Array<PlayerData> = [];
  const below20Grubs: Array<PlayerData> = [];

  for (const player of players) {
    const grubbies = getMaximalGrubsCount(goals, player);

    if (grubbies < 15) {
      below15Grubs.push(player);
      below20Grubs.push(player);
    } else if (grubbies < 20) {
      below20Grubs.push(player);
    }
  }

  const use15Grubs = RANDOM.nextDouble() < 0.5;
  const randomPlayer = use15Grubs
    ? below15Grubs[RANDOM.nextInt(below15Grubs.length)]
    : below20Grubs[RANDOM.nextInt(below20Grubs.length)];

  const lineArray: Array<Obtainable> = getLine(goals, randomPlayer.line);
  const grub = use15Grubs ? grub15 : grub20;
  const leastImportantGoal = selectLeastImportantGoal(lineArray);
  randomPlayer.line[randomPlayer.line.indexOf(leastImportantGoal.name)] = grub.name;
}

function getMaximalGrubsCount(goals: Array<Obtainable>, player: PlayerData): number {
  let grubs_saved = 0;
  // there are no intermediate grub goals, don't have to recurse
  for (const goalName of player.line) {
    let goal = goals.find(g => g.name == goalName);
    grubs_saved += Math.max(...goal.options.map(opt => opt.effect.grubs_collected));
  }
  return grubs_saved;
}

export function getLine(
  goals: Obtainable[],
  line: string[]
): Obtainable[] {
  const goalMap = new Map(goals.map(g => [g.name, g]));

  return line
    .map(name => goalMap.get(name))
    .filter((g): g is Obtainable => g !== undefined);
}


export function hasUnchoosable(pool: Array<Obtainable>, goals: Array<String>) {
  //check there isnt an unchoosable option
  for (const dep of goals) {
      const depAccurate = pool.find(g => g.name === dep);
      if (depAccurate?.choosable == false) {
          return true;
      }
  }
  return false;
}

export function hasValidOption(
    pool: Obtainable[],
    options: ObtainOption[]
): boolean {

    // leaf node
    if (options.length === 0) {
        return true;
    }

    // if ANY option is fully valid, the obtainable is valid
    for (const option of options) {
        let optionValid = true;

        for (const depName of option.dependencies) {
            const dep = pool.find(g => g.name === depName);

            if (!dep || !hasValidOption(pool, dep.options)) {
                optionValid = false;
                break;
            }
        }

        if (optionValid) {
            return true;
        }
    }

    return false;
}