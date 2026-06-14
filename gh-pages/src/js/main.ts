import hollow_knight_goals from "../../../src/main/resources/hollow_knight_goals.json";
import { bindInput, bindCheckboxInput, bindNumberInput } from "./html_parse";
import { generateBoardRobin } from "./generator_core";
import { generateBoardJSON , generateBoardArray, GLACKY_INDICES} from "./make_output";
import { parseGoals } from "./goal_parser";
import { makeSkipSettings } from "./settings";
import packagejson from "../../package.json";

const all_goals = hollow_knight_goals.goals;

// Initialize options for center square
const inputCenterSquare = document.getElementById("inputCenterSquare");
for (const g of all_goals) {
  if (g.type !== "Objective") {
    const newOption = document.createElement("option");
    newOption.value = g.name;
    newOption.text = g.name;
    inputCenterSquare.appendChild(newOption);
  }
}

const settings = {
  seed: Math.floor(Math.random() * Math.pow(2, 48)),
  playerCount: 4,
  centerSquare: "#Random",
  majorAbility: false,
  increasedMajorChance: 0.15,
  geoLimit: false,
  tollLimit: false,
  grubLimit: false,
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

bindInput<string>({
  id: "inputCenterSquare",
  setGlobal: updateSettings("centerSquare"),
  isValid: (v) => all_goals.some((g) => g.name === v)
});
bindNumberInput({ id: "inputPlayerCount", setGlobal: updateSettings("playerCount"), isValid: (n) => 0 < n && n <= 4 });
bindCheckboxInput({ id: "inputMajorAbility", setGlobal: updateSettings("majorAbility") });
bindCheckboxInput({ id: "inputGeoLimit", setGlobal: updateSettings("geoLimit") });
bindCheckboxInput({ id: "inputTollLimit", setGlobal: updateSettings("tollLimit") });
bindCheckboxInput({ id: "inputGrubLimit", setGlobal: updateSettings("grubLimit") });
bindCheckboxInput({ id: "inputMultipleSaves", setGlobal: updateSettings("multipleSaves") });
bindCheckboxInput({ id: "inputDarkrooms", setGlobal: updateSettings("darkrooms") });
bindCheckboxInput({ id: "inputHardSkips", setGlobal: updateSettings("hardSkips") });
bindCheckboxInput({ id: "inputExtremeSkips", setGlobal: updateSettings("extremeSkips") });
bindInput<number>({
  id: "inputSeed",
  setGlobal: updateSettings("seed"),
  convert: (v: string) => {
    const num = Number(v);
    if (Number.isNaN(num)) {
      return Array.from(v)
        .reduce((s, c) => Math.imul(31, s) + c.charCodeAt(0) | 0, 0);
    }
    else {
      return num;
    }
  }
});
;

let output : string[] = [];
const outputElement = document.getElementById("randomOrderOutput");
const outputCopyBtn = document.getElementById("copyButton");
document.getElementById("generateButton").onclick = () => {
  console.log(`Settings:`);
  console.log(settings);
  outputCopyBtn.textContent = "Copy";
  outputCopyBtn.classList.replace("btn-success", "btn-outline-secondary");
  const goals = parseGoals(all_goals, makeSkipSettings(settings));

  const board = generateBoardRobin({ goals, settings });
  output = generateBoardArray(board);
  showBoardBtn.disabled = false;
  outputElement.textContent = generateBoardJSON(board);
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

// Print the version at the bottom
document.getElementById(
  "versionDisplay"
).textContent = `Version ${packagejson.version}`;

const showBoardBtn = document.getElementById("showBoardButton") as HTMLButtonElement;
showBoardBtn.disabled = true;

const boardModal = document.getElementById("boardModal") as HTMLDivElement;
const boardGrid = document.getElementById("boardGrid") as HTMLDivElement;
const boardCloseBtn = boardModal.querySelector(".btn-close") as HTMLButtonElement;

function openBoardModal() {
  boardModal.classList.add("show");
  boardModal.style.display = "block";
  document.body.classList.add("modal-open");
}

function closeBoardModal() {
  boardModal.classList.remove("show");
  boardModal.style.display = "none";
  document.body.classList.remove("modal-open");
}

showBoardBtn.onclick = () => {
  renderBoard(output);
  openBoardModal();
};

boardCloseBtn.onclick = closeBoardModal;

boardModal.addEventListener("click", (e) => {
  if (e.target === boardModal) {
    closeBoardModal();
  }
});

const colorByIndex = new Map<number, number>();

GLACKY_INDICES.forEach((group, color) => {
  group.forEach((index) => {
    colorByIndex.set(index, color + 1);
  });
});

function renderBoard(board: any[]) {
  boardGrid.innerHTML = "";

  board.forEach((goal, index) => {
    const cell = document.createElement("div");
    cell.classList.add("board-cell");

    if (index === 12) {
      cell.classList.add("center");
    }

    const color = colorByIndex.get(index);
    if (color !== undefined) {
      cell.classList.add(`tile-p${color}`);
    }

    cell.textContent = goal;
    boardGrid.appendChild(cell);
  });
}