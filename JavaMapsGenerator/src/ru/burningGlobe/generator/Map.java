package ru.burningGlobe.generator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 ** Created by espacio on 19.09.2016.
 **/
class Map extends JPanel{
    private int xLinesCount;
    private int yLinesCount;
    private static final int CELL_SIZE = 50;
    private float ZOOM = 1;
    private int[][] map;
    private double dx = 0, dy = 0;
    private boolean isMove = false;
    private boolean ctrlPressed = false;
    private Point mouse;
    private int currentBrush;

    Map(int x_count, int y_count) {
        xLinesCount = x_count;
        yLinesCount = y_count;

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int dWheel = e.getWheelRotation();
                zoom(dWheel);
            }
        });

        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("кнопка мыши нажата");
                MainWindow.setFileAsUnsaved();
                int i, j;
                i = (int) (e.getX() / (CELL_SIZE * ZOOM));
                j = (int) (e.getY() / (CELL_SIZE * ZOOM));
                System.out.println("x: " + e.getX() + " y: " + e.getY());
                if (i >= xLinesCount || j >= xLinesCount)
                    return;
                System.out.println(j + "x" + i);
                map[j][i] = currentBrush;
                System.out.println("клетка покрашена; тип: " + GeneratorColors.getColorType(currentBrush));
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                System.out.println("кнопка мыши отпущена");
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
                MainWindow.setFileAsUnsaved();
                int i, j;
                i = (int) (e.getX() / (CELL_SIZE * ZOOM));
                j = (int) (e.getY() / (CELL_SIZE * ZOOM));
                System.out.println("x: " + e.getX() * ZOOM + " y: " + e.getY() * ZOOM);
                if (i >= xLinesCount || j >= xLinesCount)
                    return;
                System.out.println(j + "x" + i);
                map[j][i] = currentBrush;
                System.out.println("клетка покрашена; тип: " + GeneratorColors.getColorType(currentBrush));
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                System.out.println("x: " + e.getX() + " y: " + e.getY());
            }
        });

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.isControlDown())
                    ctrlPressed = true;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                ctrlPressed = false;
            }
        });
    }

    public void setCurrentBrush(int currentBrush) {
        this.currentBrush = currentBrush;
    }

    private void setMap(int[][] map) {
        this.map = map;
    }

    int[][] getMap() {
        return map;
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
                map[i][j] = -1;
            }
        }
    }

    void createNewMap() {
        map = new int[yLinesCount][xLinesCount];
        initMap();
        setCurrentBrush(-1);
        repaint();
    }

    void createNewMap(int[][] map) {
        setMap(map);
        setCurrentBrush(-1);
        repaint();
    }

    private void zoom(int dZoom) {
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int j = 0; j < yLinesCount; j++) {
            for (int i = 0; i < xLinesCount; i++) {
                if (map[j][i] == -1) // white cell <==> erased
                    g.setColor(GeneratorColors.eraserColor);
                else if (map[j][i] == 1) // black cell <==> road
                    g.setColor(GeneratorColors.roadColor);
                else if (map[j][i] == 2) // yellow cell <==> desert
                    g.setColor(GeneratorColors.desertColor);
                else if (map[j][i] == 3) // gray cell <==> stone
                    g.setColor(GeneratorColors.stoneColor);
                else if (map[j][i] == 4) // purple cell <==> Rb
                    g.setColor(GeneratorColors.rbColor);
                else if (map[j][i] == 5) // green cell <==> nexus
                    g.setColor(GeneratorColors.nexusColor);
                g.fillRect((int)(i * CELL_SIZE * ZOOM), (int)(j * CELL_SIZE * ZOOM), (int)(CELL_SIZE * ZOOM), (int)(CELL_SIZE * ZOOM));
            }
        }
        g.setColor(new Color(0, 0, 0));
        for (int i = 0; i <= xLinesCount; i++) {
            g.drawLine((int)(i * CELL_SIZE * ZOOM + dx), (int) dy,(int)(i * CELL_SIZE * ZOOM + dx), (int)(yLinesCount * CELL_SIZE * ZOOM + dy));
        }
        for (int i = 0; i <= yLinesCount; i++) {
            g.drawLine((int) dx, (int)(i * CELL_SIZE * ZOOM + dy), (int)(xLinesCount * CELL_SIZE * ZOOM + dx), (int)(i * CELL_SIZE * ZOOM + dy));
        }
    }
}
