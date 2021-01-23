package main ;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class JavaFxApplication extends Application {

    public static Stage mainStage ;

    // private final static Logger LOGGER = Logger.getLogger(MyLogger.class.getName());
    // Logger logger = Logger.getLogger(JavaFxApplication.class.getName());

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


    public static void main(String[] args) throws IOException {



//        logger.severe("severe message");
//
//        logger.warning("warning message");
//
//        logger.info("info message");
//
//        logger.config("config message");
//
//        logger.fine("fine message");
//
//        logger.finer("finer message");
//
//        logger.finest("finest message");


        launch();
    }
}
