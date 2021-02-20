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
import main.Models.entities.ClientEntity;
import main.Models.entities.queryContainers.ClientCredit;
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

//    @FXML TableView<ClientEntity> listClients ;
//    @FXML TableColumn<ClientEntity, String> clientName ;
//    @FXML TableColumn<ClientEntity , String> clientTele ;
//    @FXML TableColumn<ClientEntity , String> clientCin ;
//    @FXML TableColumn<ClientEntity , String> clientAddress ;
//    @FXML TableColumn<ClientEntity , Instant> clientCreationDate ;
//    @FXML TableColumn<ClientEntity , Integer> clientNumberOrder;
//    @FXML TableColumn<ClientEntity , Double> oldCreditTbl ;
//    @FXML TableColumn clientEdit;

    @FXML TableView<ClientCredit> listClients ;
    @FXML TableColumn<ClientCredit, String> clientName ;
    @FXML TableColumn<ClientCredit , String> clientTele ;
    @FXML TableColumn<ClientCredit , String> clientCin ;
    @FXML TableColumn<ClientCredit , String> clientAddress ;
    @FXML TableColumn<ClientCredit , Instant> clientCreationDate ;
    @FXML TableColumn<ClientCredit , Integer> clientNumberOrder;
    @FXML TableColumn<ClientCredit , Double> oldCreditTbl ;
    @FXML TableColumn<ClientCredit , Double> totalCreditClientTbl ;

    @FXML TableColumn clientEdit;


    ObservableList<ClientCredit> observableClients = FXCollections.observableArrayList();
//    ObservableList<ClientEntity> observableClients = FXCollections.observableArrayList();


    @FXML TextField idClientForm ;
    @FXML TextField nameClientForm;
    @FXML TextField teleClientForm;
    @FXML TextField cinClientForm ;
    @FXML TextField addressClientForm;
    @FXML TextField oldCreditForm;

    private CurrentCrudOperation currentCrudOperation = CurrentCrudOperation.ADD ;
    private ClientEntity editableClientEntity = null ;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.loadData();

    }



    private void loadData(){

//        List<ClientEntity> clients = clientServices.getAll();

        List<ClientCredit> clients =  clientServices.getAllWithCredit();

        observableClients.clear();
        observableClients.addAll( clients );

        clientName.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper( cellData.getValue().getClient().getName() ) );
        clientTele.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper( cellData.getValue().getClient().getTele() ));
        clientCin.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper( cellData.getValue().getClient().getCin() ));
        clientAddress.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper( cellData.getValue().getClient().getAddress() ));
        totalCreditClientTbl.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper( cellData.getValue().getCredit() ));
        clientCreationDate.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper(
                DateTimeFormatter.ofPattern( "dd/MM/yyyy" ).withZone(ZoneId.systemDefault()).format(cellData.getValue().getClient().getCreatedAt()))
        );

        oldCreditTbl.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper( cellData.getValue().getClient().getOldCredit() ));

        clientNumberOrder.setCellValueFactory(cellDate -> new ReadOnlyObjectWrapper<>(cellDate.getValue().getClient().getOrders().size()) );

        Callback<TableColumn<ClientCredit, Void>, TableCell<ClientCredit, Void>> cellFactory = new Callback<TableColumn<ClientCredit, Void>, TableCell<ClientCredit, Void>>() {
            @Override
            public TableCell<ClientCredit, Void> call(final TableColumn<ClientCredit, Void> param) {
                final TableCell<ClientCredit, Void> cell = new TableCell<ClientCredit, Void>() {

                    private final Button btn = new Button("Edit");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            ClientEntity data = getTableView().getItems().get(getIndex()).getClient();
                            System.out.println("selectedData: " + data.getId());

                            idClientForm.setText( String.valueOf( data.getId() ) );
                            nameClientForm.setText( data.getName() );
                            cinClientForm.setText( data.getCin() );
                            teleClientForm.setText( data.getTele() );
                            addressClientForm.setText( data.getAddress() );
                            oldCreditForm.setText(String.valueOf(data.getOldCredit()));

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
        client.setOldCredit(Double.parseDouble( oldCreditForm.getText().equals("") ? "0" : oldCreditForm.getText() ));

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
        oldCreditForm.setText("0");

        editableClientEntity = null ;
        currentCrudOperation = CurrentCrudOperation.ADD ;

        this.loadData();

    }
}
