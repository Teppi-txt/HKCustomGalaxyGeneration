"use strict"

import { contains_obt, equal_obtainable, Obtainable, PlayerData, removeAllDependencies, removeAllDependents } from "./entities";
import { GenerationSettings, hasCustomCenterSquare } from "./settings";
import { depositTolls, injectGrubs, needsMultipleSaves, reduceInflation, selectRandomGoal } from "./goal_utility";
import { Board } from "./make_output";
import { RNG } from "./random";

const MAJORS: string[] = [
  "Monarch Wings", "Crystal Heart", "Lumafly Lantern", "Desolate Dive",
  "Dream Nail", "Descending Dark", "Shade Cloak", "Isma's Tear", "Abyss Shriek"];

const RANDOM: RNG = new RNG();


function selectGoal(pool: Array<Obtainable>, settings: GenerationSettings): Obtainable {
  if (settings.majorAbility && RANDOM.nextDouble() < settings.increasedMajorChance) {
    const all_majors = pool.filter(g => MAJORS.includes(g.name));
    if (all_majors.length > 0) {
      return all_majors[RANDOM.nextInt(all_majors.length)];
    }
  }
  return selectRandomGoal(pool, RANDOM);
}

function createPlayers(count: number, goals: Array<Obtainable>): Array<PlayerData> {
  const players = Array.from({ length: count }, (_, i) => {
    return { name: `Player ${i}`, goalPool: [...goals], line: [] } as PlayerData
  });
  return players;
}


function pickGoal(settings: GenerationSettings, player: PlayerData): Obtainable {
  let playerGoal: Obtainable = selectGoal(player.goalPool, settings);
  let testLimit: number = 0;
  //check if its a legal goal
  while (!settings.multipleSaves
    && needsMultipleSaves(player.goalPool, playerGoal)
    && testLimit < 50) {
    playerGoal = selectGoal(player.goalPool, settings);
    testLimit += 1;
  }
  if (testLimit === 50) {
    alert("generation failed");
  }
  return playerGoal;
}


export function generateBoardRobin(
  { goals, settings }: { goals: Array<Obtainable>; settings: GenerationSettings }
): Board {
  const players = createPlayers(4, goals);
  let centerSquare: Obtainable | null = null;
  RANDOM.setSeed(settings.seed);

  // If we use custom center square, remove it from each player's pool
  if (hasCustomCenterSquare(settings)) {
    centerSquare = goals.find((g) => g.name === settings.centerSquare);
    players.forEach(p =>
      p.goalPool = removeAllDependents(p.goalPool, centerSquare)
        .filter(g => !equal_obtainable(g, centerSquare))
    );
  }

  // Main iteration loop: pick goals for each player, one at a time, keeping
  // other pools updated
  for (let round = 0; round < 6; round++) {
    console.log("Round " + round + "");
    for (const player of players) {
      const playerGoal: Obtainable = pickGoal(settings, player);
      // picks a random goal from the pool with majors and exclusions settings on

      // after p1 picks a goal g, p2, p3, and p4 pools cannot contain:
      // 1. any goal that is required to get g
      // 2. any goal that needs g to get
      // 3. g

      for (const otherPlayer of players) {
        const originalPool = otherPlayer.goalPool;

        // after p1 picks a goal g, p2, p3, and p4 pools cannot contain:
        // 1. any goal that is required to get g
        // 2. any goal that needs g to get
        // 3. g

        // after p1 picks a goal g, p1 pool cannot contain
        // 1. g
        // 2. any goals that are required to get g
        let newPool = otherPlayer.goalPool;

        // g
        const beforeRemoveGoal = newPool;
        newPool = newPool.filter(gl => !equal_obtainable(playerGoal, gl));

        if (otherPlayer != player) {
          // any goal that needs g to get
          const beforeDependents = newPool;
          newPool = removeAllDependents(newPool, playerGoal);

        } else {
          otherPlayer.line.push(playerGoal);
          console.log(`Added ${playerGoal.name} to ${player.name}`);
        }

        // any goals that are required to get g
        const beforeDependencies = newPool;
        newPool = removeAllDependencies(newPool, playerGoal);
        otherPlayer.goalPool = newPool;
        
        // also update the obtain options of all goals in each player's lines
        otherPlayer.line = removeAllDependents(otherPlayer.line, playerGoal);
      }
    }
  }

  if (centerSquare === null) {
    let obtainableByAll = players[0].goalPool;
    for (const player of players) {
      obtainableByAll = obtainableByAll.filter(g => contains_obt(player.goalPool, g));
    }
    centerSquare = selectGoal(obtainableByAll, settings);
  }

  // Note that this modifies players in-place
  injectMilestoneGoals(settings, players, goals);

  return { players, centerGoal: centerSquare };
}

function injectMilestoneGoals(
  settings: GenerationSettings,
  players: Array<PlayerData>,
  goals: Array<Obtainable>
) {
  const geoLimitChance = settings.geoLimit ? 1 : (3 / goals.length);
  const tollLimitChance = settings.tollLimit ? 1 : (2 / goals.length);
  const grubLimitChance = settings.grubLimit ? 1 : (1 / goals.length);

  // artificially inject geo / grub goals
  // blomsom reference
  const possibilityOfGeocitation = RANDOM.nextDouble() < geoLimitChance;
  const possibilityOfGrubcipitation = RANDOM.nextDouble() < grubLimitChance;
  const possibilityOfTollicitation = RANDOM.nextDouble() < tollLimitChance;

  if (possibilityOfTollicitation) {
    depositTolls(players, goals, RANDOM);
  }

  if (possibilityOfGeocitation) {
    reduceInflation(players, goals, RANDOM);
  }

  if (possibilityOfGrubcipitation) {
    injectGrubs(players, goals, RANDOM);
  }
}
