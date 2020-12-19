import Models.dao.ClientDao;
import Models.entities.ClientEntity;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.Instant;
import java.util.List;


public class JavaFxApplication extends Application {
//public class JavaFxApplication {

    //private static UserDao userDao = new UserDao();
    //private static RepositoryDao<UserEntity> RepositoryDao = new RepositoryDao<UserEntity>();

    @Override
    public void start(Stage stage) throws Exception {

 /*       List< UserEntity > listUser = userDao.getAll("users");
        System.out.println( listUser );*/

        Parent root = FXMLLoader.load(this.getClass().getResource("views/Main.fxml"));
        stage.setTitle("Hello World");
        stage.setScene(new Scene(root));
        stage.show();

    }


    public static void main(String[] args) {

        ClientDao clientDao = new ClientDao();

        ClientEntity nn = new ClientEntity();
        nn.setName("aaa");
        nn.setCreatedAt(Instant.now());
        clientDao.save(nn);

        List< ClientEntity > oo  = clientDao.getAll();





        System.out.println( oo );



        //List< UserEntity > listUsers = ( new RepositoryDao<UserEntity>()).getAll("UserEntity");
        //List<ClientEntity> listClients = ( new ClientDao()).getAll();
        //List<ClientEntity> ListClients = ( new RepositoryDao<ClientEntity>()).getAll("ClientEntity");

        //System.out.println( listUsers );

       launch();
    }
}
