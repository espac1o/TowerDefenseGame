package ru.burningGlobe.generator;

import java.awt.*;

/**
 ** Created by arxan on 20.09.2016.
 **/
public final class GeneratorColors {
    static final Color eraserColor = new Color(255, 255, 255);
    static final Color roadColor = new Color(63, 0, 11);
    static final Color desertColor = new Color(255, 255, 0);
    static final Color stoneColor = new Color(200, 230, 200);
    static final Color rbColor = new Color(130, 10, 80);
    static final Color nexusColor = new Color(0, 255, 0);
    static final Color mapBorderColor = new Color(255, 0, 0);
    static final Color mapEnterColor = new Color(255, 0, 0);
    static final Color towerColor = new Color(129, 6, 231);
    static final Color waterColor = new Color(70, 70, 220);
    static final Color undergroundColor = new Color(100, 40, 30);
    static final Color mapBorderPointColor = new Color(140, 0, 0);
    static final Color statusBarBorderColor = new Color(0, 0, 0);
    static final Color fieldLineColor = new Color(0, 0, 0);
    static final Color errorColor = new Color(255, 0, 0);
    static final Color routeColor = new Color(255, 0, 224);
    static final Color menuItemTitledBorderDefaultTextColor = new Color(255, 255, 255);
    static final Color menuItemTitledBorderBlackTextColor = new Color(0, 0, 0);

    static String getColorType(int brushId) {
        switch (brushId) {
            case BrushID.ERAISER:
                return " ластик";
            case BrushID.ROAD:
                return " дорога";
            case BrushID.DESERT:
                return " пустыня";
            case BrushID.STONE:
                return " камень";
            case BrushID.RUBIDIUM:
                return " рубидий";
            case BrushID.SPAWNER:
                return " старт";
            case BrushID.NEXUS:
                return " нексус";
            case BrushID.MAP_BORDER:
                return " граница";
//            case BrushID.ROUTE:
//                return " путь";
            case BrushID.TOWER:
                return " башня";
            case BrushID.WATER:
                return " вода";
            case BrushID.UNDERGROUND:
                return " подземелье";
            default:
                return " неизвестный тип";
        }
    }

    static Color getColorById(int brushId) {
        switch (brushId) {
            case BrushID.ERAISER:
                return eraserColor;
            case BrushID.ROAD:
                return roadColor;
            case BrushID.DESERT:
                return desertColor;
            case BrushID.STONE:
                return stoneColor;
            case BrushID.RUBIDIUM:
                return rbColor;
            case BrushID.SPAWNER:
                return mapEnterColor;
            case BrushID.NEXUS:
                return nexusColor;
            case BrushID.MAP_BORDER:
                return mapBorderColor;
//            case BrushID.ROUTE:
//                return routeColor;
            case BrushID.TOWER:
                return towerColor;
            case BrushID.WATER:
                return waterColor;
            case BrushID.UNDERGROUND:
                return undergroundColor;
            default:
                return errorColor;
        }
    }
}
