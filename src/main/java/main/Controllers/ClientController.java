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
import main.Models.utils.CurrentCrudOperation;
import main.Services.ClientServices;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ClientController implements Initializable {

    ClientServices clientServices  = new ClientServices();

    @FXML TableView<ClientEntity> listClients ;
    @FXML TableColumn<ClientEntity, String> clientName ;
    @FXML TableColumn<ClientEntity , String> clientTele ;
    @FXML TableColumn<ClientEntity , String> clientCin ;
    @FXML TableColumn<ClientEntity , String> clientAddress ;
    @FXML TableColumn<ClientEntity , Instant> clientCreationDate ;
    @FXML TableColumn<ClientEntity , Integer> clientNumberCommand ;
    @FXML TableColumn clientEdit;

    ObservableList<ClientEntity> observableClients = FXCollections.observableArrayList();

    @FXML TextField idClientForm ;
    @FXML TextField nameClientForm;
    @FXML TextField teleClientForm;
    @FXML TextField cinClientForm ;
    @FXML TextField addressClientForm;

    private CurrentCrudOperation currentCrudOperation = CurrentCrudOperation.ADD ;
    private ClientEntity editableClientEntity = null ;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.loadData();

    }



    private void loadData(){

        List<ClientEntity> clients = clientServices.getAll();

        observableClients.clear();
        observableClients.addAll( clients );

        clientName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clientTele.setCellValueFactory(new PropertyValueFactory<>("tele"));
        clientCin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        clientAddress.setCellValueFactory(new PropertyValueFactory<>("address"));

        clientCreationDate.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper(
                DateTimeFormatter.ofPattern( "dd/MM/yyyy" ).withZone(ZoneId.systemDefault()).format(cellData.getValue().getCreatedAt())
        ));

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

                            editableClientEntity = data ;
                            currentCrudOperation = CurrentCrudOperation.EDIT ;


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

        ClientEntity client = ( currentCrudOperation == CurrentCrudOperation.EDIT ) ? editableClientEntity : new ClientEntity();

        client.setName( nameClientForm.getText() );
        client.setCin( cinClientForm.getText() );
        client.setTele( teleClientForm.getText() );
        client.setAddress( addressClientForm.getText() );

        boolean saved = ( currentCrudOperation == CurrentCrudOperation.ADD ) ?
                clientServices.addClient( client ) :
                clientServices.updateClient( client );

        if (saved) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("l'enregistrement en Client réussi");
            alert.setHeaderText("le Client est bien enregistrer");
            alert.showAndWait();

        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error D'enregistrement");
            alert.setHeaderText("Oups, il y a eu une erreur!");
            alert.showAndWait();
        }

        idClientForm.setText( "" );
        nameClientForm.setText( "" );
        cinClientForm.setText( "" );
        teleClientForm.setText( "" );
        addressClientForm.setText( "" );

        editableClientEntity = null ;
        currentCrudOperation = CurrentCrudOperation.ADD ;

        this.loadData();

    }
}
