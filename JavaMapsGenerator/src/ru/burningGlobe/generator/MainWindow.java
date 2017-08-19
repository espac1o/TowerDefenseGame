package ru.burningGlobe.generator;

import com.sun.istack.internal.NotNull;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

/**
 ** Created by espacio on 19.09.2016.
 **/

class MainWindow extends JFrame {
    static final int WINDOW_X = 800; // actually, 805
    static final int WINDOW_Y = 600; // actually, 687
    private static final String WINDOW_TITLE = "TD Maps Generator";
    private static boolean fileSaved = true;
    private static JMenuItem jmiUndo, jmiRedo;
    private static JLabel jlPositionInfo, jlCurrentBrushColorTypeInfo, jlMapSizeInfo, jlFieldSizeInfo;
    Font statusBarFont = new Font("Century Gothic", Font.PLAIN, 12);
    private File file;
    private JMenu jmFile, jmRecent;


    private Field jpField;

    private ArrayList<String[]> recentFiles;

    MainWindow() {
        JMenuBar jmbMenu;
        JMenu jmEdit, jmTools, jmView;
        JMenuItem jmiCreate, jmiOpen, jmiSave, jmiExit;
        JMenuItem jmiGenerateRoute;
        JMenuItem jmiZoomIn, jmiZoomOut;
        JPanel jpStatusBar;
        JLabel jlPositionFlatText, jlCurrentBrushColorTypeFlatText, jlMapSizeFlatText, jlFieldSizeFlatText;
        JToolBar jtbBrushes;

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
                else {
                    saveRecentFilesList();
                    System.exit(0);
                }
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

        recentFiles = openRecentFilesList();

        jtbBrushes = new JToolBar();
        jtbBrushes.setOrientation(SwingConstants.VERTICAL);
        jtbBrushes.setPreferredSize(new Dimension(125, 125));
        add(jtbBrushes, BorderLayout.LINE_END);

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
        jlCurrentBrushColorTypeFlatText = new JLabel("Текущая кисть: ");
        jlCurrentBrushColorTypeFlatText.setFont(statusBarFont);
        jlCurrentBrushColorTypeInfo = new JLabel("");
        jlCurrentBrushColorTypeInfo.setPreferredSize(new Dimension(70, 16));
        jlCurrentBrushColorTypeInfo.setBorder(BorderFactory.createLineBorder(GeneratorColors.statusBarBorderColor));
        jlCurrentBrushColorTypeInfo.setFont(statusBarFont);
        jlCurrentBrushColorTypeInfo.setHorizontalTextPosition(JLabel.CENTER);
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
                    s = JOptionPane.showInputDialog(
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
        jpStatusBar.add(jlCurrentBrushColorTypeFlatText);
        jpStatusBar.add(jlCurrentBrushColorTypeInfo);
        jpStatusBar.add(jlMapSizeFlatText);
        jpStatusBar.add(jlMapSizeInfo);
        jpStatusBar.add(jlFieldSizeFlatText);
        jpStatusBar.add(jlFieldSizeInfo);

        add(jpStatusBar, BorderLayout.SOUTH);

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
        jmFile.add(jmiSave);

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
                    s = JOptionPane.showInputDialog(
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
                    jmView.setEnabled(true);
                    file = null;
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
                    setTitle(WINDOW_TITLE + " - " + file.getAbsolutePath());
                    jmView.setEnabled(true);
                }
            }
        });
        jmFile.add(jmiOpen);

        jmFile.addSeparator();

        updateRecentFilesMenu();
        jmFile.add(jmRecent);

        jmFile.addSeparator();

        jmiExit = new JMenuItem("Выход");
        jmiExit.addActionListener(e -> {
            if (!fileSaved && !saveOnCloseDialog())
                return;
            saveRecentFilesList();
            System.exit(0);
        });
        jmFile.add(jmiExit);

                                                                                                                        /** MENU: EDIT **/

        jmEdit = new JMenu("Правка");
        jmbMenu.add(jmEdit);
        jmiUndo = new JMenuItem("Отменить");
        jmiUndo.setEnabled(false);
        jmiUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
        jmiUndo.addActionListener(e -> jpField.undo());
        jmEdit.add(jmiUndo);

        jmiRedo = new JMenuItem("Повторить");
        jmiRedo.setEnabled(false);
        jmiRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
        jmiRedo.addActionListener(e -> jpField.redo());
        jmEdit.add(jmiRedo);

        /** MENU: TOOLS **/

        jmTools = new JMenu("Инструменты");
        jmbMenu.add(jmTools);
        jmiGenerateRoute = new JMenuItem("Сгенерировать путь");
        jmiGenerateRoute.addActionListener(e -> {
            if (jpField != null) {
                jpField.generateRoute();
                jpField.repaint();
            } else {
                System.out.println("Поле еще не создано");
            }
        });
        jmTools.add(jmiGenerateRoute);

                                                                                                                        /** MENU: VIEW **/

        jmView.setEnabled(false);
        jmbMenu.add(jmView);
        jmiZoomIn = new JMenuItem("Приблизить");
        jmiZoomIn.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, InputEvent.CTRL_MASK));
        jmiZoomIn.addActionListener(e -> jpField.zoom(0.5f));
        jmView.add(jmiZoomIn);

        jmiZoomOut = new JMenuItem("Отдалить");
        jmiZoomOut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_MASK));
        jmiZoomOut.addActionListener(e -> jpField.zoom(-0.5f));
        jmView.add(jmiZoomOut);

        /** ALL TYPES OF BRUSHES **/

        jtbBrushes.add(createBrushMenuItem("Дорога", BrushID.ROAD));
        jtbBrushes.add(createBrushMenuItem("Пустыня", BrushID.DESERT, GeneratorColors.menuItemTitledBorderBlackTextColor));
        jtbBrushes.add(createBrushMenuItem("Камень", BrushID.STONE, GeneratorColors.menuItemTitledBorderBlackTextColor));
        jtbBrushes.add(createBrushMenuItem("Вода", BrushID.WATER));
        jtbBrushes.add(createBrushMenuItem("Подземелье", BrushID.UNDERGROUND));
        jtbBrushes.add(createBrushMenuItem("Рубидий", BrushID.RUBIDIUM));
        jtbBrushes.add(createBrushMenuItem("Установка башни", BrushID.TOWER));

        jtbBrushes.addSeparator();

        jtbBrushes.add(createBrushMenuItem("Клетка старта", BrushID.SPAWNER));
        jtbBrushes.add(createBrushMenuItem("Граница карты", BrushID.MAP_BORDER));
