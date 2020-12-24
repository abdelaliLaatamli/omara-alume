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
        //aluminumDoa = new RepositoryDao<AluminumEntity>();

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

        //productSellingPrice.setCellValueFactory(cellDate -> new ReadOnlyObjectWrapper(cellDate.getValue().getPrices().size()) );

        Callback<TableColumn<ClientEntity, Void>, TableCell<ClientEntity, Void>> cellFactory = new Callback<TableColumn<ClientEntity, Void>, TableCell<ClientEntity, Void>>() {
            @Override
            public TableCell<ClientEntity, Void> call(final TableColumn<ClientEntity, Void> param) {
                final TableCell<ClientEntity, Void> cell = new TableCell<ClientEntity, Void>() {

                    private final Button btn = new Button("Edit");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            ClientEntity data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data.getId());

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

/*        TableColumn<AluminumEntity , String> productName;
        TableColumn<AluminumEntity , String> productColor;
        TableColumn<AluminumEntity , Float> productBuyPrice;
        TableColumn<AluminumEntity , String> productCountryManufacture;
        TableColumn<AluminumEntity, Instant> productCreationDate;
        TableColumn<AluminumEntity , String> productSellingPrice;
        TableColumn productEdit ;*/

        System.out.println( listAlum.size() );
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

        System.out.println(productIdForm.getText());
        System.out.println(productNameForm.getText());
        System.out.println(buyPriceProductForm.getText());
        System.out.println(colorProductForm.getSelectionModel().getSelectedItem());
        System.out.println(productCountryManufactureForm.getSelectionModel().getSelectedItem());
        System.out.println(sellPriceForm.getText());

        if( productIdForm.getText() == null || productIdForm.getText().equals("") ) {

            AluminumEntity aluminumEntity = new AluminumEntity();

            aluminumEntity.setName(productNameForm.getText());
            aluminumEntity.setPriceOfBuy( Float.valueOf( buyPriceProductForm.getText() ) );
            aluminumEntity.setColor( colorProductForm.getSelectionModel().getSelectedItem());
            aluminumEntity.setMadeBy( productCountryManufactureForm.getSelectionModel().getSelectedItem() );

/*            PriceEntity defaultPrice = new PriceEntity();
            defaultPrice.setName( "default" );
            defaultPrice.setPrice( Float.valueOf( sellPriceForm.getText()) );

            aluminumEntity.getPrices().add( defaultPrice );*/

            boolean added = aluminumService.addProductAlum( aluminumEntity );

            System.out.println( added );
            loadData();
            System.out.println("add");

        }else {


            System.out.println("edit");

        }

    }


}
