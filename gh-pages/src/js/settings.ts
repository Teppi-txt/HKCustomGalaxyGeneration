"use strict"

export type GenerationSettings = {
  seed: number;
  centerSquare: string;
  majorAbility: boolean;
  geoLimit: boolean;
  multipleSaves: boolean;
  increasedMajorChance: number;
}

export function hasCustomCenterSquare({ centerSquare }: GenerationSettings): boolean {
  return centerSquare !== "#Random";
}

export type SkipSettings = {
  darkrooms: boolean;
  settings: Array<string>;
}

type MakeSkipSettingsInput = { darkrooms: boolean; hardSkips: boolean; extremeSkips: boolean; };
export function makeSkipSettings({ darkrooms, hardSkips, extremeSkips }: MakeSkipSettingsInput): SkipSettings {
  const settings = [];
  if (hardSkips) { settings.push("hard") };
  if (extremeSkips) { settings.push("extreme") };
  return { darkrooms, settings }
}
