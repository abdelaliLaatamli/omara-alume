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
import main.Models.entities.AluminumEntity;
import main.Models.entities.PriceEntity;
import main.Models.enums.AluminumColor;
import main.Models.enums.MadeBy;
import main.Services.AluminumService;


import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;


public class AluminumController implements Initializable {


    private AluminumService aluminumService = new AluminumService();

    @FXML
    TableView<AluminumEntity> tableViewOfAlumProducts;

    @FXML
    TableColumn<AluminumEntity , String> productName;

    @FXML
    TableColumn<AluminumEntity , Integer> productQuantity;

    @FXML
    TableColumn<AluminumEntity , String> productColor;

    @FXML
    TableColumn<AluminumEntity , Float> productBuyPrice;

    @FXML
    TableColumn<AluminumEntity , String> productCountryManufacture;

    @FXML
    TableColumn<AluminumEntity, Instant> productCreationDate;

    @FXML
    TableColumn<AluminumEntity , String> productSellingPrice;


    @FXML
    TableColumn productEdit ;

    ObservableList<AluminumEntity> observableEntities = FXCollections.observableArrayList();

    @FXML
    TextField productIdForm;

    @FXML
    TextField productNameForm;

    @FXML
    TextField productQuantityForm;

    @FXML
    TextField buyPriceProductForm;

    @FXML
    ComboBox<AluminumColor> colorProductForm;

    @FXML
    ComboBox<MadeBy> productCountryManufactureForm;

    @FXML
    TextField sellPriceForm;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colorProductForm.setItems( FXCollections.observableArrayList( AluminumColor.values() ) );
        colorProductForm.getSelectionModel().selectFirst();
        productCountryManufactureForm.setItems( FXCollections.observableArrayList( MadeBy.values() ) );
        productCountryManufactureForm.getSelectionModel().selectFirst();


