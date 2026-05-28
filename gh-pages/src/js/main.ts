import { bindInput, bindCheckboxInput } from "./input_parse";
// import { getRandomOrder } from "./randomize";
// import { makeTextOrder, outputOrder } from "./make_output";
import hollow_knight_goals from "../../resources/hollow_knight_goals.json";
// import test_set from "../../resources/test_set.json";

const goals = hollow_knight_goals.goals;

// Initialize options for center square
const inputCenterSquare = document.getElementById("inputCenterSquare");
for (const g of goals) {
  const newOption = document.createElement("option");
  newOption.value = g.name;
  newOption.text = g.name;
  inputCenterSquare.appendChild(newOption);
}

let settings = {
  seed: 0,
  playerCount: 4,
  centerSquare: "random",
  majorAbility: false,
  geoLimit: false,
  noMultipleSaves: true,
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

bindInput<number>({ id: "inputSeed", getGlobal: () => settings.seed, setGlobal: updateSettings("seed") });
bindInput<number>({ id: "inputPlayerCount", getGlobal: () => settings.playerCount, setGlobal: updateSettings("playerCount") });
bindInput<string>({ id: "inputCenterSquare", getGlobal: () => settings.centerSquare, setGlobal: updateSettings("centerSquare"), isValid: (v) => goals.some((g) => g.name === v) });
bindCheckboxInput({ id: "inputMajorAbility", setGlobal: updateSettings("majorAbility") });
bindCheckboxInput({ id: "inputGeoLimit", setGlobal: updateSettings("geoLimit") });
bindCheckboxInput({ id: "inputNoMultipleSaves", setGlobal: updateSettings("noMultipleSaves") });
bindCheckboxInput({ id: "inputDarkrooms", setGlobal: updateSettings("darkrooms") });
bindCheckboxInput({ id: "inputHardSkips", setGlobal: updateSettings("hardSkips") });
bindCheckboxInput({ id: "inputExtremeSkips", setGlobal: updateSettings("extremeSkips") });


document.getElementById("generateButton").onclick = () => {
  console.log(`Settings:`);
  console.log(settings);
  // const config = { logicData: logic, itemsData: leversData };
  // const options = { difficulty, groupingFactor };
  // const order = getRandomOrder(config, options);
  // const textOrder = makeTextOrder(order, leversData);
  // outputOrder(textOrder);
};

// @ts-ignore
document.getElementById("settingsForm").reset();
