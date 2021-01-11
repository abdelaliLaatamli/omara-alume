package main ;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.InputStream;


public class JavaFxApplication extends Application {

    public static Stage mainStage ;

    @Override
    public void start(Stage stage) throws Exception {

        mainStage = stage;
        mainStage.setResizable(false);

        Image image = new Image(String.valueOf(getClass().getClassLoader().getResource("icon.jpg")));
        mainStage.getIcons().add(image);

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/MainView.fxml"));
        mainStage.setScene(new Scene(root));
        mainStage.setTitle("Home -- Aluminium et verre");
        mainStage.show();

    }


    public static void main(String[] args) {
       launch();
    }
}
