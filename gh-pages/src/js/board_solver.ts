import { Board } from "./make_output";
import { Obtainable, removeAllDependencies, removeAllDependents } from "./entities";
import { equal_obtainable } from "./entities";
import {getLine} from "./goal_utility";

export function isBoardValid( board: Board, goals: Obtainable[]) : boolean{
  for (const player of board.players) {

    let goalsYouCantUse : Obtainable[] = [];
    goalsYouCantUse.push(board.centerGoal);

    for (const player2 of board.players) {
      let p2Line = getLine(goals, player2.line);
      if (player2 !== player) {
        goalsYouCantUse = goalsYouCantUse.concat(p2Line);
      }
    }

    // console.log("Player " + player.name);
    // console.log(
    //     "Goals you can't use: " +
    //     goalsYouCantUse.map(g => g.name).join(", ")
    // );
    const lineValid = isLineValid(getLine(goals, player.line), goalsYouCantUse, goals);
    if (!lineValid) {
        return false;
    }
  }
  return true;
}

function isLineValid( line: Obtainable[], exclusions: Obtainable[], goals: Obtainable[]) : boolean {
  let goalPool = goals;

  for (const exclusion of exclusions) {
    goalPool = removeAllDependents(goalPool, exclusion);
  }

  //console.log(goalPool);

  let goalsAfterCurrent : Array<Obtainable> = [...line];
  let goalsUpToCurrent : Array<Obtainable> = [];
  goalsAfterCurrent.reverse();
  for (const goal of line) {
    // check goal still in pool
    if (!(goalPool.some(gl => equal_obtainable(goal, gl) && gl.choosable))) {
        console.error("issue with: " + goal.name)
        return false;
    }

    goalsUpToCurrent.push(goal);
    goalsAfterCurrent = goalsAfterCurrent.filter(g => g.name != goal.name);
    
    // check goal ordering
    let currentPool = goalPool;
    // cant get all goals before
    for (const g of goalsAfterCurrent) {
      currentPool = removeAllDependents(currentPool, g);
      currentPool = currentPool.map(gl =>
        equal_obtainable(g, gl)
          ? { ...gl, choosable: false }
          : gl
      );
    }

    for (const goalBefore of goalsUpToCurrent) {
      if (!(currentPool.some(gl => equal_obtainable(goalBefore, gl) && gl.choosable))) {
        console.error("ordering issue with: " + goalBefore.name + " blocking something in " + goalsAfterCurrent.map(g => g.name));
        return false;
      }
    }
  }

  return true;
}

