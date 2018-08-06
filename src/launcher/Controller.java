package launcher;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;


public class Controller {

    public ImageView siegeLogo;
    public Label statusLabel;
    public ComboBox<Servers> comboBox;
    public Button playBtn;
    public BorderPane backgroundPane;
    public ImageView closeImage;
    public ImageView settingsImage;
    public HBox playHbox;
    public HBox choiceHBox;
    public AnchorPane settingsShadowPane;
    public BorderPane settingsPane;
    public TextField gamePathTextField;
    public TextField settingsPathTextField;
    public Label setupSettingsLabel;
    public TextField setupSettingsTextField;
    public TextField setupGameTextField;
    public AnchorPane setupShadowPane;
    public BorderPane setupPane;
    public Button setupSettingsBrowseBtn;
    public Button continueBtn;
    public Label summaryLabel;

    public ImageView gameError;
    public ImageView settingsError;
    public Label gameErrorLabel;
    public Label settingsErrorLabel;


    private Stage mainStage;
    private Settings settings;

    private BooleanProperty isGameRunning;
    private BooleanProperty arePathsSet;
    ObservableList<Servers> serverList;

    public void initialize() throws FileNotFoundException {


        try {
            settings = Settings.getInstance(mainStage, this);
            if(settings.getGamePath() == null && !settings.isAfterSetup()){
                playBtn.setDisable(true);
                toggleErrorIcons(0,true);
            }

            if(settings.getSettingsPath() == null && !settings.isAfterSetup()){
                comboBox.setDisable(true);
                toggleErrorIcons(1, true);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        settingsShadowPane.setBackground(new Background(new BackgroundFill(Color.web("#000000",0.60), CornerRadii.EMPTY, Insets.EMPTY)));
        settingsPane.setBackground(new Background(new BackgroundFill(Color.web("#001A23"),CornerRadii.EMPTY,Insets.EMPTY)));

        setupShadowPane.setBackground(new Background(new BackgroundFill(Color.web("#000000",0.60), CornerRadii.EMPTY, Insets.EMPTY)));
        setupPane.setBackground(new Background(new BackgroundFill(Color.web("#001A23"),CornerRadii.EMPTY,Insets.EMPTY)));

        siegeLogo.setImage(new Image(getClass().getResourceAsStream("/resources/siegeLogo.png")));
        backgroundPane.setBackground(new Background(new BackgroundFill(Color.web("#001A23"), CornerRadii.EMPTY, Insets.EMPTY)));
        closeImage.setImage(new Image(getClass().getResourceAsStream("/resources/close.png")));
        settingsImage.setImage(new Image(getClass().getResourceAsStream("/resources/settings.png")));

        isGameRunning = new SimpleBooleanProperty(false);
        isGameRunning.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                statusLabel.getStyleClass().add("running");
                statusLabel.setText("Running");
            } else {
                statusLabel.getStyleClass().add("stopped");
                statusLabel.setText("Stopped");
            }
        });

        arePathsSet = new SimpleBooleanProperty(false);
        arePathsSet.addListener((observable, oldValue, newValue) -> {
            if(newValue){
                continueBtn.setDisable(false);
                continueBtn.setVisible(true);
                summaryLabel.setVisible(true);
            }else{
                continueBtn.setVisible(false);
                continueBtn.setDisable(true);
                summaryLabel.setVisible(false);
            }
        });

        serverList = FXCollections.observableArrayList(
                new Servers("Ping Based", "default"),
                new Servers("United States East", "eus"),
                new Servers("United States Central", "cus"),
                new Servers("United States South Central", "scus"),
                new Servers("United States West", "wus"),
                new Servers("Brazil South", "sbr"),
                new Servers("Europe North", "neu"),
                new Servers("Europe West", "weu"),
                new Servers("Asia East", "eas"),
                new Servers("Asia South East", "seas"),
                new Servers("Australia East", "eau"),
                new Servers("Japan West", "wja")
        );

        comboBox.setItems(serverList);
        comboBox.setConverter(new StringConverter<Servers>() {
            @Override
            public String toString(Servers object) {
                return object.getName();
            }

            @Override
            public Servers fromString(String string) {
                return null;
            }
        });

