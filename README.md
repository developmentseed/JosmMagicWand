# DS-annotate plugin for Java OpenStreetMap Editor

The DS-annotate plugin is an extension for the Java OpenStreetMap Desktop Editor (JOSM). Built upon ds-annotate, this plugin utilizes the [Segment Anything Service](https://github.com/developmentseed/segment-anything-services), which leverages Generative AI. Additionally, it allows users to label areas using a spectrum of colors, similar to a magic wand tool, enabling faster and more accurate mapping.

![Peek 2022-11-09 16-53](https://user-images.githubusercontent.com/12978932/200950045-179c72d5-600c-4012-b2a4-3ceb1345ea25.gif)

## Installation

1. Search the magic wand plugin.

   ![image](https://user-images.githubusercontent.com/12978932/200428835-a652ef65-f895-4acd-a19a-bef3e7a8175a.png)

2. Wait to download the plugin, sometimes it takes a few minutes, because the plugin is 100 MB in size.

   ![image](https://user-images.githubusercontent.com/12978932/200429273-ce2e2d4d-8839-46ce-ba47-a620e3984b17.png)

3. Confirmation window.

   ![image](https://user-images.githubusercontent.com/12978932/200429366-c8566cb9-d842-4efe-b810-151b68f86fc3.png)
   ![image](https://user-images.githubusercontent.com/12978932/200431510-a414f03f-c285-4217-adf0-d9d91d41d47f.png)

## Usage

The JOSM Magic Wand plugin offers three functionalities:

- Generate Geometries
- Merge Geometries
- Simplify Geometries 

### Generate geometries.

To generate geometries, you must have a base map activated and a data layer.

- You can press the shortcut `Ctrl + 1` or select directly in the mode bar (upper
  left) ![image](https://user-images.githubusercontent.com/12978932/191857775-71da462d-66fd-401f-b03a-fbf444c07b04.png),
  you will enter Magic Wand mode and you will see a wand on the
  cursor ![image](https://user-images.githubusercontent.com/12978932/191858042-942aa381-3c4b-42bf-b9df-b23782d1dce5.png)
  .
- Click on the area.
- `ctrl + 2` to generate the geometry.

- To add or enhance the selection, you can press the `ctrl` button and `click` on new area.
- To subtract the selection, you can press the `switch` button and `click` on area.

### Merge geometries

This functionality allows you to merge several geometries into one, the geometries must intersects.

![Peek 2022-09-22 22-44](https://user-images.githubusercontent.com/12978932/191888528-ea2105bc-7994-40e0-95bd-9d6c87c97631.gif)

- Select one or more geometries.
- Press `ctrl + 3`.

### Simplify geometries.

This functionality allows you to simplify and smooth a geometry.

- Select one or more geometries.
- Press `ctrl + 4`.


## Requirements

- Java JDK 11+.
- JOSM 18193 or higher.

## Extra options 

![image](https://user-images.githubusercontent.com/12978932/200949130-5c4bdea3-cccb-4865-987a-5d15013089d7.png)

![image](https://github.com/developmentseed/JosmMagicWand/assets/12978932/3261b754-e1f2-495b-9255-d0cc83b6009c)
1. Select mode.
2. Open window.
3. In the window, you will find the following options:

   - **Tolerance**: This option allows you to configure the color tolerance of the Magic Wand tool.

    ```
   Higher values expand the tolerance to more colors.
   Lower values make the tool more specific to colors.
   ```
   - **Exterior contour**: This algorithm is specifically designed to simplify polygons while maintaining the shape and outer contour (hull) of the original polygon.

    ```
    Higher values result in less simplification.
    Lower values result in more simplification.
    ```
   - **Vertices**: This algorithm focuses on the general simplification of polygons. Its goal is to reduce the number of vertices in a geometry while preserving its overall shape.

    ```
   Higher values result in more simplification.
   Lower values result in less simplification.
   ```
   - **Topology**: This algorithm is used to simplify geometries while preserving their topology. Topology refers to the spatial relationship and connectivity between geometric elements.
   
    ```
    Higher values result in more simplification.
    Lower values result in less simplification.
    ```
    - **Smooth Angle**: This option allows you to smooth very sharp angles. The value entered represents the minimum angle to be simplified.
    - **add tag**: a pop-up window will appear, allowing you to enter the tag that will be added when generating or simplifying the geometry, the format its `key=value`.


To disable the algorithms, set the value to 0.

## Use cases
### Cases where it works well

| Image                                                                                                            | Description                                            |
|------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------|
| ![image](https://user-images.githubusercontent.com/12978932/200626354-cfe0ab0b-5490-4abf-9e0d-0e30bf57abf7.png)  | The edge of the lake has borders marked with the path. |
| ![image](https://user-images.githubusercontent.com/12978932/200627390-ddd26531-cfe7-47c8-bf16-8e7a64ce3f60.png)  | The edge of the lake and the farm have marked borders. |
| ![image](https://user-images.githubusercontent.com/12978932/200627737-04ec310a-2499-4a2d-8b33-c112e6926cdf.png)  | Uniform color and sharp edges (contrast).              |
| ![image](https://user-images.githubusercontent.com/12978932/200628149-7e536725-369b-42aa-8309-d7cd37a73baa.png)  | Uniform color and sharp edges (contrast).              |
| ![image](https://user-images.githubusercontent.com/12978932/200628412-756285de-581f-4369-8e81-f94c8f2f6da5.png)  | Uniform color and sharp edges (contrast).              |
| ![image](https://user-images.githubusercontent.com/12978932/200628944-2c2143ec-a7e0-4b87-a507-41ad069e4d39.png)  | Uniform color and sharp edges (contrast).              |



### Cases where it doesn't work well

| Image                                                                                                            | Description                                                                   |
|------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------|
| ![image](https://user-images.githubusercontent.com/12978932/200629118-48fa92ef-60a3-4d8a-b807-917c9dd95b8c.png)  | The two farms are separated by a very thin path of similar color.             |
| ![image](https://user-images.githubusercontent.com/12978932/200629404-9b9a48f5-4c9d-483e-9f8d-1a7499f51659.png)  | Parts of the farm and the border have similar colors with the rest.           |
| ![image](https://user-images.githubusercontent.com/12978932/200644356-09bbca8e-833c-49f8-9427-cb2c4de7a223.png)  | The borders of the farm with the road and the other farm have similar colors. |
