package launcher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {

    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setMaxWidth(640);
        primaryStage.setMaxHeight(480);
        primaryStage.initStyle(StageStyle.UNDECORATED);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainWindow.fxml"));
        Parent root = loader.load();

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        Controller controller = (Controller) loader.getController();
        controller.setMainStage(primaryStage);
        primaryStage.setTitle("R6Siege Launcher");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/icon.png")));
        Scene scene = new Scene(root, 640, 480);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("resources/theme.css").toString());
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) throws IOException {
        launch(args);

    }

}

