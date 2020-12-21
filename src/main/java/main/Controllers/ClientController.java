package main.Controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import main.Models.dao.RepositoryDao;
import main.Models.entities.ClientEntity;
import main.Models.tableModels.TableModels;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.*;

public class ClientController implements Initializable {

    RepositoryDao<ClientEntity> clientsDao ;

    @FXML
    TableView<ClientEntity> listClients ;

    @FXML
    TableColumn<ClientEntity, String> clientName ;

    @FXML
    TableColumn<ClientEntity , String> clientTele ;

    @FXML
    TableColumn<ClientEntity , String> clientCin ;

    @FXML
    TableColumn<ClientEntity , String> clientAddress ;

    @FXML
    TableColumn<ClientEntity , Instant> clientCreationDate ;

    @FXML
    TableColumn<ClientEntity , Integer> clientNumberCommand ;

    @FXML
    TableColumn clientEdit;

    ObservableList<ClientEntity> observableClients = FXCollections.observableArrayList();


    @FXML
    TextField idClientForm ;

    @FXML
    TextField nameClientForm;

    @FXML
    TextField teleClientForm;

    @FXML
    TextField cinClientForm ;

    @FXML
    TextField addressClientForm;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        clientsDao = new RepositoryDao<ClientEntity>();

        this.loadData();

    }



    private void loadData(){

        List<ClientEntity> clients =  clientsDao.getAll("ClientEntity") ;


        observableClients.addAll( clients );

/*        clientName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clientTele.setCellValueFactory(new PropertyValueFactory<>("tele"));
        clientCin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        clientAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        clientCreationDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        clientNumberCommand.setCellValueFactory(new PropertyValueFactory<>("commands") );*/

       // listClients.setItems( clients );
    }



    public void goToHome(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/Main.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Aluminium et verre");
        main.JavaFxApplication.mainStage.show();
    }

    public void saveClientForm(MouseEvent mouseEvent) {

        ClientEntity client = new ClientEntity();
        ClientEntity newClient = null ;


        client.setName( nameClientForm.getText() );
        client.setCin( cinClientForm.getText() );
        client.setTele( teleClientForm.getText() );
        client.setAddress( addressClientForm.getText() );

        if( idClientForm.getText() != null ){
            System.out.println("edit");
            client.setId( Integer.valueOf( idClientForm.getText() ) );
            clientsDao.update( client  );
            newClient = client ;
        }else{
            System.out.println( "add" );
            newClient = clientsDao.save( client , "main.Models.entities.ClientEntity"  );
        }



        if( newClient == null ){
            System.out.println("null");
        }else {
            System.out.println("Saved");

            idClientForm.setText( "" );
            nameClientForm.setText( "" );
            cinClientForm.setText( "" );
            teleClientForm.setText( "" );
            addressClientForm.setText( "" );
            this.loadData();
            listClients.refresh();
        }

/*        idClientForm.setText( "" );
        nameClientForm.setText( "" );
        cinClientForm.setText( "" );
        teleClientForm.setText( "" );
        addressClientForm.setText( "" );*/



    }
}
