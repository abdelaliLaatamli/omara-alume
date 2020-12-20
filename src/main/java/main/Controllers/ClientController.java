package main.Controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.Models.dao.RepositoryDao;
import main.Models.entities.ClientEntity;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;

public class ClientController implements Initializable {

    RepositoryDao<ClientEntity> clientsDao ;

    @FXML
    TableView<ClientEntity> listClients ;

    @FXML
    TableColumn<ClientEntity , String> clientName ;

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

    ObservableList<ClientEntity> observableClients = FXCollections.observableArrayList();



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        clientsDao = new RepositoryDao<ClientEntity>();


        this.loadData();


    }



    private void loadData(){
        List<ClientEntity> clients =  clientsDao.getAll("ClientEntity") ;

        observableClients.addAll( clients );

        clientName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clientTele.setCellValueFactory(new PropertyValueFactory<>("tele"));
        clientCin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        clientAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        clientCreationDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        clientNumberCommand.setCellValueFactory(cellDate -> new ReadOnlyObjectWrapper<>(cellDate.getValue().getCommands().size()) );

        listClients.setItems( observableClients );
    }
}
