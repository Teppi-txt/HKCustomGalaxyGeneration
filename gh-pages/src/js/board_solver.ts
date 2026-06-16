import { Board } from "./make_output";
import { Obtainable, removeAllDependencies, removeAllDependents } from "./entities";
import { equal_obtainable } from "./entities";

export function isBoardValid( board: Board, goals: Obtainable[]) : boolean{
  for (const player of board.players) {

    let goalsYouCantUse : Obtainable[] = [];
    goalsYouCantUse.push(board.centerGoal);

    for (const player2 of board.players) {
      if (player2 !== player) {
        goalsYouCantUse = goalsYouCantUse.concat(player2.line);
      }
    }

    // console.log("Player " + player.name);
    // console.log(
    //     "Goals you can't use: " +
    //     goalsYouCantUse.map(g => g.name).join(", ")
    // );
    const lineValid = isLineValid(player.line, goalsYouCantUse, goals);
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
    goalPool = goalPool.filter(gl => !equal_obtainable(exclusion, gl));
  }

  //console.log(goalPool);

  for (const goal of line) {
    // check goal still in pool
    if (!goalPool.some(gl => equal_obtainable(goal, gl))) {
        console.log("issue with: " + goal.name)
        return false;
    }
    
    // check goal ordering
    // TODO:
  }

  return true;
}

