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
import main.Models.entities.*;
import main.Models.utils.CurrentCrudOperation;
import main.Services.ProviderService;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ProviderController implements Initializable {

    private final ProviderService providerService = new ProviderService();


    @FXML TableView<ProviderEntity> tProviderList ;
    @FXML TableColumn<ProviderEntity , String> pName;
    @FXML TableColumn<ProviderEntity , String> pTele;
    @FXML TableColumn<ProviderEntity , String> pIdentify;
    @FXML TableColumn<ProviderEntity , String> pAddress;
    @FXML TableColumn<ProviderEntity , String> pDateInsert;
    @FXML TableColumn<ProviderEntity , String> pNOrders;
    @FXML TableColumn<ProviderEntity , Void> providerEdit;
    ObservableList<ProviderEntity> observableProviders = FXCollections.observableArrayList();

    @FXML TextField pIDForm;
    @FXML TextField pNameForm;
    @FXML TextField pTeleForm;
    @FXML TextField pIdentifyForm;
    @FXML TextField pAddressForm;

    private ProviderEntity editableProviderEntity = null ;
    private CurrentCrudOperation currentCrudOperation = CurrentCrudOperation.ADD ;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.loadDataTable();
    }


    private void loadDataTable(){

        observableProviders.clear();
        observableProviders.addAll( providerService.getAll() );

        pName.setCellValueFactory(new PropertyValueFactory<>("name"));
        pTele.setCellValueFactory(new PropertyValueFactory<>("tele"));
        pIdentify.setCellValueFactory(new PropertyValueFactory<>("identify"));
        pAddress.setCellValueFactory( new PropertyValueFactory<>("address"));

        pNOrders.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper( cellData.getValue().getStocks().size() ) );
        pDateInsert.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper(
                DateTimeFormatter.ofPattern( "dd/MM/yyyy" ).withZone(ZoneId.systemDefault()).format(cellData.getValue().getCreateAd())
        ));



        Callback<TableColumn<ProviderEntity, Void>, TableCell<ProviderEntity, Void>> editCellFactory = new Callback<TableColumn<ProviderEntity, Void>, TableCell<ProviderEntity, Void>>() {
            @Override
            public TableCell<ProviderEntity, Void> call(final TableColumn<ProviderEntity, Void> param) {
                final TableCell<ProviderEntity, Void> cell = new TableCell<ProviderEntity, Void>() {

                    private final Button btn = new Button("Edit");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            ProviderEntity data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data.getId());

                            pIDForm.setText( String.valueOf( data.getId() ) );
                            pNameForm.setText( data.getName() );
                            pIdentifyForm.setText( data.getIdentify() );
                            pTeleForm.setText( data.getTele() );
                            pAddressForm.setText( data.getAddress() );

                            currentCrudOperation = CurrentCrudOperation.EDIT ;
                            editableProviderEntity = data ;


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


        providerEdit.setCellFactory( editCellFactory );

        tProviderList.setItems( this.observableProviders );


    }


    public void goBack(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/MainView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Home -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }

    public void saveProvider(MouseEvent mouseEvent) {

        ProviderEntity provider = (currentCrudOperation == CurrentCrudOperation.ADD) ? new ProviderEntity() : editableProviderEntity;

        provider.setName( pNameForm.getText() );
        provider.setIdentify( pIdentifyForm.getText() );
        provider.setTele( pTeleForm.getText() );
        provider.setAddress( pAddressForm.getText() );

        boolean saved = (currentCrudOperation == CurrentCrudOperation.ADD) ? providerService.add(provider) : providerService.update(provider);

        if (saved) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("l'enregistrement de fournisseur est réussi");
            alert.setHeaderText("le fournisseur est bien enregistré");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error D'enregistrement");
            alert.setHeaderText("Oups, il y a eu une erreur!");
            alert.showAndWait();
        }

        pIDForm.setText( "" );
        pNameForm.setText( "" );
        pTeleForm.setText( "" );
        pIdentifyForm.setText( "" );
        pAddressForm.setText( "" );
        this.loadDataTable();

        editableProviderEntity = null ;
        currentCrudOperation = CurrentCrudOperation.ADD ;


    }


}
