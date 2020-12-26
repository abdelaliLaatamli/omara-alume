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
import main.Models.entities.AccessoryEntity;
import main.Models.entities.AluminumEntity;
import main.Models.entities.PriceEntity;
import main.Models.enums.AccessoryColor;
import main.Services.AccessoryService;
import main.Services.AluminumService;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AccessoryController implements Initializable {

    private AccessoryService accessoryService = new AccessoryService();

    @FXML TableView<AccessoryEntity> tableViewOfAlumAccessories;
    @FXML TableColumn<AccessoryEntity , String> accessoryName ;
    @FXML TableColumn<AccessoryEntity , Float> accessoryBuyPrice;
    @FXML TableColumn<AccessoryEntity , Integer> accessoryQuantity;
    @FXML TableColumn<AccessoryEntity , String> accessoryColor;
    @FXML TableColumn<AccessoryEntity , String> accessoryCreationDate;
    @FXML TableColumn<AccessoryEntity , Float> accessorySellingPrice;
    @FXML TableColumn accessoryEdit;

    ObservableList<AccessoryEntity> observableEntities = FXCollections.observableArrayList();

    @FXML TextField accessoryIdForm;
    @FXML TextField accessoryNameForm;
    @FXML TextField accessoryBuyPriceForm ;
    @FXML TextField accessoryQuantityForm;
    @FXML ComboBox<AccessoryColor>  accessoryColorForm;
    @FXML TextField accessorySellPriceForm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        accessoryColorForm.setItems(FXCollections.observableArrayList(AccessoryColor.values()));
        accessoryColorForm.getSelectionModel().selectFirst();

        this.loadData();

    }


    private void loadData(){

        this.tableViewOfAlumAccessories.getItems().clear();
        this.tableViewOfAlumAccessories.refresh();
        List<AccessoryEntity> listAccessories = accessoryService.getAllAccessoryProducts();

        observableEntities.addAll( listAccessories );


        accessoryName.setCellValueFactory( new PropertyValueFactory<>("name") ); ;
        accessoryBuyPrice.setCellValueFactory(new PropertyValueFactory<>("priceOfBuy"));
        accessoryQuantity.setCellValueFactory(cellDate -> new ReadOnlyObjectWrapper<>( (int) cellDate.getValue().getQuantity() ));
        accessoryColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        accessoryCreationDate.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
                DateTimeFormatter.ofPattern( "dd/MM/yyyy" ).withZone(ZoneId.systemDefault()).format(cellData.getValue().getCreatedAt())
        ));
        accessorySellingPrice.setCellValueFactory(
                cellDate -> new ReadOnlyObjectWrapper(
                        ( cellDate.getValue().getPrices().size() > 0 ) ?
                                cellDate.getValue().getPrices().stream().findFirst().get().getPrice() :
                                cellDate.getValue().getPrices().size()
                )
        );

        Callback<TableColumn<AccessoryEntity, Void>, TableCell<AccessoryEntity, Void>> cellFactory = new Callback<TableColumn<AccessoryEntity, Void>, TableCell<AccessoryEntity, Void>>() {
            @Override
            public TableCell<AccessoryEntity, Void> call(final TableColumn<AccessoryEntity, Void> param) {
                final TableCell<AccessoryEntity, Void> cell = new TableCell<AccessoryEntity, Void>() {

                    private final Button btn = new Button("Edit");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            AccessoryEntity data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data.getId());

                            accessoryIdForm.setText( String.valueOf( data.getId() ) );
                            accessoryNameForm.setText( data.getName() );
                            accessoryBuyPriceForm.setText( String.valueOf( data.getPriceOfBuy() ) );
                            accessoryQuantityForm.setText( String.valueOf( (int) data.getQuantity() ) );
                            accessoryColorForm.getSelectionModel().select( data.getColor() );
                            accessorySellPriceForm.setText( String.valueOf( data.getPrices().stream().findFirst().get().getPrice() ) );

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

        accessoryEdit.setCellFactory(cellFactory);
        tableViewOfAlumAccessories.setItems( FXCollections.observableArrayList(listAccessories) );
        tableViewOfAlumAccessories.setItems( observableEntities );

    }

    public void saveAccessoryForm(MouseEvent mouseEvent) {

        if( accessoryIdForm.getText() == null || accessoryIdForm.getText().equals("") ) {
            addProduct();
        }else {
            updateProducts();
        }

        loadData();

        accessoryIdForm.setText("");
        accessoryNameForm.setText("");
        accessoryBuyPriceForm.setText("");
        accessoryColorForm.getSelectionModel().selectFirst();
        accessoryQuantityForm.setText("");
        accessorySellPriceForm.setText("");

    }

    private void updateProducts() {
        AccessoryEntity accessoryEntity = accessoryService.getAlumenuim( Integer.valueOf( accessoryIdForm.getText() ) );

        accessoryEntity.setName(accessoryNameForm.getText());

        if( !accessoryQuantityForm.getText().equals("") )
            accessoryEntity.setQuantity( Float.valueOf( accessoryQuantityForm.getText() ) );

        if( !accessoryBuyPriceForm.getText().equals("") )
            accessoryEntity.setPriceOfBuy( Float.valueOf( accessoryBuyPriceForm.getText() ) );

        accessoryEntity.setColor( accessoryColorForm.getSelectionModel().getSelectedItem());

        if(!accessorySellPriceForm.getText().equals(""))
                accessoryEntity
                        .getPrices()
                        .stream()
                        .filter( price -> price.getName().equals("default") )
                        .findFirst()
                        .get()
                        .setPrice( Float.valueOf( accessorySellPriceForm.getText()) );

        boolean updated = accessoryService.updateProduct( accessoryEntity );

        if( updated ){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Réussite de la mise à jour");
            alert.setHeaderText("le produit est mise à jour");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error D'ajouter");
            alert.setHeaderText("Oups, il y a eu une erreur!");
            alert.showAndWait();
        }
    }

    private void addProduct() {
        AccessoryEntity accessoryEntity = new AccessoryEntity();
        accessoryEntity.setName(accessoryNameForm.getText());
        accessoryEntity.setPriceOfBuy(!accessoryBuyPriceForm.getText().equals("") ? Float.valueOf(accessoryBuyPriceForm.getText()) : 0f);

        accessoryEntity.setColor(accessoryColorForm.getSelectionModel().getSelectedItem());
        accessoryEntity.setQuantity(!accessoryQuantityForm.getText().equals("") ? Float.valueOf(accessoryQuantityForm.getText()) : 0f);

        PriceEntity defaultPrice = new PriceEntity();
        defaultPrice.setName("default");
        defaultPrice.setPrice((!accessorySellPriceForm.getText().equals("")) ? Float.valueOf(accessorySellPriceForm.getText()) : 0f);
        accessoryEntity.getPrices().add(defaultPrice);


        boolean added = accessoryService.addProduct(accessoryEntity, defaultPrice);

        System.out.println(added);

        if (added) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("l'ajout de produit réussi");
            alert.setHeaderText("le produit est bien ajouté");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error D'ajouter");
            alert.setHeaderText("Oups, il y a eu une erreur!");
            alert.showAndWait();
        }
    }

    public void goBack(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ProductsView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" Products -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }

    public void goHome(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/MainView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Home -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }




}
