"use strict";

import { RNG } from "./random";
import { contains_obt, Obtainable, PlayerData } from "./entities";

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
      if (contains_obt(opt.dependencies, i)) {
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
    const below: Array<PlayerData> = [];
    let targetPlayer: PlayerData | null = null;

    for (const player of players) {
      const geo = getMaximalSpentGeo(player.line);

      if (geo < bound) {
        below.push(player);
      } else if (!targetPlayer) {
        targetPlayer = player;
      } else {
        // more than one player above this bound, so this goal is illegal
        targetPlayer = null;
        break;
      }
    }

    // all players below geo bound
    if (below.length === players.length) {
      targetPlayer = below[RANDOM.nextInt(below.length)];
    }

    // only one below geo bound
    if (!!targetPlayer) {
      const goalPool: Array<Obtainable> = getGoalsBeforeGeoLimit(targetPlayer, bound);

      if (goalPool.length === 0) {
        return; // give up
      }

      const lineArray: Array<Obtainable> = targetPlayer.line;
      const leastImportantGoal: Obtainable = selectLeastImportantGoal(goalPool);
      lineArray[lineArray.indexOf(leastImportantGoal)] = geoGoal;

      console.log(`Replacing ${leastImportantGoal.name} with geo ${bound / 1000}k`);

      return;
    }
  }
}

function getMaximalSpentGeo(line: Array<Obtainable>): number {
  // TODO this is omega-borked rn, implement the toposort...
  // ArrayList < Obtainable > graph = TopologicalSort.constructOrderingGraph(new GoalPool(goals), new ArrayList<>()).getElements();
  // loop through the graph, always pick the most expensive option
  let geo_spent = 0;
  for (const goal of line) {
    geo_spent += Math.max(...goal.options.map(opt => opt.effect.geo_spent));
  }
  return geo_spent;
}

function getGoalsBeforeGeoLimit(player: PlayerData, limit: number): Array<Obtainable> {
  let currentPool = [];
  for (const goal of player.line) {
    currentPool.push(goal);
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
  const tollsGoal = goals.find(g => g.name === "Pay for 6 tolls");

  const available: Array<PlayerData> = [];
  let targetPlayer: PlayerData | null = null;
  for (const player of players) {
    const tolls = getMaximalTollCount(player);

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

  const lineArray: Array<Obtainable> = targetPlayer.line;
  const leastImportantGoal = selectLeastImportantGoal(lineArray);
  lineArray[lineArray.indexOf(leastImportantGoal)] = tollsGoal;

  console.log(`Replacing ${leastImportantGoal.name} with 6 tolls`);

  return;
}

function getMaximalTollCount(player: PlayerData): number {
  // TODO this is omega-borked rn, implement the toposort...
  // ArrayList < Obtainable > graph = TopologicalSort.constructOrderingGraph(new GoalPool(goals), new ArrayList<>()).getElements();
  // loop through the graph, always pick the most toll expensive option
  let tolls_spent = 0;
  for (const goal of player.line) {
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
    const grubbies = getMaximalGrubsCount(player);

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

  const lineArray = randomPlayer.line;
  const grub = use15Grubs ? grub15 : grub20;
  const leastImportantGoal = selectLeastImportantGoal(lineArray);
  lineArray[lineArray.indexOf(leastImportantGoal)] = grub;
}

function getMaximalGrubsCount(player: PlayerData): number {
  let grubs_saved = 0;
  // there are no intermediate grub goals, don't have to recurse
  for (const goal of player.line) {
    grubs_saved += Math.max(...goal.options.map(opt => opt.effect.grubs_collected));
  }
  return grubs_saved;
}
