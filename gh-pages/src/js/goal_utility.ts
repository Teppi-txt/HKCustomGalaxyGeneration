"use strict";

import { Obtainable } from "./entities";

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
