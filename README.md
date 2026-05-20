# HKCustomGalaxyGeneration

A program to generate custom Hollow Knight bingo boards for the Galaxy gamemode.

## What is Galaxy in bingo?

Galaxy is a 4-player bingo gamemode where each player is assigned a "line" and must complete the goals on that line **in order** without marking goals belonging to other players or the center square. The gamemode is a commonly played, casual format in the Hollow Knight bingo community. BingoSync, the main bingo board hosting site, doesn't support generation of valid galaxy boards so players have to reroll boards until a valid board is found.

## Example Board

<p align="center">
  <img
    width="714"
    height="667"
    alt="Example Galaxy Board"
    src="https://github.com/user-attachments/assets/ac7cd0fb-2e4f-4222-98ad-f3ad6b5fb479"
  />
</p>

## Features

Boards can be generated using a custom seed and a variety of configurable generation settings, including:

- Increased weighting for major abilities
- Forcing resource limitations (geo, tolls, etc.)
- Customizable center square
- Adjustable logic presets for skips and limitations
  - See: [Skip List](skip_list.md)
