package main.Controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import main.Models.entities.StockEntity;
import main.Models.entities.StockItemsEntity;
import main.Models.entities.queryContainers.MovementArticle;
import main.Models.entities.queryContainers.StockItemCalculus;
import main.Models.entities.queryContainers.StockItemStatus;
import main.Models.enums.StockSearchProduct;
import main.Models.utils.CurrentCrudOperation;
import main.Services.StockService;
import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class StockManagementController implements Initializable {

    StockService stockService = new StockService();

    @FXML TabPane tabStock;

    @FXML TableView<StockItemStatus> tableOfInventory;
    @FXML TableColumn<StockItemStatus , String> nameOfProductColumn;
    @FXML TableColumn<StockItemStatus , String> stockStatusColumn;
    @FXML TableColumn<StockItemStatus , String> numberItemsInStockColumn;
    @FXML TableColumn<StockItemStatus , String> typeOfProductColumn ;
    @FXML TableColumn<StockItemStatus , Void> showDetailsColumn ;

    ObservableList<StockItemStatus> observableStock = FXCollections.observableArrayList();

    @FXML ComboBox<StockSearchProduct> productTypeSearch;
    @FXML TextField searchProductByName;


    @FXML TableView<MovementArticle> tableMovement;
    @FXML TableColumn<MovementArticle , String> dateMovementColumn;
    @FXML TableColumn<MovementArticle , String> typeMovementColumn;
    @FXML TableColumn<MovementArticle , String> referenceMovementColumn;
    @FXML TableColumn<MovementArticle , String> quantityMovementColumn;
    @FXML TableColumn<MovementArticle , String> priceUnitColumn;

    ObservableList<MovementArticle> observableMovement = FXCollections.observableArrayList();

    List<StockItemStatus> listStockItemStatus = new ArrayList<>();

    @FXML Label totalPriceInStock ;

//    --------------------- Entree Tab -----------------

    @FXML TableView<StockEntity> tableOfStockOrders;
    @FXML TableColumn<StockEntity , String> sNameOfStock;
    @FXML TableColumn<StockEntity , String> sProviderName;
    @FXML TableColumn<StockEntity , String> sTotalOrder;
    @FXML TableColumn<StockEntity , String> numberItems;
    @FXML TableColumn<StockEntity , String> importAt;
    @FXML TableColumn<StockEntity , Void> editProductOfCommand;
    @FXML TableColumn<StockEntity , Void> deleteProductOfCommand;
    @FXML TableColumn<StockEntity , Void> showDetailsOfCommand;


    ObservableList<StockEntity> observableList = FXCollections.observableArrayList();


    @FXML TableView<StockItemsEntity> tableArticlesOfStockOrder;
    @FXML TableColumn<StockItemsEntity , String> sProductName;
    @FXML TableColumn<StockItemsEntity , String> sProductsQuentityColumn;
    @FXML TableColumn<StockItemsEntity , String> sPriceOfBuyColumn;
    @FXML TableColumn<StockItemsEntity , String> sTotalArticle;

    ObservableList<StockItemsEntity> observableStockItemsDetails = FXCollections.observableArrayList();

    public void setOpenTab(){
        tabStock.getSelectionModel().select(1);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        FillInventoryTab();


        tableOfStockOrders.getItems().clear();
        observableList.setAll( stockService.getAll() );

        sNameOfStock.setCellValueFactory( new PropertyValueFactory<>("name") );
        sProviderName.setCellValueFactory( cellData -> new ReadOnlyStringWrapper( cellData.getValue().getProvider().getName() ) );
        sTotalOrder.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper(
                cellData.getValue().getStockItems().stream().map( c -> c.getPriceOfBuy() * c.getQuantity() ).reduce( 0f , (subTotal , currentItem) -> subTotal + currentItem ) + " DH"
        ) );
        numberItems.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper( cellData.getValue().getStockItems().size() ) );
        importAt.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper(
                DateTimeFormatter.ofPattern( "dd/MM/yyyy" ).withZone(ZoneId.systemDefault()).format(cellData.getValue().getImportedAt())
        ) );

        Callback<TableColumn<StockEntity, Void>, TableCell<StockEntity, Void>> editCellFactory = new Callback<TableColumn<StockEntity, Void>, TableCell<StockEntity, Void>>() {
            @Override
            public TableCell<StockEntity, Void> call(final TableColumn<StockEntity, Void> param) {
                final TableCell<StockEntity, Void> cell = new TableCell<StockEntity, Void>() {

                    private final Button btn = new Button("Modifier");

                    {
                        btn.setOnAction((ActionEvent event) -> {

                            StockEntity data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData Edit: " + data.getId());

                            try {

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/AddItemsToStockView.fxml"));
                                Parent root = loader.load();
                                AddItemsToStockController controller = loader.<AddItemsToStockController>getController();
                                controller.setData(data);
                                main.JavaFxApplication.mainStage.setScene(new Scene(root));
                                main.JavaFxApplication.mainStage.setTitle("Edit Order -- Aluminium et verre");
                                main.JavaFxApplication.mainStage.show();


                            } catch (IOException e) {

                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Exception Dialog");
                                alert.setHeaderText("Look, an Exception Dialog");
                                alert.setContentText("Could not find file blabla.txt!");

                                String exceptionText = e.toString();

                                Label label = new Label("The exception stacktrace was:");

                                TextArea textArea = new TextArea(exceptionText);
                                textArea.setEditable(false);
                                textArea.setWrapText(true);

                                textArea.setMaxWidth(Double.MAX_VALUE);
                                textArea.setMaxHeight(Double.MAX_VALUE);
                                GridPane.setVgrow(textArea, Priority.ALWAYS);
                                GridPane.setHgrow(textArea, Priority.ALWAYS);

                                GridPane expContent = new GridPane();
                                expContent.setMaxWidth(Double.MAX_VALUE);
                                expContent.add(label, 0, 0);
                                expContent.add(textArea, 0, 1);

                                alert.getDialogPane().setExpandableContent(expContent);

                                alert.showAndWait();
                            }

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


        Callback<TableColumn<StockEntity, Void>, TableCell<StockEntity, Void>> deleteCellFactory = new Callback<TableColumn<StockEntity, Void>, TableCell<StockEntity, Void>>() {
            @Override
            public TableCell<StockEntity, Void> call(final TableColumn<StockEntity, Void> param) {
                final TableCell<StockEntity, Void> cell = new TableCell<StockEntity, Void>() {

                    private final Button btn = new Button("Supprimer");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            StockEntity order = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData Delete: " + order.getId());

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

        Callback<TableColumn<StockEntity, Void>, TableCell<StockEntity, Void>> detailsCellFactory = new Callback<TableColumn<StockEntity, Void>, TableCell<StockEntity, Void>>() {
            @Override
            public TableCell<StockEntity, Void> call(final TableColumn<StockEntity, Void> param) {
                final TableCell<StockEntity, Void> cell = new TableCell<StockEntity, Void>() {

                    private final Button btn = new Button("Details");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            StockEntity order = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData Details: " + order.getId());

                            observableStockItemsDetails.clear();
                            observableStockItemsDetails.addAll( order.getStockItems() );

                            sProductName.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(  cellData.getValue().getArticle().getName() ));
                            sPriceOfBuyColumn.setCellValueFactory(new PropertyValueFactory<>("priceOfBuy"));
                            sProductsQuentityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
                            sTotalArticle.setCellValueFactory( cellData ->  new ReadOnlyObjectWrapper(  cellData.getValue().getQuantity() * cellData.getValue().getPriceOfBuy() + " DH "  ) );

                            tableArticlesOfStockOrder.setItems( observableStockItemsDetails );


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

        editProductOfCommand.setCellFactory( editCellFactory  );
        deleteProductOfCommand.setCellFactory(deleteCellFactory);
        showDetailsOfCommand.setCellFactory(detailsCellFactory);


        tableOfStockOrders.setItems( observableList );


    }

    private void FillInventoryTab() {
        listStockItemStatus = stockService.getStockProductStatus();
        loadFieldPrograming();
        loadDataAndFill( listStockItemStatus );

        List<StockItemCalculus> listStockItemsCalculus = stockService.getStockItemsCalculus();
        Double totalPriceStock =  listStockItemsCalculus
                .stream().filter( e -> ( e.getInProducts().intValue() - e.getOutProducts().intValue() ) > 0  )
                .map( e -> ( e.getInProducts().intValue() - e.getOutProducts().intValue() ) * e.getPriceOfBuy()  )
                .reduce( 0d , ( subSum , currentValue ) ->  subSum + currentValue  ) ;

        totalPriceInStock.setText( totalPriceStock + " DH " );
    }

    private void loadFieldPrograming() {
        productTypeSearch.setItems( FXCollections.observableArrayList( StockSearchProduct.values() ) );
        productTypeSearch.getSelectionModel().selectFirst();

        productTypeSearch.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {

            switch ( newValue ){
                case ALL:
                    loadDataAndFill( listStockItemStatus );
                    searchProductByName.setText("");
                    break;
                case ALUMINIUM:
                    loadDataAndFill( listStockItemStatus.stream().filter( e -> e.getArticle().getType() == StockSearchProduct.ALUMINIUM ).collect(Collectors.toList()) );
                    searchProductByName.setText("");
                    break;
                case ACCESSOIRE:
                    loadDataAndFill( listStockItemStatus.stream().filter( e -> e.getArticle().getType() == StockSearchProduct.ACCESSOIRE ).collect(Collectors.toList()) );
                    searchProductByName.setText("");
                    break;
                case VERRE:
                    loadDataAndFill( listStockItemStatus.stream().filter( e -> e.getArticle().getType() == StockSearchProduct.VERRE  ).collect(Collectors.toList()) );
                    searchProductByName.setText("");
                    break;
            }

        } );

        searchProductByName.textProperty().addListener((options, oldValue, newValue) -> {

            List<StockItemStatus> filteredListStockItemStatus = listStockItemStatus.stream().
                    filter( e -> e.getArticle()
                                    .getName()
                                    .toLowerCase(Locale.ROOT)
                                    .contains( newValue.toLowerCase(Locale.ROOT) )
                                    && (
                                            productTypeSearch.getSelectionModel().getSelectedItem() == StockSearchProduct.ALL ||
                                             e.getArticle().getType() == productTypeSearch.getSelectionModel().getSelectedItem()
                                    )
                    ).collect(Collectors.toList());

            loadDataAndFill( filteredListStockItemStatus );

        });

    }

    private void loadDataAndFill( List<StockItemStatus> filteredListStockItemStatus) {

        observableStock.clear();
        observableStock.addAll( filteredListStockItemStatus );

        nameOfProductColumn.setCellValueFactory( new PropertyValueFactory<>("article") );
        stockStatusColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
                ( cellData.getValue().getInProducts() - cellData.getValue().getOutProducts() > 0 ) ? "Article en stock" : "Article épuisé" ) );
        numberItemsInStockColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
                String.format("%.2f", cellData.getValue().getInProducts() - cellData.getValue().getOutProducts() )  )
        );
        typeOfProductColumn.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper( cellData.getValue().getArticle().getType() ) );

        Callback<TableColumn<StockItemStatus, Void>, TableCell<StockItemStatus, Void>> showDetailsCellFactory = new Callback<TableColumn<StockItemStatus, Void>, TableCell<StockItemStatus, Void>>() {
            @Override
            public TableCell<StockItemStatus, Void> call(final TableColumn<StockItemStatus, Void> param) {
                final TableCell<StockItemStatus, Void> cell = new TableCell<StockItemStatus, Void>() {

                    private final Button btn = new Button("Details");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            StockItemStatus stockItemStatus = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData Details : " + stockItemStatus.getArticle());
                            loadMovementTable( stockItemStatus );
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

        showDetailsColumn.setCellFactory( showDetailsCellFactory );
        tableOfInventory.setItems( observableStock );


    }

    private void loadMovementTable( StockItemStatus stockItemStatus ) {

        List<MovementArticle> movementArticles = stockService.getMovementProductInStock(stockItemStatus.getArticle().getId());

        observableMovement.clear();
        observableMovement.addAll( movementArticles );

        dateMovementColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
                DateTimeFormatter.ofPattern( "dd/MM/yyyy" ).withZone(ZoneId.systemDefault()).format(cellData.getValue().getDate())
        ));

        typeMovementColumn.setCellValueFactory( new PropertyValueFactory<>("type") );

        referenceMovementColumn.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper(
                cellData.getValue().getType().equals( "sortie" ) ?
                        String.format("REF%08d", Integer.valueOf( cellData.getValue().getReference() ) ) :
                        cellData.getValue().getReference() )
        );

        quantityMovementColumn.setCellValueFactory( new PropertyValueFactory<>("quantity") );
        priceUnitColumn.setCellValueFactory( new PropertyValueFactory<>("price") );

        tableMovement.setItems( observableMovement );

    }


    public void goBack(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/MainView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Home -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }


    public void goToStockView(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/AddItemsToStockView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Ajouter Order -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }
}
