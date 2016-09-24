package ru.burningGlobe.generator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 ** Created by espacio on 19.09.2016.
 **/
class Field extends JPanel{
    private int xLinesCount;
    private int yLinesCount;
    private static final int CELL_SIZE = 25;
    private float ZOOM = 1;
    private int[][] field;
    private double dx = 0, dy = 0;
    private int currentBrush;
    private ArrayList<String> stack;
    private String currentStep;
    private int linesMissed;
    private Point mapStartPoint;
    private Point mapEndPoint;
    private boolean borderIsPressed;

    Field(int x_count, int y_count) {
        xLinesCount = x_count;
        yLinesCount = y_count;
        stack = new ArrayList<>();
        currentStep = "";
        linesMissed = 0;
        borderIsPressed = false;

        mapStartPoint = new Point(0, 0);
        mapEndPoint = new Point(x_count, y_count);

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int wheelVelocity = CELL_SIZE / 2;
                int sign = e.getWheelRotation();
                int mapHeight = getBounds().height;
                int mapWidth = getBounds().width;
                if (e.isControlDown()) {
                    if (sign == 1) {
                        System.out.println("Scrolled down");
                        if (CELL_SIZE * ZOOM * xLinesCount + dx > mapWidth) {
                            if (CELL_SIZE * ZOOM * xLinesCount + dx + wheelVelocity < mapWidth)
                                wheelVelocity = (int) (CELL_SIZE * ZOOM * xLinesCount + dx) - mapWidth;
                            dx -= sign * wheelVelocity;
                            System.out.println("dx = " + dx + "dy = " + dy);
                        }
                    } else if (sign == -1) {
                        System.out.println("Scrolled up");
                        if (dx < 0) {
                            if (dx + wheelVelocity > 0)
                                wheelVelocity = (int) dx;
                            dx -= sign * wheelVelocity;
                            System.out.println("dx = " + dx + "dy = " + dy);
                        }
                    }
                } else {
                    if (sign == 1) {
                        System.out.println("Scrolled down");
                        if (CELL_SIZE * ZOOM * yLinesCount + dy > mapHeight) {
                            if (CELL_SIZE * ZOOM * yLinesCount + dy + wheelVelocity < mapHeight)
                                wheelVelocity = (int) (CELL_SIZE * ZOOM * yLinesCount + dy) - mapHeight;
                            dy -= sign * wheelVelocity;
                            System.out.println("dx = " + dx + "dy = " + dy);
                        }
                    }
                    else if (sign == -1) {
                        System.out.println("Scrolled up");
                        if (dy < 0) {
                            if (dy + wheelVelocity > 0)
                                wheelVelocity = (int) dy;
                            dy -= sign * wheelVelocity;
                            System.out.println("dx = " + dx + "dy = " + dy);
                        }
                    }
                }
                repaint();
            }
    });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("кнопка мыши нажата");
                int i, j;
                final int BRUSH_MAP_BORDER_ID = 6;
                i = (int) ((e.getX() + dx) / (CELL_SIZE * ZOOM));
                j = (int) ((e.getY() + dy) / (CELL_SIZE * ZOOM));
                System.out.println("x: " + (e.getX() * ZOOM + dx) + " y: " + (e.getY() * ZOOM + dy));
                if (field[j][i] == currentBrush)
                    return;
                if (i >= xLinesCount || j >= xLinesCount)
                    return;
                MainWindow.setFileAsUnsaved();
                if (currentBrush == BRUSH_MAP_BORDER_ID) {
                    if (e.isControlDown()) {
                        if (mapEndPoint.x == i + 1 && mapEndPoint.y == j + 1)
                            return;
                        mapEndPoint.setLocation(i + 1, j + 1);
                        System.out.print("конечная ");
                    }
                    else {
                        if (mapStartPoint.x == i && mapStartPoint.y == j)
                            return;
                        mapStartPoint.setLocation(i, j);
                        System.out.print("начальная ");
                    }
                    System.out.println("граница установлена в точке " + i + ":" + j);
                    borderIsPressed = true;
                    repaint();
                    return;
                }
                MainWindow.setUndoEnabled(true);
                System.out.println(j + "x" + i);
                currentStep += "#" + i + ":" + j + "|" + field[j][i] + "->" + currentBrush;
                field[j][i] = currentBrush;
                System.out.println("клетка покрашена; тип: " + GeneratorColors.getColorType(currentBrush));
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("кнопка мыши отпущена");
                borderIsPressed = false;
                if (!currentStep.isEmpty()) {
                    if (linesMissed != 0) {
                        stack = new ArrayList<>(stack.subList(0, stack.size() - linesMissed));
                        linesMissed = 0;
                    }
                    stack.add(currentStep);
                    currentStep = "";
                    System.out.println("команда добавлена в стек; Текущий стек: " + stack.toString());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                System.out.println("кнопка мыши удерживается");
                int i, j;
                i = (int) ((e.getX() + dx) / (CELL_SIZE * ZOOM));
                j = (int) ((e.getY() + dy) / (CELL_SIZE * ZOOM));
                System.out.println("x: " + (e.getX() * ZOOM + dx) + " y: " + (e.getY() * ZOOM + dy));
                if (field[j][i] == currentBrush)
                    return;
                if (i >= xLinesCount || j >= xLinesCount)
                    return;
                MainWindow.setFileAsUnsaved();
                if (borderIsPressed) {
                    if (e.isControlDown()) {
                        if (mapEndPoint.x == i + 1 && mapEndPoint.y == j + 1)
                            return;
                        mapEndPoint.setLocation(i + 1, j + 1);
                        System.out.print("конечная ");
                    }
                    else {
                        if (mapStartPoint.x == i && mapStartPoint.y == j)
                            return;
                        mapStartPoint.setLocation(i, j);
                        System.out.print("начальная ");
                    }
                    System.out.println("граница установлена в точке " + i + ":" + j);
                    repaint();
                    return;
                }
                System.out.println(j + "x" + i);
                currentStep += "#" + i + ":" + j + "|" + field[j][i] + "->" + currentBrush;
                field[j][i] = currentBrush;
                System.out.println("клетка покрашена; тип: " + GeneratorColors.getColorType(currentBrush));
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
//                System.out.println("x: " + e.getX() + " y: " + e.getY());
            }
        });
    }

    public void setCurrentBrush(int currentBrush) {
        this.currentBrush = currentBrush;
    }

    private void setField(int[][] field) {
        this.field = field;
    }

    private void setMap(Point mapStartPoint, Point mapEndPoint) {
        this.mapStartPoint = mapStartPoint;
        this.mapEndPoint = mapEndPoint;
    }

    int[][] getField() {
        return field;
    }

    int getXLinesCount() {
        return xLinesCount;
    }

    int getYLinesCount() {
        return yLinesCount;
    }

    private void initMap() {
        for (int i = 0; i < yLinesCount; i++) {
            for (int j = 0; j < xLinesCount; j++) {
                field[i][j] = -1;
            }
        }
    }

    void createNewMap() {
        field = new int[yLinesCount][xLinesCount];
        initMap();
        setCurrentBrush(-1);
        repaint();
    }

    void createNewMap(int[][] map) {
        setField(map);
        setCurrentBrush(-1);
        repaint();
    }

    void zoom(float dZoom) {
        dZoom += ZOOM;
        if (dZoom <= 0)
            ZOOM = 0.5f;
        else if (dZoom > 2)
            ZOOM = 2;
        else
            ZOOM = dZoom;
        validate();
        repaint();
    }

    void undo() {
        String step;
        int x, y;
        int brush;
        int nextStep;
        linesMissed += 1;
        if (linesMissed == stack.size())
            MainWindow.setUndoEnabled(false);
        MainWindow.setRedoEnabled(true);
        step = stack.get(stack.size() - linesMissed);
        while(true) {
            x = Integer.parseInt(step.substring(step.indexOf("#") + 1, step.indexOf(":")));
            y = Integer.parseInt(step.substring(step.indexOf(":") + 1, step.indexOf("|")));
            brush = Integer.parseInt(step.substring(step.indexOf("|") + 1, step.indexOf("->")));
            field[y][x] = brush;
            nextStep = step.substring(1).indexOf("#") + 1;
            if (nextStep == 0)
                break;
            else
                step = step.substring(nextStep);
        }
        repaint();
    }

    void redo() {
        String step;
        int x, y;
        int brush;
        int nextStep;
        linesMissed -= 1;
        if (linesMissed == 0)
            MainWindow.setRedoEnabled(false);
        MainWindow.setUndoEnabled(true);
        step = stack.get(stack.size() - linesMissed - 1);
        while(true) {
            x = Integer.parseInt(step.substring(step.indexOf("#") + 1, step.indexOf(":")));
            y = Integer.parseInt(step.substring(step.indexOf(":") + 1, step.indexOf("|")));
            nextStep = step.substring(1).indexOf("#") + 1;
            if (nextStep == 0)
                brush = Integer.parseInt(step.substring(step.indexOf("->") + 2));
            else
                brush = Integer.parseInt(step.substring(step.indexOf("->") + 2, nextStep));
            field[y][x] = brush;
            if (nextStep == 0)
                break;
            else
                step = step.substring(nextStep);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int j = 0; j < yLinesCount; j++) {
            for (int i = 0; i < xLinesCount; i++) {
                if (field[j][i] == -1) // white cell <==> erased
                    g.setColor(GeneratorColors.eraserColor);
                else if (field[j][i] == 1) // black cell <==> road
                    g.setColor(GeneratorColors.roadColor);
                else if (field[j][i] == 2) // yellow cell <==> desert
                    g.setColor(GeneratorColors.desertColor);
                else if (field[j][i] == 3) // gray cell <==> stone
                    g.setColor(GeneratorColors.stoneColor);
                else if (field[j][i] == 4) // purple cell <==> Rb
                    g.setColor(GeneratorColors.rbColor);
                else if (field[j][i] == 5) // green cell <==> nexus
                    g.setColor(GeneratorColors.nexusColor);
                g.fillRect((int)(i * CELL_SIZE * ZOOM + dx), (int)(j * CELL_SIZE * ZOOM + dy), (int)(CELL_SIZE * ZOOM), (int)(CELL_SIZE * ZOOM));
            }
        }
        g.setColor(new Color(0, 0, 0));
        for (int i = 0; i <= xLinesCount; i++) {
            g.drawLine((int)(i * CELL_SIZE * ZOOM + dx), (int) dy,(int)(i * CELL_SIZE * ZOOM + dx), (int)(yLinesCount * CELL_SIZE * ZOOM + dy));
        }
        for (int i = 0; i <= yLinesCount; i++) {
            g.drawLine((int) dx, (int)(i * CELL_SIZE * ZOOM + dy), (int)(xLinesCount * CELL_SIZE * ZOOM + dx), (int)(i * CELL_SIZE * ZOOM + dy));
        }
        g.setColor(GeneratorColors.mapBorderColor);
        g.drawLine((int) (mapStartPoint.x * CELL_SIZE * ZOOM + dx), (int) (mapStartPoint.y * CELL_SIZE * ZOOM + dy), (int) (mapStartPoint.x * CELL_SIZE * ZOOM + dx), (int) (mapEndPoint.y * CELL_SIZE * ZOOM + dy)); // |<
        g.drawLine((int) (mapStartPoint.x * CELL_SIZE * ZOOM + dx), (int) (mapStartPoint.y * CELL_SIZE * ZOOM + dy), (int) (mapEndPoint.x * CELL_SIZE * ZOOM + dx), (int) (mapStartPoint.y * CELL_SIZE * ZOOM + dy)); // -^
        g.drawLine((int) (mapEndPoint.x * CELL_SIZE * ZOOM + dx), (int) (mapStartPoint.y * CELL_SIZE * ZOOM + dy), (int) (mapEndPoint.x * CELL_SIZE * ZOOM + dx), (int) (mapEndPoint.y * CELL_SIZE * ZOOM + dy)); // |>
        g.drawLine((int)(mapStartPoint.x * CELL_SIZE * ZOOM + dx), (int)(mapEndPoint.y * CELL_SIZE * ZOOM + dy), (int)(mapEndPoint.x * CELL_SIZE * ZOOM + dx), (int)(mapEndPoint.y * CELL_SIZE * ZOOM + dy)); // _
    }
}
