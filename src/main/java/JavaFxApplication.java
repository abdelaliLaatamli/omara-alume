import Models.dao.UserDao;
import Models.model.UserEntity;

import java.util.List;


//public class JavaFxApplication extends Application {
public class JavaFxApplication {

    private static UserDao userDao = new UserDao();
/*
    @Override
    public void start(Stage stage) throws Exception {

        List< UserEntity > listUser = userDao.getAll("users");
        System.out.println( listUser );
        /*
        Parent root = FXMLLoader.load(this.getClass().getResource("views/Main.fxml"));
        stage.setTitle("Hello World");
        stage.setScene(new Scene(root));
        stage.show();


    }
    */

    public static void main(String[] args) {
        List< UserEntity > listUser = userDao.getAllUser();
        System.out.println( listUser );

       // launch();
    }
}