        this.loadData();
    }

    private void loadData(){


        this.tableViewOfAlumProducts.getItems().clear();
        this.tableViewOfAlumProducts.refresh();
        List<AluminumEntity> listAlum = aluminumService.getAllAlumunuimProducts();

        observableEntities.addAll( listAlum );

        productName.setCellValueFactory(new PropertyValueFactory<>("name"));
        //productQuantity.setCellValueFactory( cellDate -> new ReadOnlyObjectWrapper( (int) cellDate.getValue().getQuantity() ) );
        productQuantity.setCellValueFactory( cellDate -> new ReadOnlyObjectWrapper(  cellDate.getValue().getName() ) );
        productColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        productBuyPrice.setCellValueFactory(new PropertyValueFactory<>("priceOfBuy"));
        productCountryManufacture.setCellValueFactory(new PropertyValueFactory<>("madeBy"));
        productCreationDate.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper(
                "aaaa"
               // DateTimeFormatter.ofPattern( "dd/MM/yyyy" ).withZone(ZoneId.systemDefault()).format(cellData.getValue())
        ));

        productSellingPrice.setCellValueFactory(cellDate -> new ReadOnlyObjectWrapper(
                ( cellDate.getValue().getPrices().size() > 0 ) ?
                        cellDate.getValue().getPrices().stream().findFirst().get().getPrice() :
                        cellDate.getValue().getPrices().size()
                )
        );

        Callback<TableColumn<AluminumEntity, Void>, TableCell<AluminumEntity, Void>> cellFactory = new Callback<TableColumn<AluminumEntity, Void>, TableCell<AluminumEntity, Void>>() {
            @Override
            public TableCell<AluminumEntity, Void> call(final TableColumn<AluminumEntity, Void> param) {
                final TableCell<AluminumEntity, Void> cell = new TableCell<AluminumEntity, Void>() {

                    private final Button btn = new Button("Edit");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            AluminumEntity data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data.getId());

                            productIdForm.setText( String.valueOf( data.getId() ) );
                            // productQuantityForm.setText(  String.valueOf( (int) data.getQuantity() ) );
                            productQuantityForm.setText(  String.valueOf( 15 ) );
                            productNameForm.setText( data.getName() );
                            // buyPriceProductForm.setText( String.valueOf( data.getPriceOfBuy() ) );
                            buyPriceProductForm.setText( String.valueOf( 15 ) );
                            colorProductForm.getSelectionModel().select( data.getColor() );
                            productCountryManufactureForm.getSelectionModel().select( data.getMadeBy() );// getSelectedItem();
                            sellPriceForm.setText( String.valueOf( data.getPrices().stream().findFirst().get().getPrice() ) );

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

        productEdit.setCellFactory(cellFactory);
        tableViewOfAlumProducts.setItems( FXCollections.observableArrayList(listAlum) );
        tableViewOfAlumProducts.setItems( observableEntities );

    }


    public void goHome(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/MainView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Home -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();
    }

    public void goBack(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ProductsView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" Products -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }

    public void saveAluminumForm(MouseEvent mouseEvent) {


        if( productIdForm.getText() == null || productIdForm.getText().equals("") ) {
            addProduct();
        }else {
            updateProduct();
        }

        loadData();

        productIdForm.setText("");
        productNameForm.setText("");
        buyPriceProductForm.setText("");
        colorProductForm.getSelectionModel().selectFirst();
        productCountryManufactureForm.getSelectionModel().selectFirst();
        productQuantityForm.setText("");
        sellPriceForm.setText("");

    }

    private void updateProduct() {
        AluminumEntity aluminumEntity = aluminumService.getAlumenuim( Integer.valueOf( productIdForm.getText() ) );

        aluminumEntity.setName(productNameForm.getText());

//        if( !productQuantityForm.getText().equals("") )
//            aluminumEntity.setQuantity( Float.valueOf( productQuantityForm.getText() ) );

//        if( !buyPriceProductForm.getText().equals("") )
//            aluminumEntity.setPriceOfBuy( Float.valueOf( buyPriceProductForm.getText() ) );

        aluminumEntity.setColor( colorProductForm.getSelectionModel().getSelectedItem());

        aluminumEntity.setMadeBy( productCountryManufactureForm.getSelectionModel().getSelectedItem() );

        if(!sellPriceForm.getText().equals(""))
            aluminumEntity
                    .getPrices()
                    .stream()
                    .filter( price -> price.getName().equals("default") )
                    .findFirst()
                    .get()
                    .setPrice( Float.valueOf( sellPriceForm.getText()) );

        boolean updated = aluminumService.updateProduct( aluminumEntity );


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
        AluminumEntity aluminumEntity = new AluminumEntity();
        aluminumEntity.setName(productNameForm.getText());
        // aluminumEntity.setPriceOfBuy(!buyPriceProductForm.getText().equals("") ? Float.valueOf(  buyPriceProductForm.getText() ) : 0f );
        aluminumEntity.setColor( colorProductForm.getSelectionModel().getSelectedItem());
        aluminumEntity.setMadeBy( productCountryManufactureForm.getSelectionModel().getSelectedItem() );
        // aluminumEntity.setQuantity( !productQuantityForm.getText().equals("") ? Float.valueOf( productQuantityForm.getText() ) : 0f );

        PriceEntity defaultPrice  = new PriceEntity();
        defaultPrice.setName( "default" );
        defaultPrice.setPrice( (!sellPriceForm.getText().equals("")) ? Float.valueOf( sellPriceForm.getText()) : 0f );
        aluminumEntity.getPrices().add( defaultPrice );


        boolean added = aluminumService.addProductAlum( aluminumEntity , defaultPrice);

        System.out.println( added );

        if( added ){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("l'ajout de produit réussi");
            alert.setHeaderText("le produit est bien ajouté");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error D'ajouter");
            alert.setHeaderText("Oups, il y a eu une erreur!");
            alert.showAndWait();
        }
    }


}
