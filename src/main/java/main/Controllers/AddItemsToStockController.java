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
import main.Models.enums.ProductsType;
import main.Models.utils.CurrentCrudOperation;
import main.Services.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class AddItemsToStockController implements Initializable {

    private final StockService stockService = new StockService();
    private final ProviderService providerService = new ProviderService();
    private final AccessoryService accessoryService = new AccessoryService();
    private final AluminumService aluminumService = new AluminumService();
    private final GlassService glassService = new GlassService();


    @FXML ComboBox<ProviderEntity> sProvider;
    @FXML TextField sLable;
    @FXML ComboBox<ProductsType> sProduitType;
    @FXML ComboBox<ArticleEntity> sProduit;
    @FXML TextField sQuentity;
    @FXML TextField sPriceOfBuy;


    StockEntity stockEntity ;

    @FXML TableView<StockItemsEntity> tableProductsOfStockOrder;
    @FXML TableColumn<StockItemsEntity , String> sProductName;
    @FXML TableColumn<StockItemsEntity , String> sProductsQuentityColumn;
    @FXML TableColumn<StockItemsEntity , String> sPriceOfBuyColumn;
    @FXML TableColumn<StockItemsEntity , String> sTotalOrder;
    @FXML TableColumn<StockItemsEntity , Void> sEditProductItem;
    @FXML TableColumn<StockItemsEntity , Void> sRemoveProductFromStockItems;

    ObservableList<StockItemsEntity> observableList = FXCollections.observableArrayList();



    CurrentCrudOperation currentOperationStockItem = CurrentCrudOperation.ADD;
    StockItemsEntity currentStockItemEdited = null ;


    CurrentCrudOperation currentCrudOperationStock = CurrentCrudOperation.ADD;


    public void setData( StockEntity stock  ){

        stockEntity = stock ;
        currentCrudOperationStock = CurrentCrudOperation.EDIT;

        sProvider.getSelectionModel().select( stockEntity.getProvider() );
        sProvider.setDisable( true );

        sLable.setText( stockEntity.getName() );

        this.loadDataTable();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        stockEntity = new StockEntity();

        sProvider.setItems( FXCollections.observableArrayList( providerService.getAll() ) );
        sProduitType.setItems( FXCollections.observableArrayList( ProductsType.values() ) );


        sProduitType.getSelectionModel().selectedIndexProperty().addListener( ( (options, oldValue, newValue) -> {

            switch ( (int) newValue ){
                case 0 :
                    sProduit.getItems().clear();
                    sProduit.setItems( FXCollections.observableArrayList( aluminumService.getAllAlumunuimProducts() ) );
                    break;

                case 1 :
                    sProduit.getItems().clear();
                    sProduit.setItems( FXCollections.observableArrayList( accessoryService.getAllAccessoryProducts() ) );
                    break;

                case 2:
                    sProduit.getItems().clear();
                    sProduit.setItems( FXCollections.observableArrayList( glassService.getAllGlassProducts() ) );
                    break;

            }


        } )  );

        sProduitType.getSelectionModel().selectFirst();

    }

    private void loadDataTable() {

        observableList.clear();
        observableList.addAll( stockEntity.getStockItems() );

        sProductName.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(  cellData.getValue().getArticle().getName() ));
        sPriceOfBuyColumn.setCellValueFactory(new PropertyValueFactory<>("priceOfBuy"));
        sProductsQuentityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        sTotalOrder.setCellValueFactory( cellData ->  new ReadOnlyObjectWrapper( cellData.getValue().getQuantity() * cellData.getValue().getPriceOfBuy() + " DH" ) );


        Callback<TableColumn<StockItemsEntity, Void>, TableCell<StockItemsEntity, Void>> editCellFactory = new Callback<TableColumn<StockItemsEntity, Void>, TableCell<StockItemsEntity, Void>>() {
            @Override
            public TableCell<StockItemsEntity, Void> call(final TableColumn<StockItemsEntity, Void> param) {
                final TableCell<StockItemsEntity, Void> cell = new TableCell<StockItemsEntity, Void>() {

                    private final Button btn = new Button("Edit");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            StockItemsEntity data = getTableView().getItems().get(getIndex());

                            switch (data.getArticle().getType()){
                                case "AluminumEntity"  :
                                    sProduitType.getSelectionModel().select(0);
                                    break;

                                case "AccessoryEntity" :
                                    sProduitType.getSelectionModel().select(1);
                                    break;

                                case "GlassEntity"     :
                                    sProduitType.getSelectionModel().select(2);
                                    break;

                                default:
                                    System.out.println(" there no type of avalaibele types ");
                            }

                            sProduit.getSelectionModel().select(data.getArticle());
                            sQuentity.setText( String.valueOf( data.getQuantity()) );
                            sPriceOfBuy.setText( String.valueOf( data.getPriceOfBuy() ) );

                            currentStockItemEdited = data;
                            currentOperationStockItem = CurrentCrudOperation.EDIT;

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

        Callback<TableColumn<StockItemsEntity, Void>, TableCell<StockItemsEntity, Void>> deleteCellFactory = new Callback<TableColumn<StockItemsEntity, Void>, TableCell<StockItemsEntity, Void>>() {
            @Override
            public TableCell<StockItemsEntity, Void> call(final TableColumn<StockItemsEntity, Void> param) {
                final TableCell<StockItemsEntity, Void> cell = new TableCell<StockItemsEntity, Void>() {

                    private final Button btn = new Button("Delete");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            StockItemsEntity data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData delete: " + data.getId());
                            stockEntity.getStockItems().remove( data );
                            loadDataTable();
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

        sEditProductItem.setCellFactory( editCellFactory );
        sRemoveProductFromStockItems.setCellFactory( deleteCellFactory );

        tableProductsOfStockOrder.setItems( this.observableList );


    }


    public void saveArticleInStock(MouseEvent mouseEvent) {


        if( currentOperationStockItem == CurrentCrudOperation.ADD ){
            StockItemsEntity stockItemsEntity = new StockItemsEntity();
            stockItemsEntity.setArticle( sProduit.getSelectionModel().getSelectedItem());
            stockItemsEntity.setQuantity( Float.valueOf( sQuentity.getText()) );
            stockItemsEntity.setPriceOfBuy( Float.valueOf( sPriceOfBuy.getText() ) );
            stockEntity.getStockItems().add( stockItemsEntity );
        }else{
            StockItemsEntity stockItemsEntity = currentStockItemEdited;
            stockItemsEntity.setArticle( sProduit.getSelectionModel().getSelectedItem());
            stockItemsEntity.setQuantity( Float.valueOf( sQuentity.getText()) );
            stockItemsEntity.setPriceOfBuy( Float.valueOf( sPriceOfBuy.getText() ) );
            currentStockItemEdited = null ;

        }

        loadDataTable();
        currentOperationStockItem = CurrentCrudOperation.ADD;
        sProduitType.getSelectionModel().select(0);
        sProduit.getSelectionModel().select(null);
        sQuentity.setText( "1" );
        sPriceOfBuy.setText( "0" );

    }



    public void saveStockOrder(MouseEvent mouseEvent) throws IOException {
        stockEntity.setName( sLable.getText() );
        stockEntity.setProvider( sProvider.getSelectionModel().getSelectedItem() );
        boolean saved = false ;
        System.out.println( currentCrudOperationStock );
        if( currentCrudOperationStock == CurrentCrudOperation.ADD ){
             saved = stockService.add( stockEntity );
            System.out.println( "add" );
        }else{
             saved = stockService.update(stockEntity);
            System.out.println( "update" );
        }

        if( saved ){

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("l'enregistrement en Stock réussi");
            alert.setHeaderText("l'ordre Stock est bien enregistrer");
            alert.showAndWait();

            Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/StockView.fxml"));
            main.JavaFxApplication.mainStage.setScene(new Scene(root));
            main.JavaFxApplication.mainStage.setTitle(" Stock -- Aluminium et verre");
            main.JavaFxApplication.mainStage.show();

        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error D'enregistrement");
            alert.setHeaderText("Oups, il y a eu une erreur!");
            alert.showAndWait();
        }




    }


    public void goBack(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/StockView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" Stock -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();
    }



}
