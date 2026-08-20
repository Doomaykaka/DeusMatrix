package deusmatrix.utils;

import deusmatrix.models.Statistic;
import deusmatrix.models.User;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class SupportFunctions {
    public static User createEmptyUser(String name) {
        Date creationDate = Date.from(Instant.now());
        Long level = 1L;
        Long experience = 0L;
        Long experienceToNextLevel = 100L;
        Statistic statistic = createEmptyStatistic();

        return new User(name, creationDate, level, experience, experienceToNextLevel, statistic);
    }

    public static Statistic createEmptyStatistic() {
        Date lastPlayDate = Date.from(Instant.now());
        Long daysInGame = 1L;
        Long easyWins = 0L;
        Long middleWins = 0L;
        Long hardWins = 0L;
        Long easyBestTime = 0L;
        Long middleBestTime = 0L;
        Long hardBestTime = 0L;
        Long easyLose = 0L;
        Long middleLose = 0L;
        Long hardLose = 0L;

        return new Statistic(
                lastPlayDate,
                daysInGame,
                easyWins,
                middleWins,
                hardWins,
                easyBestTime,
                middleBestTime,
                hardBestTime,
                easyLose,
                middleLose,
                hardLose);
    }

    public static boolean writeContentInNewFile(File folderToSave, String name, List<String> content) {
        try {
            if (folderToSave == null || name == null || content == null) {
                return false;
            }

            Path file = folderToSave.toPath().resolve(name).normalize();
            String value = String.join(System.lineSeparator(), content) + System.lineSeparator();
            Files.writeString(
                    file,
                    value,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.WRITE);
            return true;
        } catch (IOException | RuntimeException e) {
            Logger.getInstance().warning("Can't save file: " + e.getMessage());
            return false;
        }
    }

    public static List<String> readFileContent(FileReader file) throws FileNotFoundException {
        List<String> fileContent = new ArrayList<>();

        Scanner scanner = new Scanner(file);

        while (scanner.hasNextLine()) {
            fileContent.add(scanner.nextLine());
        }

        scanner.close();

        return fileContent;
    }

    public static Rectangle getWindowBounds(int width, int height) {
        Rectangle bounds = null;

        Rectangle screenSize = getScreenSize();
        int xCenter = (int) (screenSize.getWidth() / 2);
        int yCenter = (int) (screenSize.getHeight() / 2);

        int windowX = xCenter - width / 2;
        int windowY = yCenter - height / 2;

        bounds = new Rectangle(windowX, windowY, width, height);

        return bounds;
    }

    public static Rectangle getScreenSize() {
        Rectangle screenSize = null;

        Dimension screenDimension = Toolkit.getDefaultToolkit().getScreenSize();
        screenSize = new Rectangle((int) screenDimension.getWidth(), (int) screenDimension.getHeight());

        return screenSize;
    }

    public static Image getAppIcon() {
        Image appIcon = null;

        URL appIconUrl = SupportFunctions.class.getResource("/deusmatrix/images/app_icon.jpg");
        appIcon = Toolkit.getDefaultToolkit().getImage(appIconUrl);

        return appIcon;
    }

    public static ImageIcon getResourceImage(String name) {
        String path = "deusmatrix/images/" + name;
        URL resourceURL = SupportFunctions.class.getClassLoader().getResource(path);

        return new ImageIcon(resourceURL);
    }

    public static JPanel getEntityWindowCheckbox(String label) {
        JPanel result = null;

        result = new JPanel();
        BoxLayout layout = new BoxLayout(result, BoxLayout.X_AXIS);
        result.setLayout(layout);

        JCheckBox input = new JCheckBox();

        result.add(new JLabel(label));
        result.add(input);

        return result;
    }

    public static void setEntityWindowCheckboxValue(JPanel panel, boolean value) {
        int checkboxIndex = 1;

        JCheckBox checkbox = (JCheckBox) panel.getComponent(checkboxIndex);
        checkbox.getModel().setSelected(value);
    }

    public static boolean getEntityWindowCheckboxValue(JPanel panel) {
        int checkboxIndex = 1;

        JCheckBox checkbox = (JCheckBox) panel.getComponent(checkboxIndex);
        return checkbox.isSelected();
    }

    public static void addButtonWithGap(JPanel panel, JButton button, int gap) {
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(gap, gap)));
    }

    public static void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void addChildPanelWithGap(JPanel panel, JPanel child, int gap) {
        panel.add(child);
        panel.add(Box.createRigidArea(new Dimension(gap, gap)));
    }

    public static File chooseFile() {
        File selectedFile = null;

        JFileChooser fileChooser = new JFileChooser();
        int state = fileChooser.showOpenDialog(null);

        if (state == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
        }

        return selectedFile;
    }

    public static File saveFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION ? fileChooser.getSelectedFile() : null;
    }

    public static MouseListener getOnClickListener(Runnable callback) {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                callback.run();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                return;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                return;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                return;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                return;
            }
        };
    }
}
