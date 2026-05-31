import { bindInput, bindCheckboxInput, bindNumberInput } from "./html_parse";
import hollow_knight_goals from "../../resources/hollow_knight_goals.json";
import { generateBoardRobin } from "./generator_core";
import { generateBoardJSON } from "./make_output";
import { parseGoals } from "./goal_parser";
import {RNG} from "./random";
import { makeSkipSettings } from "./settings";

// Initialize options for center square
const inputCenterSquare = document.getElementById("inputCenterSquare");
for (const g of hollow_knight_goals) {
  if (g.type !== "Objective") {
    const newOption = document.createElement("option");
    newOption.value = g.name;
    newOption.text = g.name;
    inputCenterSquare.appendChild(newOption);
  }
}

const settings = {
  playerCount: 4,
  centerSquare: "#Random",
  seed: Math.floor(Math.random() * Math.pow(2, 48)),
  majorAbility: false,
  increasedMajorChance: 0.15,
  geoLimit: false,
  multipleSaves: false,
  darkrooms: false,
  hardSkips: false,
  extremeSkips: false,
};

function updateSettings<T>(field: string) {
  return ((v: T) => {
    // @ts-ignore
    settings[field] = v
  })
}

bindNumberInput({ id: "inputPlayerCount", setGlobal: updateSettings("playerCount"), isValid: (n) => 0 < n && n <= 4 });
bindInput<string>({ id: "inputCenterSquare", getGlobal: () => settings.centerSquare, setGlobal: updateSettings("centerSquare"), isValid: (v) => hollow_knight_goals.some((g) => g.name === v) });
bindCheckboxInput({ id: "inputMajorAbility", setGlobal: updateSettings("majorAbility") });
bindCheckboxInput({ id: "inputGeoLimit", setGlobal: updateSettings("geoLimit") });
bindCheckboxInput({ id: "inputMultipleSaves", setGlobal: updateSettings("multipleSaves") });
bindCheckboxInput({ id: "inputDarkrooms", setGlobal: updateSettings("darkrooms") });
bindCheckboxInput({ id: "inputHardSkips", setGlobal: updateSettings("hardSkips") });
bindCheckboxInput({ id: "inputExtremeSkips", setGlobal: updateSettings("extremeSkips") });
bindNumberInput({ id: "inputSeed", setGlobal: updateSettings("seed") });


const outputElement = document.getElementById("randomOrderOutput");
const outputCopyBtn = document.getElementById("copyButton");
document.getElementById("generateButton").onclick = () => {
  console.log(`Settings:`);
  console.log(settings);
  outputCopyBtn.textContent = "Copy";
  outputCopyBtn.classList.replace("btn-success", "btn-outline-secondary");
  const goals = parseGoals(hollow_knight_goals, makeSkipSettings(settings));
  const board = generateBoardRobin({goals, settings});
  const output = generateBoardJSON(board);
  console.log(output);
  outputElement.textContent = output;
};

outputCopyBtn.onclick = () => {
  navigator.clipboard.writeText(outputElement.textContent).then(() => {
    outputCopyBtn.textContent = "Copied!";
    outputCopyBtn.classList.replace("btn-outline-secondary", "btn-success");
  });
}

// Set the initial seed in the html element
// @ts-ignore
document.getElementById("inputSeed").value = settings.seed;