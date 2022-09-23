# Josm Magic Wand

Plugin created for the [JOSM](https://josm.openstreetmap.de/), allows you to select areas to label using a range of
colors, it is also possible to add areas and subtract selected areas.

![Peek 2022-09-22 16-27](https://user-images.githubusercontent.com/12978932/191855039-685b9420-0e55-4883-a466-4ff85d860bac.gif)

## Usage

Josm Magic Wand plugin It has two modes of execution: generate and merge geometries.

### Generate geometries.

To generate geometries, you must have a base map activated and a data layer.

- You can press the shortcut `Ctrl + 1` or select directly in the mode bar (upper
  left) ![image](https://user-images.githubusercontent.com/12978932/191857775-71da462d-66fd-401f-b03a-fbf444c07b04.png),
  you will enter Magic Wand mode and you will see a wand on the
  cursor ![image](https://user-images.githubusercontent.com/12978932/191858042-942aa381-3c4b-42bf-b9df-b23782d1dce5.png)
  .
- Click on the area.
- `Ctrl + 2` to generate the geometry.

- To add or enhance the selection, you can press the `Ctrl` button and `click` on new area.
- To subtract the selection, you can press the `switch` button and `click` on area.

### Merge geometries

this functionality allows you to merge several geometries into one, the geometries must intersects.

![Peek 2022-09-22 22-44](https://user-images.githubusercontent.com/12978932/191888528-ea2105bc-7994-40e0-95bd-9d6c87c97631.gif)

- Select one or more geometries.
- Press `Ctrl + 3`.

## Requirements

- Java JDK 11.
- JOSM 18193 or higher.

## Extra options

![image](https://user-images.githubusercontent.com/12978932/191889354-4b67612b-b3c4-454c-89b4-1397b6cb813b.png)

1. Select mode.
2. Open window.
3. Adjust the tolerance of magic wand. 
