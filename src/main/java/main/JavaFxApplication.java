package main ;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import main.Models.dao.PaymentsMadeDao;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;


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


    public static void main(String[] args) throws IOException {

        JavaFxApplication.deleteNullPayements();
        JavaFxApplication.saveDb();
        launch();
    }

    private static void deleteNullPayements(){

        PaymentsMadeDao paymentsMadeDao = new PaymentsMadeDao();
        paymentsMadeDao.deleteAllNull();

    }

    private  static void saveDb() throws IOException {


        String date = (new SimpleDateFormat("dd-MM-yyyy")).format(new Date());
        String month = (new SimpleDateFormat("MM-yyyy")).format(new Date());

        String filePath = JavaFxApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath().replace(
                new java.io.File(JavaFxApplication.class.getProtectionDomain()
                        .getCodeSource()
                        .getLocation()
                        .getPath())
                        .getName() , "DBBKP" );

        File f = new File(filePath);

        if ( f.exists() && f.isDirectory() ) {
            JavaFxApplication.createMonthDate( filePath , month , date );
        }else{
            if (f.mkdir())
                JavaFxApplication.createMonthDate(filePath, month , date);
        }

    }

    private static void createMonthDate(String filePath, String month, String date) throws IOException {


        File f = new File(filePath +"/"+ month);

        if ( f.exists() && f.isDirectory() ) {
            JavaFxApplication.saveDbFile(filePath +"/"+ month , date );
        }else{
            if (f.mkdir()){
                JavaFxApplication.saveDbFile(filePath +"/"+ month , date );
            }
        }
    }

    private static void saveDbFile( String filePath  , String date ) throws IOException {
        Process run = Runtime.getRuntime().exec(
                "C:/xampp/mysql/bin/mysqldump --user=root --password=  omar_alum"
        );
        InputStream in = run.getInputStream();

        in.read(new byte[in.available()]);

        File targetFile = new File(filePath+"/"+date+".sql");

        FileUtils.copyInputStreamToFile(in, targetFile);
    }
}
