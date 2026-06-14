"use strict";

import { Obtainable, PlayerData } from "./entities";

export type Board = {
  players: Array<PlayerData>;
  centerGoal: Obtainable
};

export const GLACKY_INDICES = [
  [0, 1, 2, 3, 8, 13],
  [4, 9, 14, 19, 18, 17],
  [24, 23, 22, 21, 16, 11],
  [20, 15, 10, 5, 6, 7],
];
const GLACKY_INDEX_CENTER = 12;


export function generateBoardJSON(board: Board): string {
  if (board.players.length === 4) {
    return generate4PBoard(board);
  }
  return generateRGOBoard(board);
}

export function generateBoardArray(board: Board): string[] {
  const boardArray: string[] = new Array(25);

  for (const playerCount in board.players) {
    const line = board.players[playerCount].line;
    console.log(line);
    for (const i in line) {
      boardArray[GLACKY_INDICES[playerCount][i]] = line[i].name;
    }
  }
  boardArray[GLACKY_INDEX_CENTER] = board.centerGoal.name;

  return boardArray;
}

function generate4PBoard(board: Board): string {
  const boardArray: Obtainable[] = new Array(25);

  for (const playerCount in board.players) {
    const line = board.players[playerCount].line;
    console.log(line);
    for (const i in line) {
      boardArray[GLACKY_INDICES[playerCount][i]] = line[i];
    }
  }
  boardArray[GLACKY_INDEX_CENTER] = board.centerGoal;

  const output = "[\n  " + boardArray.map(
    boardGoal => `  { "name" : "${boardGoal.name}" }`
  ).join(",\n  ") + "\n]";
  return output;
}

function generateRGOBoard(board: Board): string {
  let output = [];
  for (let i = 0; i < 25; i++) {
    output.push(`{ "name" : "${board.players[0].line[i].name}" }`);
  }
  return "[" + output.join(", \n") + "}";
}