        for (Servers iterator : serverList) {
            if (iterator.getServer().equals(getServerProperty())) {
                comboBox.getSelectionModel().select(iterator);
            }
        }

        comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            try {
                setServerProperty(newValue.getServer());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });

        Thread t = new Thread(() -> {
            while (true) {
                try {
                    String line;
                    AtomicBoolean isRunning = new AtomicBoolean(false);

                    Process p = Runtime.getRuntime().exec
                            (System.getenv("windir") + "\\system32\\" + "tasklist.exe");

                    BufferedReader input =
                            new BufferedReader(new InputStreamReader(p.getInputStream()));

                    while ((line = input.readLine()) != null) {
                        if (line.contains("RainbowSix_BE.exe")) {
                            isRunning.set(true);
                            break;
                        }
                    }
                    input.close();

                    Platform.runLater(() -> setIsGameRunning(isRunning.get()));
                } catch (Exception err) {
                    err.printStackTrace();
                }

                try {
                    Thread.currentThread().sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    public void playBtnReleased(MouseEvent mouseEvent) throws IOException {

        if (settings.getGamePath() != null && settings.getGamePath().contains("RainbowSix.exe")) {
            Process process = new ProcessBuilder(settings.getGamePath()).start();
            System.exit(0);
        }
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    private void setServerProperty(String newServer) throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(settings.getSettingsPath()));
        String oldFile = "";
        String oldServer = "";
        try {
            String line = reader.readLine();
            while (line != null) {
                if (line.contains("DataCenterHint=")) {
                    oldServer = line;
                }
                oldFile += line + System.lineSeparator();
                line = reader.readLine();
            }
            reader.close();

            String newFile = oldFile.replaceAll(oldServer, "DataCenterHint=" + newServer);

            FileWriter writer = new FileWriter(settings.getSettingsPath());
            writer.write(newFile);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getServerProperty() throws FileNotFoundException {
        String temp = "Path not set!";

        if(settings.getSettingsPath() != null) {
            File gameSettings = new File(settings.getSettingsPath());
            Scanner scan = new Scanner(gameSettings);


            while (scan.hasNextLine()) {
                temp = scan.nextLine();
                if (temp.contains("DataCenterHint=")) {
                    temp = temp.substring(temp.indexOf("=") + 1, temp.length());
                    return temp;
                }
            }
        }

        return temp;
    }

    public void settingsImageEnter(MouseEvent mouseEvent) {
        settingsImage.setOpacity(1.0);
    }

    public void settingsImageExit(MouseEvent mouseEvent) {
        settingsImage.setOpacity(0.5);
    }

    public void settingsImageReleased(MouseEvent mouseEvent) {

        gamePathTextField.setText(settings.getGamePath());
        settingsPathTextField.setText(settings.getSettingsPath());

        settingsShadowPane.setDisable(false);
        settingsShadowPane.setVisible(true);

    }

    public void closeImageReleased(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void closeImageEnter(MouseEvent mouseEvent) {
        closeImage.setOpacity(1.0);
    }

    public void closeImageExit(MouseEvent mouseEvent) {
        closeImage.setOpacity(0.5);
    }

    public void setIsGameRunning(boolean isRunning) {
        this.isGameRunning.setValue(isRunning);
    }

    private void toggleErrorIcons(int option, boolean toggle){
        switch (option){
            case 0:
                if(gameError == null) {
                    gameError = new ImageView();
                    gameError.setImage(new Image(getClass().getResourceAsStream("/resources/error.png")));
                    gameError.setOpacity(0.6);
                    gameError.setPreserveRatio(true);
                    gameError.setFitHeight(15);

                    gameErrorLabel = new Label();
                    gameErrorLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    gameErrorLabel.setGraphic(gameError);
                    gameErrorLabel.setTooltip(new Tooltip("Game Path not set!"));

                    playHbox.getChildren().add(gameErrorLabel);
                }else{
                    gameError.setVisible(toggle);
                    gameErrorLabel.setVisible(toggle);
                }
                break;
            case 1:
                if(settingsError == null){
                    settingsError = new ImageView();
                    settingsError.setImage(new Image(getClass().getResourceAsStream("/resources/error.png")));
                    settingsError.setOpacity(0.6);
                    settingsError.setPreserveRatio(true);
                    settingsError.setFitHeight(15);

                    settingsErrorLabel = new Label();
                    settingsErrorLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    settingsErrorLabel.setGraphic(settingsError);
                    settingsErrorLabel.setTooltip(new Tooltip("Settings Path not set!"));

                    choiceHBox.getChildren().add(settingsErrorLabel);
                }else{
                    settingsError.setVisible(toggle);
                    settingsErrorLabel.setVisible(toggle);
                }
                break;
        }
    }

    public void settingsBackReleased(MouseEvent mouseEvent) {
        settingsShadowPane.setVisible(false);
        settingsShadowPane.setDisable(true);
    }

    public void gamePathBrowseReleased(MouseEvent mouseEvent) {
        gamePathTextField.setText(settings.openFileChooser(0, mainStage).getAbsolutePath());
    }

    public void settingsPathBrowseReleased(MouseEvent mouseEvent) {
        settingsPathTextField.setText(settings.openFileChooser(1, mainStage).getAbsolutePath());
    }

    public void settingsOkReleased(MouseEvent mouseEvent) throws IOException {
        String gamePath = gamePathTextField.getText();
        String settingsPath = settingsPathTextField.getText();

        if(!gamePath.isEmpty() && !settingsPath.isEmpty()) {
            settings.changeSettings(gamePath, settingsPath);
        }

        settingsShadowPane.setVisible(false);
        settingsShadowPane.setDisable(true);
    }

    public void settingsApplyReleased(MouseEvent mouseEvent) throws IOException {
        String gamePath = gamePathTextField.getText();
        String settingsPath = settingsPathTextField.getText();

        if(!gamePath.isEmpty() && !settingsPath.isEmpty()) {
            settings.changeSettings(gamePath, settingsPath);
        }
    }

    public void continueBtnReleased(MouseEvent mouseEvent) throws IOException {
        settings.changeSettings(setupGameTextField.getText(), setupSettingsTextField.getText());

        for (Servers iterator : serverList) {
            if (iterator.getServer().equals(getServerProperty())) {
                comboBox.getSelectionModel().select(iterator);
            }
        }

        setupShadowPane.setVisible(false);
        setupShadowPane.setDisable(true);
    }

    public void setupGameBrowseReleased(MouseEvent mouseEvent) {
        File gameFile = settings.openFileChooser(0, mainStage);
        if(gameFile != null) {
            setupGameTextField.setText(gameFile.getAbsolutePath());
            if(setupSettingsTextField.getText().contains("GameSettings.ini")){
                arePathsSet.setValue(true);
            }
        }
    }

    public void setupSettingsBrowseReleased(MouseEvent mouseEvent) {
        File settingsFile = settings.openFileChooser(1, mainStage);
        if(settingsFile != null) {
            setupSettingsTextField.setText(settingsFile.getAbsolutePath());
            if(setupGameTextField.getText().contains("RainbowSix.exe")){
                arePathsSet.setValue(true);
            }
        }
    }

    public void firstTimeSetup(){

        setupShadowPane.setDisable(false);
        setupShadowPane.setVisible(true);

        File selectedDirectory = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getPath() + "\\my games\\Rainbow Six - Siege");
        if (selectedDirectory != null) {
            String[] randomFolderList = selectedDirectory.list();
            if (randomFolderList.length == 1) {
                setupSettingsLabel.setText("I have found the Game Settings file");
                setupSettingsTextField.setText(selectedDirectory + "\\" + randomFolderList[0] + "\\GameSettings.ini");
                if(setupGameTextField.getText().contains("RainbowSix.exe")){
                    arePathsSet.setValue(true);
                }
            } else {
                setupSettingsLabel.setText("Please find the Game Settings file");
                setupSettingsBrowseBtn.setDisable(false);
                setupSettingsBrowseBtn.setVisible(true);
            }
        }
    }

    public void cancelBtnRealeased(MouseEvent mouseEvent) {
        settings.deleteSettingsFile();
        System.exit(0);
    }
}
