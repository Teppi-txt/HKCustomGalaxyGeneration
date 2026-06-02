"use strict";

import { AchievementGoal, CollectionObtainable, MilestoneGoal, Objective, Obtainable, PlayerStateEffect } from "./entities";
import { SkipSettings } from "./settings";

type Option = {
  dependencies: Array<string>;
  effect?: {
    geo?: number;
    simple_keys?: number;
    pale_ore?: number;
    grubs_saved?: number;
    tolls?: number;
    lifeblood_masks?: number;
  };
  notes?: string;
};

type JSONGoal = {
  name: string;
  type: string;
  options?: Array<Option | string>;
};

export function parseGoals(goals: Array<JSONGoal>, settings: SkipSettings): Array<Obtainable> {
  const byName: Record<string, Obtainable> = {};
  const result: Array<Obtainable> = Array();

  const getOrCreate = (name: string) => {
    if (!(name in byName)) {
      byName[name] = { name, options: [], goalKind: "unknown" }
    }
    return byName[name]
  }


  // First pass: create empty objects
  for (const element of goals) {
    let newGoal: Obtainable = getOrCreate(element.name);
    const type: string = element.type;

    if (type === "CollectionGoal") {
      if ("count" in element) {
        // @ts-expect-error
        let collectionGoal: CollectionObtainable = newGoal;
        collectionGoal.goalKind = "CollectionObtainable";
        collectionGoal.count = element.count as number;
        collectionGoal.collectionItems = (element.options as string[]).map(getOrCreate);
        newGoal = collectionGoal;
      }
      else {
        alert(`error parsing goal ${element.name} of kind CollectionGoal. Everything will break now`);
      }
    } else if (type === "MilestoneGoal") {
      if ("amount" in element && "objective" in element) {
        // @ts-expect-error
        let milestoneGoal: MilestoneGoal = newGoal;
        milestoneGoal.goalKind = "MilestoneGoal";
        milestoneGoal.milestoneType = element.objective as string;
        milestoneGoal.milestone = element.amount as number;
        newGoal = milestoneGoal;
      }
      else {
        alert(`error parsing goal ${element.name} of kind MilestoneGoal. Everything will break now`);
      }
    } else if (type === "Objective") {
      (newGoal as Objective).goalKind = "Objective";
    } else if (type === "AchievementGoal") {
      (newGoal as AchievementGoal).goalKind = "AchievementGoal";
    } else {
      alert(`Unknown type of goal ${type} for goal ${element.name}. Everything will break now`);
    }
    // fill dependencies/options for Objective and AchievementGoal
    if ((newGoal.goalKind === "Objective" || newGoal.goalKind === "AchievementGoal")
      && "options" in element) {
      const options = newGoal.options;

      for (const optionObj of element.options) {
        const option = (optionObj as Option);
        const dependencies = [];
        if (!!option.notes && !settings.settings.includes(option.notes)) {
          continue; //dont process
        }

        if ("dependencies" in option) {
          for (const depElement of option.dependencies) {
            const depName = depElement;
            const dependency = getOrCreate(depName);
            dependencies.push(dependency);
          }
        }
        const effect: PlayerStateEffect = parseEffect(option.effect);
        options.push({ dependencies, effect });
      }
    }
    result.push(newGoal);
  }

  if (settings.darkrooms) {
    console.log("darkrooms on");
    removeLantern(result);
  }
  return result;
}

const LANTERN_REQUIRED = ["No Eyes", "Peaks Access"];

function removeLantern(result: Array<Obtainable>): Array<Obtainable> {
  for (const l of result) {
    if (LANTERN_REQUIRED.includes(l.name)) {
      continue;
    }

    for (const option of l.options) {
      // In-place removal of dependency
      const idx = option.dependencies.findIndex(dep => dep.name === "Lumafly Lantern");
      if (idx > -1) {
        option.dependencies.splice(idx, 1);
      }
    }
  }
  return result
}

function parseEffect(effectInput: Option["effect"] | null | undefined): PlayerStateEffect {
  const effect = {
    grubs_collected: 0,
    geo_spent: 0,
    essence_collected: 0,
    tolls_collected: 0
  };
  if (!!effectInput) {
    effect.grubs_collected = effectInput.grubs_saved ?? 0;
    effect.geo_spent = Math.abs(effectInput.geo ?? 0);
    // effect.essence_collected = effectInput.essence ?? 0;
    effect.tolls_collected = effectInput.tolls ?? 0;
  }
  return effect;
}
