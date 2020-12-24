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
import main.Models.entities.AluminumEntity;
import main.Models.entities.ClientEntity;
import main.Models.entities.PriceEntity;
import main.Models.enums.AluminumColors;
import main.Models.enums.MadeBy;
import main.Services.AluminumService;


import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import java.util.ResourceBundle;

public class AluminumController implements Initializable {


    //private RepositoryDao<AluminumEntity> aluminumDoa ;
    private AluminumService aluminumService = new AluminumService();
    //private AluminumService aluminumService = new AluminumService();

    @FXML
    TableView<AluminumEntity> tableViewOfAlumProducts;

    @FXML
    TableColumn<AluminumEntity , String> productName;

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
    TextField buyPriceProductForm;

    @FXML
    ComboBox<AluminumColors> colorProductForm;

    @FXML
    ComboBox<MadeBy> productCountryManufactureForm;

    @FXML
    TextField sellPriceForm;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        colorProductForm.setItems( FXCollections.observableArrayList( AluminumColors.values() ) );
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
        productColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        productBuyPrice.setCellValueFactory(new PropertyValueFactory<>("priceOfBuy"));
        productCountryManufacture.setCellValueFactory(new PropertyValueFactory<>("madeBy"));
        productCreationDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

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
                            productNameForm.setText( data.getName() );
                            buyPriceProductForm.setText( String.valueOf( data.getPriceOfBuy() ) );
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

            AluminumEntity aluminumEntity = new AluminumEntity();
            aluminumEntity.setName(productNameForm.getText());
            aluminumEntity.setPriceOfBuy(!buyPriceProductForm.getText().equals("") ? Float.valueOf(  buyPriceProductForm.getText() ) : 0.0f );
            aluminumEntity.setColor( colorProductForm.getSelectionModel().getSelectedItem());
            aluminumEntity.setMadeBy( productCountryManufactureForm.getSelectionModel().getSelectedItem() );
            PriceEntity defaultPrice  = new PriceEntity();
            defaultPrice.setName( "default" );
            defaultPrice.setPrice( (!sellPriceForm.getText().equals("")) ? Float.valueOf( sellPriceForm.getText()) : 0.0f );
            aluminumEntity.getPrices().add( defaultPrice );


            boolean added = aluminumService.addProductAlum( aluminumEntity , defaultPrice);

            System.out.println( added );

            System.out.println("add");

        }else {

            AluminumEntity aluminumEntity = aluminumService.getAlumenuim( Integer.valueOf( productIdForm.getText() ) );

            aluminumEntity.setName(productNameForm.getText());

            if( !buyPriceProductForm.getText().equals("") )
                aluminumEntity.setPriceOfBuy( Float.valueOf( buyPriceProductForm.getText() ) );

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

            AluminumEntity updated = aluminumService.saveProduct( aluminumEntity );

            System.out.println( updated );
            System.out.println("updated");

        }
        loadData();

    }


}