//        jtbBrushes.add(createBrushMenuItem("Путь", BrushID.ROUTE));
        jtbBrushes.add(createBrushMenuItem("Нексус", BrushID.NEXUS, GeneratorColors.menuItemTitledBorderBlackTextColor));

        jtbBrushes.addSeparator();

        jtbBrushes.add(createBrushMenuItem("Ластик", BrushID.ERAISER, GeneratorColors.menuItemTitledBorderBlackTextColor));

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

    private void setBrush(int brushId) {
        if (jpField != null)
            jpField.setCurrentBrush(brushId);
        if (jlCurrentBrushColorTypeInfo != null) {
            jlCurrentBrushColorTypeInfo.setText(GeneratorColors.getColorType(brushId));
            jlCurrentBrushColorTypeInfo.setBorder(BorderFactory.createLineBorder(GeneratorColors.getColorById(brushId)));
        }
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

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
                return saveFile(file);
            } else //if (returnVal == JFileChooser.CANCEL_OPTION)
                return false;
        }

        return saveFile(file);
    }

    private boolean saveFile(File newFile) {
        try {
            FileWriter fw;
            StringBuilder sb;
            String filename;
            ArrayList<Point> route;

            filename = newFile.toString();
            if (!filename.contains(".tdmap"))
                filename += ".tdmap";
            fw = new FileWriter(filename);
            sb = new StringBuilder();
            sb.append(jpField.getXLinesCount()).append(" ").append(jpField.getYLinesCount()).append(System.lineSeparator());
            sb.append(jpField.getMapStartPoint().x).append(" ").append(jpField.getMapStartPoint().y).append(System.lineSeparator());
            sb.append(jpField.getMapEndPoint().x).append(" ").append(jpField.getMapEndPoint().y).append(System.lineSeparator());
            int[][] mapArray = jpField.getField();
            for (int i = 0; i < jpField.getYLinesCount(); i++) {
                for (int j = 0; j < jpField.getXLinesCount(); j++) {
                    sb.append(mapArray[i][j]).append(" ");
                }
                sb.append(System.lineSeparator());
            }
            route = jpField.getRoute();
            final String separator = " %->% ";
            for (Point point : route) {
                if (route.indexOf(point) != 0)
                    sb.append(separator);
                sb.append(point.x).append(":").append(point.y);
            }

            sb.append(System.lineSeparator());
            fw.write(sb.toString());
            fw.close();
            fileSaved = true;
        }
        catch (IOException e1) {
            e1.printStackTrace();
        }

        String[] oldObject = null;
        if (file != null)
            oldObject = new String[]{file.getName(), file.getAbsolutePath()};
        file = newFile;
        String[] newObject = {file.getName(), file.getAbsolutePath()};
        if (oldObject == null)
            oldObject = newObject;
        int n = recentFiles.size();
        for (int i = 0; i < n; i++) {
            String[] element = recentFiles.get(i);
            if (element[0].equals(oldObject[0]) && element[1].equals(oldObject[1])) {
                recentFiles.remove(i);
                recentFiles.add(0, newObject);
                System.out.println("current file exist. Was moved up");
                break;
            }
            if (i + 1 == n) {
                recentFiles.add(0, newObject);
                System.out.println("current file was added");
            }
        }
        updateRecentFilesMenu();
        return true;
    }

    private boolean openFile() {
        JFileChooser fileChooser;
        FileFilter filter;
        int returnVal;

        fileChooser = new JFileChooser();
        filter = new FileNameExtensionFilter("TD Map file", "tdmap");
        fileChooser.setFileFilter(filter);
        returnVal = fileChooser.showOpenDialog(MainWindow.this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            fileSaved = false;
            return openFile(fileChooser.getSelectedFile());
        } else  //returnVal == JFileChooser.CANCEL_OPTION
            return false;
    }

    private boolean openFile(File newFile) {
        FileReader fr;
        int fieldSizeX, fieldSizeY;
        StringBuilder fileData;
        String sFile;
        String[] lineElements;
        int[][] field;
        ArrayList<Point> route;

        try {
            fr = new FileReader(newFile);
            fileData = new StringBuilder();
            int symbol;
            while ((symbol = fr.read()) != -1) {
                fileData.append((char) symbol);
            }
            fr.close();

            // file size
            sFile = fileData.toString();
            lineElements = sFile.substring(0, sFile.indexOf(System.lineSeparator())).split(" ");
            fieldSizeX = Integer.parseInt(lineElements[0]);
            fieldSizeY = Integer.parseInt(lineElements[1]);
            field = new int[fieldSizeY][fieldSizeX];

            if (jpField == null)
                jpField = new Field(fieldSizeX, fieldSizeY);
            else
                jpField.newField(fieldSizeX, fieldSizeY);

            fileSaved = true;

            // map size
            sFile = sFile.substring(sFile.indexOf(System.lineSeparator()) + System.lineSeparator().length());
            int nextElement = sFile.indexOf(System.lineSeparator());
            if (nextElement != -1) {
                lineElements = sFile.substring(0, nextElement).split(" ");
                int x1, y1, x2, y2;
                x1 = Integer.parseInt(lineElements[0]);
                y1 = Integer.parseInt(lineElements[1]);
                sFile = sFile.substring(sFile.indexOf(System.lineSeparator()) + System.lineSeparator().length());
                nextElement = sFile.indexOf(System.lineSeparator());
                if (nextElement != -1) {
                    lineElements = sFile.substring(0, nextElement).split(" ");
                    x2 = Integer.parseInt(lineElements[0]);
                    y2 = Integer.parseInt(lineElements[1]);
                    jpField.setMapSize(new Point(x1, y1), new Point(x2, y2));
                } else {
                    jpField.setMapSize(new Point(x1, y1), null);
                }
            }

            // map
            for (int i = 0; i < fieldSizeY; i++) {
                sFile = sFile.substring(sFile.indexOf(System.lineSeparator()) + System.lineSeparator().length());
                lineElements = sFile.substring(0, sFile.indexOf(System.lineSeparator())).split(" ");
                for (int j = 0; j < fieldSizeX; j++) {
                    field[i][j] = Integer.parseInt(lineElements[j]);
                }
            }
            jlFieldSizeInfo.setText(fieldSizeX + "x" + fieldSizeY);

            sFile = sFile.substring(sFile.indexOf(System.lineSeparator()) + System.lineSeparator().length());
            int indexOfRouteStart = sFile.indexOf(System.lineSeparator());
            if (indexOfRouteStart == -1) {
                jpField.initField(field);
            } else {
                final String separator = " %->% ";
                route = new ArrayList<>();

                String[] points = sFile.split(System.lineSeparator())[0].split(separator);
                for (String point : points) {
                    route.add(new Point(Integer.parseInt(point.split(":")[0]), Integer.parseInt(point.split(":")[1])));
                }
                jpField.initField(field, route);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        file = newFile;
        String[] object = {file.getName(), file.getAbsolutePath()};
        int n = recentFiles.size();
        for (int i = 0; i < n; i++) {
            String[] element = recentFiles.get(i);
            if (element[0].equals(object[0]) && element[1].equals(object[1])) {
                recentFiles.remove(i);
                recentFiles.add(0, object);
                System.out.println("current file exist. Was moved up");
                break;
            }
            if (i + 1 == n) {
                recentFiles.add(0, object);
                System.out.println("current file was added");
            }
        }
        updateRecentFilesMenu();
        revalidate();
        return true;
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

    /**
     * @param title                 text of TitledBorder
     * @param brushId               numeric id for brush
     * @param titledBorderTextColor special color for text in TitledBorder
     * @return a menu item that includes TitledBorder, ActionListener and bgColor
     */
    private JMenuItem createBrushMenuItem(String title, int brushId, Color titledBorderTextColor) {
        JMenuItem jMenuItem = new JMenuItem();
        jMenuItem.setBackground(GeneratorColors.getColorById(brushId));
        jMenuItem.addActionListener(e -> setBrush(brushId));
        TitledBorder titledBorder = BorderFactory.createTitledBorder(title);
        titledBorder.setTitleColor(titledBorderTextColor);
        jMenuItem.setBorder(titledBorder);
        return jMenuItem;
    }

    /**
     * analogue of createBrushMenuItem(...) that takes color for text in TitledBorder as default
     *
     * @param title   text of TitledBorder
     * @param brushId numeric id for brush
     * @return a menu item that includes TitledBorder, ActionListener and bgColor
     */
    private JMenuItem createBrushMenuItem(String title, int brushId) {
        return createBrushMenuItem(title, brushId, GeneratorColors.menuItemTitledBorderDefaultTextColor);
    }

    private void updateRecentFilesMenu() {
        jmRecent = new JMenu("Недавние карты");
        if (recentFiles == null) {
            jmRecent.setEnabled(false);
        } else {
            for (String[] element : recentFiles) {
                JMenuItem jMenuItem = new JMenuItem(element[0]);
                jMenuItem.addActionListener(e -> {
                    if (!fileSaved && !saveOnCloseDialog())
                        return;
                    if (openFile(new File(element[1]))) {
                        add(jpField, BorderLayout.CENTER);
                        setTitle(WINDOW_TITLE + " - " + file.toString());
//                        jmView.setEnabled(true);
                    } else {
                        System.out.println("Cannot open file \"" + element[1] + "\". Deleting it from \"Recent files\"\'s list");
                        recentFiles.remove(element);
                        jmRecent.remove(this);
                    }
                });
                jmRecent.add(jMenuItem);
                jmRecent.setEnabled(true);
            }
            jmFile.updateUI();
        }
    }

    private ArrayList<String[]> openRecentFilesList() {
        final String FILENAME = ".\\data\\recent_files.txt";
        ArrayList<String[]> files = new ArrayList<>();
        try {
            File file = new File(FILENAME);
            if (!file.exists()) {
                System.out.println("\"Recent files\" is not exists");
                if (file.createNewFile())
                    System.out.println("successfully created");
                else
                    System.out.println("smth went wrong");
                return null;
            }
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int x;
            while ((x = in.read()) != -1) {
                out.write(x);
            }
            in.close();
            String temp = new String(out.toByteArray());
            String[] lines = temp.split(System.lineSeparator());
            String splitter = " %filepath% ";
            for (String line : lines) {
                files.add(line.split(splitter));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR: openRecentFilesList() caught an exception ");
            return null;
        }
        return files;
    }

    private boolean saveRecentFilesList() {
        try {
            FileWriter fw;
            StringBuilder sb;
            final String FILENAME = ".\\data\\recent_files.txt";
            String splitter = " %filepath% ";

            fw = new FileWriter(FILENAME);
            sb = new StringBuilder();
            if (recentFiles == null)
                return true;
            for (String[] element : recentFiles) {
                sb.append(element[0]).append(splitter).append(element[1]).append(System.lineSeparator());
            }

            fw.write(sb.toString());
            fw.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        }
        return true;
    }
}
