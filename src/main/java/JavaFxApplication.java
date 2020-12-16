import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class JavaFxApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        //System.out.println(this.getClass().getClassLoader().getResource("views/Main.fxml"));


        Parent root = FXMLLoader.load(this.getClass().getResource("views/Main.fxml"));
        stage.setTitle("Hello World");
        stage.setScene(new Scene(root));
        stage.show();


        /*
        String javaVersion = System.getProperty("java.version");
        String javafxVersion = System.getProperty("javafx.version");
        Label l = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        Scene scene = new Scene(new StackPane(l), 640, 480);
        stage.setScene(scene);
        stage.show();
        */
        //System.out.println(getClass().getResource("/Main.fxml"));
       // URL url = getClass().getResource("/Main.fxml") ;
      //  System.out.println( url );
       // Parent root  = FXMLLoader.load(url);
        /*
        Parent root = null;
        try {

            root = FXMLLoader.load(getClass().getResource("/views/Main.fxml"));
            stage.setTitle("Hello World");
            stage.setScene(new Scene(root, 300, 275));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
        */



    }

    public static void main(String[] args) {
        launch();
    }
}
