package ru.burningGlobe.generator;

import java.awt.*;

/**
 ** Created by arxan on 20.09.2016.
 **/
public final class GeneratorColors {
    static final Color eraserColor = new Color(255, 255, 255);
    static final Color roadColor = new Color(0, 0, 0);
    static final Color desertColor = new Color(255, 255, 0);
    static final Color stoneColor = new Color(200, 230, 200);
    static final Color rbColor = new Color(130, 10, 80);
    static final Color nexusColor = new Color(0, 255, 0);
    static final Color mapBorderColor = new Color(255, 0, 0);

    static String getColorType(int brushId) {
        switch (brushId) {
            case -1:
                return "ластик";
            case 1:
                return "дорога";
            case 2:
                return "пустыня";
            case 3:
                return "камень";
            case 4:
                return "рубидий";
            case 5:
                return "нексус";
            default:
                return "неизвестный тип";
        }
    }
}
