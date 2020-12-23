package main ;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



public class JavaFxApplication extends Application {

    public static Stage mainStage ;

    @Override
    public void start(Stage stage) throws Exception {

        mainStage = stage;

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/MainView.fxml"));
        mainStage.setScene(new Scene(root));
        mainStage.setTitle("Home -- Aluminium et verre");
        mainStage.show();

    }


    public static void main(String[] args) {
       launch();
    }
}
