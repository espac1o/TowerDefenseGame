package ru.burningGlobe.generator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 ** Created by espacio on 19.09.2016.
 **/
class Field extends JPanel{
    private static final int CELL_SIZE = 25;
    ArrayList<Integer> roadComponents;
    private int xLinesCount;
    private int yLinesCount;
    private float ZOOM = 1;
    private int[][] field;
    private double dx = 0, dy = 0;
    private int currentBrush;
    private ArrayList<String> stack;
    private String currentStep;
    private int linesMissed;
    private Point mapStartPoint;
    private Point mapEndPoint;
    private boolean isBorderPressed;
    private boolean isControlDown;
    private Point spawnerPoint;
    private Point nexusPoint;
    private ArrayList<Point> route;

    Field(int x_count, int y_count) {
        newField(x_count, y_count);
        initRoadComponents();
        setCurrentBrush(-1);

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
                    } else if (sign == -1) {
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
                i = (int) ((e.getX() - dx) / (CELL_SIZE * ZOOM));
                j = (int) ((e.getY() - dy) / (CELL_SIZE * ZOOM));
                System.out.println("x: " + (e.getX() * ZOOM - dx) + " y: " + (e.getY() * ZOOM - dy));
                if (i >= xLinesCount || j >= xLinesCount)
                    return;
                if (i < 0 || j < 0)
                    return;
                if (currentBrush == BrushID.MAP_BORDER) {
                    isBorderPressed = true;
                    if (e.isControlDown() || isControlDown) {
                        if (mapEndPoint.x == i + 1 && mapEndPoint.y == j + 1)
                            return;
                        mapEndPoint.setLocation(i + 1, j + 1);
                        System.out.print("конечная ");
                    } else {
                        if (mapStartPoint.x == i && mapStartPoint.y == j)
                            return;
                        mapStartPoint.setLocation(i, j);
                        System.out.print("начальная ");
                    }
                    System.out.println("граница установлена в точке " + i + ":" + j);
                    MainWindow.setMapSizeInfo(getMapSize());
                    repaint();
                    return;
                }
                if (closeToBorderPoint(e.getX(), e.getY())) {
                    isBorderPressed = true;
                    return;
                }
                if (field[j][i] == currentBrush)
                    return;
                if (currentBrush == BrushID.SPAWNER) {
                    if (spawnerPoint != null)
                        field[spawnerPoint.y][spawnerPoint.x] = -1;
                    spawnerPoint = new Point(i, j);
                } else if (currentBrush == BrushID.NEXUS) {
                    if (nexusPoint != null)
                        field[nexusPoint.y][nexusPoint.x] = -1;
                    nexusPoint = new Point(i, j);
                }
                int oldBrush = field[j][i];
                MainWindow.setFileAsUnsaved();
                System.out.println(j + "x" + i);
                currentStep += "#" + i + ":" + j + "|" + field[j][i] + "->" + currentBrush;
                field[j][i] = currentBrush;
                if (roadComponents.contains(oldBrush)) {
                    if (oldBrush == BrushID.SPAWNER)
                        spawnerPoint = null;
                    else if (oldBrush == BrushID.NEXUS)
                        nexusPoint = null;
                    if (route != null && route.contains(new Point(i, j))) {
                        route = null;
                        generateRoute();
                    }
                } else if (roadComponents.contains(currentBrush)) {
                    generateRoute();
                }

                System.out.println("клетка покрашена; тип: " + GeneratorColors.getColorType(currentBrush));
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("кнопка мыши отпущена");
                isBorderPressed = false;
                isControlDown = false;
                if (!currentStep.isEmpty()) {
                    if (linesMissed != 0) {
                        stack = new ArrayList<>(stack.subList(0, stack.size() - linesMissed));
                        linesMissed = 0;
                    }
                    stack.add(currentStep);
                    currentStep = "";
                    System.out.println("команда добавлена в стек; Текущий стек: " + stack.toString());
                    MainWindow.setUndoEnabled(true);
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
                i = (int) ((e.getX() - dx) / (CELL_SIZE * ZOOM));
                j = (int) ((e.getY() - dy) / (CELL_SIZE * ZOOM));
                System.out.println("x: " + (e.getX() * ZOOM - dx) + " y: " + (e.getY() * ZOOM - dy));
                if (i >= xLinesCount || j >= xLinesCount)
                    return;
                if (i < 0 || j < 0)
                    return;
                if (isBorderPressed) {
                    if (e.isControlDown() || isControlDown) {
                        if (mapEndPoint.x == i + 1 && mapEndPoint.y == j + 1)
                            return;
                        mapEndPoint.setLocation(i + 1, j + 1);
                        System.out.print("конечная ");
                    } else {
                        if (mapStartPoint.x == i && mapStartPoint.y == j)
                            return;
                        mapStartPoint.setLocation(i, j);
                        System.out.print("начальная ");
                    }
                    System.out.println("граница установлена в точке " + i + ":" + j);
                    MainWindow.setMapSizeInfo(getMapSize());
                    repaint();
                    return;
                }
                if (currentBrush == BrushID.NEXUS || currentBrush == BrushID.SPAWNER)
                    return;
                if (field[j][i] == currentBrush)
                    return;
                int oldBrush = field[j][i];
                System.out.println(j + "x" + i);
                currentStep += "#" + i + ":" + j + "|" + field[j][i] + "->" + currentBrush;
                field[j][i] = currentBrush;
                if (roadComponents.contains(oldBrush)) {
                    if (oldBrush == BrushID.SPAWNER)
                        spawnerPoint = null;
                    else if (oldBrush == BrushID.NEXUS)
                        nexusPoint = null;
                    if (route != null && route.contains(new Point(i, j))) {
                        route = null;
                        generateRoute();
                    }
                } else if (roadComponents.contains(currentBrush)) {
                    generateRoute();
                }

                System.out.println("клетка покрашена; тип: " + GeneratorColors.getColorType(currentBrush));
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int i, j;
                i = (int) ((e.getX() - dx) / (CELL_SIZE * ZOOM));
                j = (int) ((e.getY() - dy) / (CELL_SIZE * ZOOM));
                if (i >= xLinesCount || j >= xLinesCount)
                    return;
                MainWindow.setLineInfo(i + ":" + j);
            }
        });
    }

    public void newField(int x, int y) {
        xLinesCount = x;
        yLinesCount = y;
        if (stack == null)
            stack = new ArrayList<>();
        else
            stack.clear();
        spawnerPoint = null;
        nexusPoint = null;
        route = null;
        currentStep = "";
        linesMissed = 0;
        isBorderPressed = false;
        isControlDown = false;
        mapStartPoint = new Point(0, 0);
        mapEndPoint = new Point(x, y);
        MainWindow.setUndoEnabled(false);
        MainWindow.setRedoEnabled(false);
        MainWindow.setMapSizeInfo(getMapSize());
    }

    public void setCurrentBrush(int currentBrush) {
        this.currentBrush = currentBrush;
    }

    void setMapSize(Point mapStartPoint_, Point mapEndPoint_) {
        if (mapStartPoint != null) {
            mapStartPoint = mapStartPoint_;
        }
        if (mapEndPoint != null) {
            mapEndPoint = mapEndPoint_;
        }
        MainWindow.setMapSizeInfo(getMapSize());
    }

    public String getMapSize() {
        return (mapEndPoint.x - mapStartPoint.x) + "x" + (mapEndPoint.y - mapStartPoint.y);
    }

    int[][] getField() {
        return field;
    }

    private void setField(int[][] field) {
        this.field = field;
    }

    int getXLinesCount() {
        return xLinesCount;
    }

    int getYLinesCount() {
        return yLinesCount;
    }

    public Point getMapStartPoint() {
        return mapStartPoint;
    }

    public Point getMapEndPoint() {
        return mapEndPoint;
    }

    public ArrayList<Point> getRoute() {
        return route;
    }

    private void setRoute(ArrayList<Point> route_) {
        route = route_;
    }

    public void generateRoute() {
        generateRoute(spawnerPoint, null);
    }

    private void generateRoute(Point cell, ArrayList<Point> currRoute) {
        if (spawnerPoint == null || nexusPoint == null)
            return;
        if (cell == null)
            cell = spawnerPoint;
        if (currRoute == null) { // if route is clear
            currRoute = new ArrayList<>();
        }
        if (currRoute.contains(cell))
            return;
        currRoute.add(cell);
        if (cell.equals(nexusPoint)) { // if it's nexus
            if (route == null) {
                route = new ArrayList<>();
                route.addAll(currRoute);
            } else if (route.size() > currRoute.size()) {
                route.clear();
                route.addAll(currRoute);
            }
            currRoute.remove(cell);
            return;
        }
        if (route != null && currRoute.size() >= route.size()) {
            currRoute.remove(cell);
            return;
        }
        if (cell.y - 1 >= 0 && roadComponents.contains(field[cell.y - 1][cell.x])) { // if upper cell is road
            generateRoute(new Point(cell.x, cell.y - 1), currRoute);
        }
        if (cell.x + 1 < field[cell.y].length && roadComponents.contains(field[cell.y][cell.x + 1])) { // if right cell is road
            generateRoute(new Point(cell.x + 1, cell.y), currRoute);
        }
        if (cell.y + 1 < field.length && roadComponents.contains(field[cell.y + 1][cell.x])) { // if lower cell is road
            generateRoute(new Point(cell.x, cell.y + 1), currRoute);
        }
        if (cell.x - 1 >= 0 && roadComponents.contains(field[cell.y][cell.x - 1])) { // if left cell is road
            generateRoute(new Point(cell.x - 1, cell.y), currRoute);
        }
        currRoute.remove(cell);
    }

    void initField() {
        field = new int[yLinesCount][xLinesCount];
        for (int i = 0; i < yLinesCount; i++) {
            for (int j = 0; j < xLinesCount; j++) {
                field[i][j] = -1;
            }
        }
        repaint();
    }

    void initField(int[][] field) {
        setField(field);
        initSpawnerPoint();
        initNexusPoint();
        if (route == null)
            generateRoute();
        repaint();
    }

    void initField(int[][] field, ArrayList<Point> route) {
        setRoute(route);
        initField(field);
    }

    private void initNexusPoint() {
        for (int j = field.length - 1; j >= 0; j--) {
            for (int i = field[j].length - 1; i >= 0; i--) {
                if (field[j][i] == BrushID.NEXUS) {
                    nexusPoint = new Point(i, j);
                }
            }
        }
    }

    public void initRoadComponents() {
        if (roadComponents == null)
            roadComponents = new ArrayList<>();
        roadComponents.add(BrushID.ROAD);
//        roadComponents.add(BrushID.WATER);
//        roadComponents.add(BrushID.UNDERGROUND);
        roadComponents.add(BrushID.SPAWNER);
        roadComponents.add(BrushID.NEXUS);
    }

    private void initSpawnerPoint() {
        for (int j = 0; j < field.length; j++) {
            for (int i = 0; i < field[j].length; i++) {
                if (field[j][i] == BrushID.SPAWNER) {
                    spawnerPoint = new Point(i, j);
                }
            }
        }
    }

    private boolean closeToBorderPoint(int clickX, int clickY) {
        final double error = 5 * ZOOM;
        final double startPointX = mapStartPoint.x * CELL_SIZE * ZOOM + dx;
        final double startPointY = mapStartPoint.y * CELL_SIZE * ZOOM + dy;
        final double endPointX = mapEndPoint.x * CELL_SIZE * ZOOM + dx;
        final double endPointY = mapEndPoint.y * CELL_SIZE * ZOOM + dy;

        if (Math.abs(startPointX - clickX) <= error && Math.abs(startPointY - clickY) <= error)
            return true;
        else if (Math.abs(endPointX - clickX) <= error && Math.abs(endPointY - clickY) <= error) {
            isControlDown = true;
            return true;
        }

        return false;
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
        int oldBrush;
        boolean routeWasUpdated;

        routeWasUpdated = false;
        linesMissed += 1;
        if (linesMissed == stack.size())
            MainWindow.setUndoEnabled(false);
        MainWindow.setRedoEnabled(true);
        step = stack.get(stack.size() - linesMissed);
        while(true) {
            x = Integer.parseInt(step.substring(step.indexOf("#") + 1, step.indexOf(":")));
            y = Integer.parseInt(step.substring(step.indexOf(":") + 1, step.indexOf("|")));
            brush = Integer.parseInt(step.substring(step.indexOf("|") + 1, step.indexOf("->")));
            oldBrush = field[y][x];
            field[y][x] = brush;
            if (roadComponents.contains(oldBrush) || roadComponents.contains(brush)) {
                if (oldBrush == BrushID.NEXUS)
                    nexusPoint = null;
                else if (brush == BrushID.NEXUS)
                    nexusPoint = new Point(x, y);
                if (oldBrush == BrushID.SPAWNER)
                    spawnerPoint = null;
                else if (brush == BrushID.SPAWNER)
                    spawnerPoint = new Point(x, y);
                if (!routeWasUpdated)
                    routeWasUpdated = true;
            }
            nextStep = step.substring(1).indexOf("#") + 1;
            if (nextStep == 0)
                break;
            else
                step = step.substring(nextStep);
        }
        if (routeWasUpdated) {
            route = null;
            generateRoute();
        }
        repaint();
    }

    void redo() {
        String step;
        int x, y;
        int brush;
        int nextStep;
        int oldBrush;
        boolean routeWasUpdated;

        routeWasUpdated = false;
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
            oldBrush = field[y][x];
            field[y][x] = brush;
            if (roadComponents.contains(oldBrush) || roadComponents.contains(brush)) {
                if (oldBrush == BrushID.NEXUS)
                    nexusPoint = null;
                else if (brush == BrushID.NEXUS)
                    nexusPoint = new Point(x, y);
                if (oldBrush == BrushID.SPAWNER)
                    spawnerPoint = null;
                else if (brush == BrushID.SPAWNER)
                    spawnerPoint = new Point(x, y);
                if (!routeWasUpdated)
                    routeWasUpdated = true;
            }
            if (nextStep == 0)
                break;
            else
                step = step.substring(nextStep);
        }
        if (routeWasUpdated)
            generateRoute();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        for (int j = 0; j < yLinesCount; j++) {
            for (int i = 0; i < xLinesCount; i++) {
                g.setColor(GeneratorColors.getColorById(field[j][i]));
                if (field[j][i] != BrushID.MAP_BORDER || GeneratorColors.getColorById(field[j][i]) != GeneratorColors.errorColor)
                    g.fillRect(
                            (int) (i * CELL_SIZE * ZOOM + dx),
                            (int) (j * CELL_SIZE * ZOOM + dy),
                            (int) (CELL_SIZE * ZOOM),
                            (int) (CELL_SIZE * ZOOM)
                    );
            }
        }
        g.setColor(GeneratorColors.fieldLineColor);
        for (int i = 0; i <= xLinesCount; i++) {
            g.drawLine(
                    (int) (i * CELL_SIZE * ZOOM + dx),
                    (int) dy,
                    (int) (i * CELL_SIZE * ZOOM + dx),
                    (int) (yLinesCount * CELL_SIZE * ZOOM + dy)
            );
        }
        for (int i = 0; i <= yLinesCount; i++) {
            g.drawLine(
                    (int) dx,
                    (int) (i * CELL_SIZE * ZOOM + dy),
                    (int) (xLinesCount * CELL_SIZE * ZOOM + dx),
                    (int) (i * CELL_SIZE * ZOOM + dy)
            );
        }
        g.setColor(GeneratorColors.mapBorderColor);
        g.drawLine(
                (int) (mapStartPoint.x * CELL_SIZE * ZOOM + dx),
                (int) (mapStartPoint.y * CELL_SIZE * ZOOM + dy),
                (int) (mapStartPoint.x * CELL_SIZE * ZOOM + dx),
                (int) (mapEndPoint.y * CELL_SIZE * ZOOM + dy)
        ); // |<
        g.drawLine(
                (int) (mapStartPoint.x * CELL_SIZE * ZOOM + dx),
                (int) (mapStartPoint.y * CELL_SIZE * ZOOM + dy),
                (int) (mapEndPoint.x * CELL_SIZE * ZOOM + dx),
                (int) (mapStartPoint.y * CELL_SIZE * ZOOM + dy)
        ); // -^
        g.drawLine(
                (int) (mapEndPoint.x * CELL_SIZE * ZOOM + dx),
                (int) (mapStartPoint.y * CELL_SIZE * ZOOM + dy),
                (int) (mapEndPoint.x * CELL_SIZE * ZOOM + dx),
                (int) (mapEndPoint.y * CELL_SIZE * ZOOM + dy)
        ); // |>
        g.drawLine(
                (int) (mapStartPoint.x * CELL_SIZE * ZOOM + dx),
                (int) (mapEndPoint.y * CELL_SIZE * ZOOM + dy),
                (int) (mapEndPoint.x * CELL_SIZE * ZOOM + dx),
                (int) (mapEndPoint.y * CELL_SIZE * ZOOM + dy)
        ); // _

        g2.setColor(GeneratorColors.mapBorderPointColor);
        g2.setStroke(new BasicStroke(3));
        g2.drawRect(
                (int) (mapStartPoint.x * CELL_SIZE * ZOOM + dx),
                (int) (mapStartPoint.y * CELL_SIZE * ZOOM + dy),
                1,
                1
        );
        g2.drawRect(
                (int) (mapEndPoint.x * CELL_SIZE * ZOOM + dx),
                (int) (mapEndPoint.y * CELL_SIZE * ZOOM + dy),
                1,
                1
        );

        if (route != null) {
            g.setColor(GeneratorColors.routeColor);
            g2.setColor(GeneratorColors.routeColor);
            for (int i = 0; i < route.size(); i++) {
                Point a = route.get(i);
                if (i + 1 != route.size()) {
                    Point b = route.get(i + 1);
                    g.drawLine(
                            (int) ((a.getX() + 0.5f) * CELL_SIZE * ZOOM + dx),
                            (int) ((a.getY() + 0.5f) * CELL_SIZE * ZOOM + dy),
                            (int) ((b.getX() + 0.5f) * CELL_SIZE * ZOOM + dx),
                            (int) ((b.getY() + 0.5f) * CELL_SIZE * ZOOM + dy)
                    );
                }

                g2.drawRect(
                        (int) ((a.getX() + 0.5f) * CELL_SIZE * ZOOM + dx),
                        (int) ((a.getY() + 0.5f) * CELL_SIZE * ZOOM + dy),
                        1,
                        1
                );
            }
        }
    }
}
