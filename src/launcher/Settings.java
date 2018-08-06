package launcher;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by mkope on 04-Aug-18.
 */
public class Settings {

    private static Settings instance = null;
    private static String gamePath;
    private static String settingsPath;
    private File settingsFile;
    private Controller myController;
    private boolean isAfterSetup = false;

    private Settings(Stage mainStage, Controller myController) throws IOException {

        this.myController = myController;

        settingsFile = new File("settings.ini");
        if (settingsFile.createNewFile()) {
            myController.firstTimeSetup();
            isAfterSetup = true;
        } else {
            readSettings(settingsFile);
        }
    }

    public void changeSettings(String gamePath, String settingsPath) throws IOException {
        FileWriter writer = new FileWriter(settingsFile);

        setGamePath(gamePath);
        writer.write("GamePath = '" + getGamePath() + "'\n");

        setSettingsPath(settingsPath);
        writer.write("SettingsPath = '" + getSettingsPath() + "'");

        writer.close();
    }

    public void readSettings(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String temp;
        while (scanner.hasNextLine()) {
            temp = scanner.nextLine();
            if (temp.contains("GamePath")) {
                setGamePath(temp.substring(temp.indexOf("'") + 1, temp.length() - 1));
            } else if (temp.contains("SettingsPath")) {
                setSettingsPath(temp.substring(temp.indexOf("'") + 1, temp.length() - 1));
            }
        }
    }

    public String getGamePath() {
        return gamePath;
    }

    public void setGamePath(String gamePath) {
        this.gamePath = gamePath;
    }

    public String getSettingsPath() {
        return settingsPath;
    }

    public void setSettingsPath(String settingsPath) {
        this.settingsPath = settingsPath;
    }

    public boolean isAfterSetup() {
        return isAfterSetup;
    }

    public static Settings getInstance(Stage mainStage, Controller myController) throws IOException {
        if (instance == null) {
            instance = new Settings(mainStage, myController);
        }
        return instance;
    }

    public File openFileChooser(int option, Stage mainStage){

        FileChooser fileChooser = new FileChooser();

        switch (option){
            case 0:
                fileChooser.setTitle("Find game executable");
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Siege executable", "RainbowSix.exe"));
                return fileChooser.showOpenDialog(mainStage);
            case 1:
                fileChooser.setTitle("Find game settings");
                fileChooser.getExtensionFilters().clear();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Siege settings file", "GameSettings.ini"));
                fileChooser.setInitialDirectory(new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\my games\\Rainbow Six - Siege"));
                return fileChooser.showOpenDialog(mainStage);
        }
        return new File("");
    }

    public void deleteSettingsFile(){
        if(settingsFile.exists()) {
            settingsFile.delete();
        }
    }
}
