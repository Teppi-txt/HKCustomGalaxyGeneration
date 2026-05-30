"use strict";

import { Obtainable,  Objective, AchievementGoal} from "./entities";

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

export function selectRandomGoal(playerGoals: Obtainable[]): Obtainable {
  const validIndices: number[] = [];

  for (let i = 0; i < playerGoals.length; i++) {
    const goal = playerGoals[i];

    if (goal.goalKind ==  "Objective" || goal.goalKind === "MilestoneGoal") {
      continue;
    }

    validIndices.push(i);
  }

  const randomIndex =
    validIndices[Math.floor(Math.random() * validIndices.length)];

  return playerGoals[randomIndex];
}
