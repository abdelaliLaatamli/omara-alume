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

        clientName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clientTele.setCellValueFactory(new PropertyValueFactory<>("tele"));
        clientCin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        clientAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        clientCreationDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
//        clientCreationDate.setCellValueFactory( cellData -> new ReadOnlyStringWrapper((new SimpleDateFormat("MM-dd-yyyy")).format(cellData.getValue().getCreatedAt()))  );


        clientNumberCommand.setCellValueFactory(cellDate -> new ReadOnlyObjectWrapper<>(cellDate.getValue().getCommands().size()) );

        Callback<TableColumn<ClientEntity, Void>, TableCell<ClientEntity, Void>> cellFactory = new Callback<TableColumn<ClientEntity, Void>, TableCell<ClientEntity, Void>>() {
            @Override
            public TableCell<ClientEntity, Void> call(final TableColumn<ClientEntity, Void> param) {
                final TableCell<ClientEntity, Void> cell = new TableCell<ClientEntity, Void>() {

                    private final Button btn = new Button("Edit");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            ClientEntity data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data.getId());

                            idClientForm.setText( String.valueOf( data.getId() ) );
                            nameClientForm.setText( data.getName() );
                            cinClientForm.setText( data.getCin() );
                            teleClientForm.setText( data.getTele() );
                            addressClientForm.setText( data.getAddress() );

                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        clientEdit.setCellFactory(cellFactory);
        listClients.setItems( FXCollections.observableArrayList(clients) );
        listClients.setItems( this.observableClients );

    }



    public void goToHome(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/MainView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Home -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();
    }

    public void saveClientForm(MouseEvent mouseEvent) {

        ClientEntity client = new ClientEntity();
        ClientEntity newClient = null ;


        client.setName( nameClientForm.getText() );
        client.setCin( cinClientForm.getText() );
        client.setTele( teleClientForm.getText() );
        client.setAddress( addressClientForm.getText() );


        if( idClientForm.getText() == null || idClientForm.getText().equals("") ){

            System.out.println( "add" );
            newClient = clientsDao.save( client , "main.Models.entities.ClientEntity"  );

        }else{

            System.out.println("edit");
            client.setId( Integer.valueOf( idClientForm.getText() ) );
            clientsDao.update( client  );
            newClient = client ;

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

            this.listClients.getItems().clear();
            this.listClients.refresh();
            this.loadData();

        }




    }
}
