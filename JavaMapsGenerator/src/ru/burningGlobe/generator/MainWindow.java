package ru.burningGlobe.generator;

import com.sun.istack.internal.NotNull;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

/**
 ** Created by espacio on 19.09.2016.
 **/

class MainWindow extends JFrame {
    static final int WINDOW_X = 800; // actually, 805
    static final int WINDOW_Y = 600; // actually, 687
    private static final String WINDOW_TITLE = "TD Maps Generator";
    Font statusBarFont = new Font("Century Gothic", Font.PLAIN, 12);
    private File file;
    private static boolean fileSaved = true;
    private static JMenuItem jmiUndo, jmiRedo;
    private static JLabel jlMapSizeInfo, jlPositionInfo, jlFieldSizeInfo;;

    private Field jpField;

    MainWindow() {
        JMenuBar jmbMenu;
        JMenu jmFile, jmEdit, jmBrush, jmView;
        JMenuItem jmiCreate, jmiOpen, jmiSave, jmiExit;
        JMenuItem jmiBrushRoad, jmiBrushDesert,  jmiBrushStone, jmiBrushRb, jmiBrushNexus, jmiBrushMapBorder, jmiBrushEraser;
        JMenuItem jmiZoomIn, jmiZoomOut;
        JPanel jpStatusBar;
        JLabel jlPositionFlatText, jlMapSizeFlatText, jlFieldSizeFlatText;

        setSize(WINDOW_X, WINDOW_Y);
        setResizable(false);
        setTitle(WINDOW_TITLE);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                if (!fileSaved && !saveOnCloseDialog())
                    System.out.println("Карта до сих пор не сохранена");
                else
                    System.exit(0);
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        jpStatusBar = new JPanel();
        jpStatusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
        jpStatusBar.setPreferredSize(new Dimension(this.getWidth(), 28));
        jlPositionFlatText = new JLabel("Позиция: ");
        jlPositionFlatText.setFont(statusBarFont);
        jlPositionInfo = new JLabel("0:0");
        jlPositionInfo.setPreferredSize(new Dimension(50, 16));
        jlPositionInfo.setBorder(BorderFactory.createLineBorder(GeneratorColors.statusBarBorderColor));
        jlPositionInfo.setFont(statusBarFont);
        jlPositionInfo.setHorizontalTextPosition(JLabel.CENTER);
        jlMapSizeFlatText = new JLabel("Размер карты: ");
        jlMapSizeFlatText.setFont(statusBarFont);
        jlMapSizeInfo = new JLabel("0x0");
        jlMapSizeInfo.setPreferredSize(new Dimension(55, 16));
        jlMapSizeInfo.setBorder(BorderFactory.createLineBorder(GeneratorColors.statusBarBorderColor));
        jlMapSizeInfo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String s;
                int x, y;
                if (jpField == null) {
                    JOptionPane.showMessageDialog(MainWindow.this, "Карта еще не создана");
                    return;
                }
                while (true) {
                    s = (String) JOptionPane.showInputDialog(
                            MainWindow.this,
                            "Введите размер карты (минимум: 15x15):",
                            jpField.getMapSize());
                    if (s == null) break;
                    if (s.isEmpty()) continue;
                    if (!s.contains("x")) continue;
                    x = Integer.parseInt(s.substring(0, s.indexOf("x")));
                    y = Integer.parseInt(s.substring(s.indexOf("x") + 1));
                    jpField.setMapSize(null, new Point(x, y));
                    jlMapSizeInfo.setText(jpField.getMapSize());
                    revalidate();
                    jpField.repaint();
                    return;
                }
            }
        });
        jlMapSizeInfo.setFont(statusBarFont);
        jlMapSizeInfo.setHorizontalTextPosition(JLabel.CENTER);
        jlFieldSizeFlatText = new JLabel("Размер карты: ");
        jlFieldSizeFlatText.setFont(statusBarFont);
        jlFieldSizeInfo = new JLabel("0x0");
        jlFieldSizeInfo.setPreferredSize(new Dimension(55, 16));
        jlFieldSizeInfo.setBorder(BorderFactory.createLineBorder(GeneratorColors.statusBarBorderColor));
        jlFieldSizeInfo.setFont(statusBarFont);
        jlFieldSizeInfo.setHorizontalTextPosition(JLabel.CENTER);
        jpStatusBar.add(jlPositionFlatText);
        jpStatusBar.add(jlPositionInfo);
        jpStatusBar.add(jlMapSizeFlatText);
        jpStatusBar.add(jlMapSizeInfo);
        jpStatusBar.add(jlFieldSizeFlatText);
        jpStatusBar.add(jlFieldSizeInfo);

        add(jpStatusBar, BorderLayout.SOUTH);

        jmBrush = new JMenu("Кисть");
        jmView = new JMenu("Вид");

                                                                                                                        /** MENU: FILE **/

        jmbMenu = new JMenuBar();
        add(jmbMenu, BorderLayout.NORTH);
        jmFile = new JMenu("Файл");
        jmbMenu.add(jmFile);

        jmiSave = new JMenuItem("Сохранить");
        jmiSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        jmiSave.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (saveDialog() && saveFile())
                    setTitle(WINDOW_TITLE + " - " + file.toString());
            }
        });

        jmiCreate = new JMenuItem("Создать новую карту");
        jmiCreate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        jmiCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!fileSaved && !saveOnCloseDialog())
                    return;
                String s;
                int x, y;
                while (true) {
                    s = (String) JOptionPane.showInputDialog(
                            MainWindow.this,
                            "Введите размер поля (минимум: 15x15):",
                            "100x100");
                    if (s == null) break;
                    if (s.isEmpty()) continue;
                    if (!s.contains("x")) continue;
                    x = Integer.parseInt(s.substring(0, s.indexOf("x")));
                    if (x < 15) continue;
                    y = Integer.parseInt(s.substring(s.indexOf("x") + 1));
                    if (y < 15) continue;
                    if (jpField == null)
                        jpField = new Field(x, y);
                    else
                        jpField.newField(x, y);
                    jlFieldSizeInfo.setText(x + "x" + y);
                    add(jpField, BorderLayout.CENTER);
                    jpField.initField();
                    jlMapSizeInfo.setText(jpField.getMapSize());
                    fileSaved = true;
                    jmBrush.setEnabled(true);
                    jmView.setEnabled(true);
                    setTitle(WINDOW_TITLE + " - Новая карта");
                    revalidate();
                    break;
                }
            }
        });
        jmFile.add(jmiCreate);

        jmiOpen = new JMenuItem("Открыть существующую карту");
        jmiOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        jmiOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!fileSaved && !saveOnCloseDialog())
                    return;
                if (openFile()) {
                    add(jpField, BorderLayout.CENTER);
                    setTitle(WINDOW_TITLE + " - " + file.toString());
                    jmBrush.setEnabled(true);
                    jmView.setEnabled(true);
                }
            }
        });
        jmFile.add(jmiOpen);

        jmFile.add(jmiSave);

        jmFile.addSeparator();

        jmiExit = new JMenuItem("Выход");
        jmiExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!fileSaved && !saveOnCloseDialog())
                    return;
                System.exit(0);
            }
        });
        jmFile.add(jmiExit);

                                                                                                                        /** MENU: EDIT **/

        jmEdit = new JMenu("Правка");
        jmbMenu.add(jmEdit);
        jmiUndo = new JMenuItem("Отменить");
        jmiUndo.setEnabled(false);
        jmiUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        jmiUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jpField.undo();
            }
        });
        jmEdit.add(jmiUndo);

        jmiRedo = new JMenuItem("Повторить");
        jmiRedo.setEnabled(false);
        jmiRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        jmiRedo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jpField.redo();
            }
        });
        jmEdit.add(jmiRedo);

                                                                                                                        /** MENU: BRUSHES **/

        URL imageUrl = this.getClass().getResource(".\\src\\ru\\burningGlobe\\generator\\images\\brush.png");
        if (imageUrl != null) {
            jmBrush.setText("");
            jmBrush.setIcon(new ImageIcon(imageUrl));
        }
        jmBrush.setEnabled(false);
        jmbMenu.add(jmBrush);

                                                                                                                        /** ALL TYPES OF BRUSHES **/

        jmiBrushRoad = new JMenuItem("Дорога");
        jmiBrushRoad.setBackground(GeneratorColors.roadColor);
        jmiBrushRoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jpField.setCurrentBrush(1);
            }
        });
        jmBrush.add(jmiBrushRoad);

        jmiBrushDesert = new JMenuItem("Пустыня");
        jmiBrushDesert.setBackground(GeneratorColors.desertColor);
        jmiBrushDesert.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jpField.setCurrentBrush(2);
            }
        });
        jmBrush.add(jmiBrushDesert);

        jmiBrushStone = new JMenuItem("Камень");
        jmiBrushStone.setBackground(GeneratorColors.stoneColor);
        jmiBrushStone.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jpField.setCurrentBrush(3);
            }
        });
        jmBrush.add(jmiBrushStone);

        jmiBrushRb = new JMenuItem("Рубидий");
        jmiBrushRb.setBackground(GeneratorColors.rbColor);
        jmiBrushRb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jpField.setCurrentBrush(4);
            }
        });
        jmBrush.add(jmiBrushRb);

        jmiBrushNexus = new JMenuItem("Нексус");
        jmiBrushNexus.setBackground(GeneratorColors.nexusColor);
        jmiBrushNexus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jpField.setCurrentBrush(5);
            }
        });
        jmBrush.add(jmiBrushNexus);

        jmBrush.addSeparator();

        jmiBrushMapBorder = new JMenuItem("Граница карты");
        jmiBrushMapBorder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jpField.setCurrentBrush(6);
            }
        });
        jmBrush.add(jmiBrushMapBorder);

        jmBrush.addSeparator();

        jmiBrushEraser = new JMenuItem("Ластик");
        jmiBrushEraser.setBackground(GeneratorColors.eraserColor);
        jmiBrushEraser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jpField.setCurrentBrush(-1);
            }
        });
        jmBrush.add(jmiBrushEraser);

                                                                                                                        /** MENU: VIEW **/

        jmView.setEnabled(false);
        jmbMenu.add(jmView);
        jmiZoomIn = new JMenuItem("Приблизить");
        jmiZoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_MASK));
        jmiZoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jpField.zoom(0.5f);
            }
        });
        jmView.add(jmiZoomIn);

        jmiZoomOut = new JMenuItem("Отдалить");
        jmiZoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_MASK));
        jmiZoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jpField.zoom(-0.5f);
            }
        });
        jmView.add(jmiZoomOut);

        setVisible(true);
    }

    static void setFileAsUnsaved() {fileSaved = false;}

    static void setMapSizeInfo(String info) {
        if (jlMapSizeInfo != null)
            jlMapSizeInfo.setText(info);
    }

    static void setLineInfo(String info) {
        if (jlPositionInfo != null)
            jlPositionInfo.setText(info);
    }

    static void setUndoEnabled(Boolean status) {
        if (jmiUndo != null)
            jmiUndo.setEnabled(status);
    }

    static void setRedoEnabled(Boolean status) {
        if (jmiRedo != null)
            jmiRedo.setEnabled(status);
    }


    @NotNull private Boolean saveFile() {
        if (fileSaved)
            return true;
        if (file == null) {
            JFileChooser fileChooser;
            FileFilter filter;
            int returnVal;

            fileChooser = new JFileChooser();
            filter = new FileNameExtensionFilter("TD Map file", "tdmap");
            fileChooser.setFileFilter(filter);
            returnVal = fileChooser.showSaveDialog(MainWindow.this);

            if (returnVal == JFileChooser.APPROVE_OPTION)
                file = fileChooser.getSelectedFile();
            else if (returnVal == JFileChooser.CANCEL_OPTION)
                return false;
        }
        try {
            FileWriter fw;
            StringBuilder sb;
            String filename;

            filename = file.toString();
            if (!filename.contains(".tdmap"))
                filename += ".tdmap";
            fw = new FileWriter(filename);
            sb = new StringBuilder();
            sb.append(jpField.getXLinesCount()).append(" ").append(jpField.getYLinesCount()).append("\r\n");
            int[][] mapArray = jpField.getField();
            for (int i = 0; i < jpField.getYLinesCount(); i++) {
                for (int j = 0; j < jpField.getXLinesCount(); j++) {
                    sb.append(mapArray[i][j]).append(" ");
                }
                sb.append("\r\n");
            }
            sb.append(jpField.getMapStartPoint().x).append(" ").append(jpField.getMapStartPoint().y).append("\r\n");
            sb.append(jpField.getMapEndPoint().x).append(" ").append(jpField.getMapEndPoint().y).append("\r\n");
            fw.write(sb.toString());
            fw.close();
            fileSaved = true;
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }
        return true;
    }

    private boolean openFile() {
        try {
            JFileChooser fileChooser;
            FileFilter filter;
            int returnVal;

            fileChooser = new JFileChooser();
            filter = new FileNameExtensionFilter("TD Map file","tdmap");
            fileChooser.setFileFilter(filter);
            returnVal = fileChooser.showOpenDialog(MainWindow.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                fileSaved = false;
            }
            else if (returnVal == JFileChooser.CANCEL_OPTION) {
                return false;
            }

            FileReader fr;
            int fieldSizeX, fieldSizeY;
            StringBuilder fileData;
            String sFile, s;
            String[] lineElements;
            int[][] field;

            fr = new FileReader(file);
            fileData = new StringBuilder();
            int symbol;
            while ((symbol = fr.read()) != -1) {
                fileData.append((char)symbol);
            }
            fr.close();

            sFile = fileData.toString();
            lineElements = sFile.substring(0, sFile.indexOf("\r\n")).split(" ");
            fieldSizeX = Integer.parseInt(lineElements[0]);
            fieldSizeY = Integer.parseInt(lineElements[1]);
            field = new int[fieldSizeY][fieldSizeX];
            for (int i = 0; i < fieldSizeY; i++) {
                sFile = sFile.substring(sFile.indexOf("\n") + 1);
                lineElements = sFile.substring(0, sFile.indexOf("\r\n")).split(" ");
                for (int j = 0; j < fieldSizeX; j++) {
                    field[i][j] = Integer.parseInt(lineElements[j]);
                }
            }
            if (jpField == null)
                jpField = new Field(fieldSizeX, fieldSizeY);
            else
                jpField.newField(fieldSizeX, fieldSizeY);
            jlFieldSizeInfo.setText(fieldSizeX + "x" + fieldSizeY);
            jpField.initField(field);
            sFile = sFile.substring(sFile.indexOf("\n") + 1);
            int nextElement = sFile.indexOf("\r\n");
            if (nextElement != -1) {
                lineElements = sFile.substring(0, nextElement).split(" ");
                int x1, y1, x2, y2;
                x1 = Integer.parseInt(lineElements[0]);
                y1 = Integer.parseInt(lineElements[1]);
                sFile = sFile.substring(sFile.indexOf("\n") + 1);
                nextElement = sFile.indexOf("\r\n");
                if (nextElement != -1) {
                    lineElements = sFile.substring(0, nextElement).split(" ");
                    x2 = Integer.parseInt(lineElements[0]);
                    y2 = Integer.parseInt(lineElements[1]);
                    jpField.setMapSize(new Point(x1, y1), new Point(x2, y2));
                }
                else {
                    jpField.setMapSize(new Point(x1, y1), null);
                }
            }
            fileSaved = true;
            revalidate();
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Boolean saveOnCloseDialog() {
        int returnVal;
        returnVal = JOptionPane.showConfirmDialog(
                MainWindow.this,
                "Сохранить карту?",
                "Сохранение",
                JOptionPane.YES_NO_OPTION
        );
        if (returnVal == JOptionPane.YES_OPTION) {
            fileSaved = saveFile();
        }
        else if (returnVal == JOptionPane.NO_OPTION) {
            fileSaved = true;
        }
        return fileSaved;
    }

    private boolean saveDialog() {
        if (file == null && fileSaved) {
            JOptionPane.showMessageDialog(MainWindow.this, "Карта еще не открыта");
            return false;
        }
        return true;
    }
}
