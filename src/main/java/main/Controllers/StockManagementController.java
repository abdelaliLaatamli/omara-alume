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
import main.Models.entities.queryContainers.MovementArticle;
import main.Models.entities.queryContainers.StockItemCalculus;
import main.Models.entities.queryContainers.StockItemStatus;
import main.Models.enums.StockSearchProduct;
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        listStockItemStatus = stockService.getStockProductStatus();
        loadFieldPrograming();
        loadDataAndFill( listStockItemStatus );

        List<StockItemCalculus> listStockItemsCalculus = stockService.getStockItemsCalculus();
        Double dd =  listStockItemsCalculus
                .stream().filter( e -> ( e.getInProducts().intValue() - e.getOutProducts().intValue() ) > 0  )
                .map( e -> ( e.getInProducts().intValue() - e.getOutProducts().intValue() ) * e.getPriceOfBuy()  )
                .reduce( 0d , ( subSum , currentValue ) ->  subSum + currentValue  ) ;

        totalPriceInStock.setText( dd + " DH " );

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

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ListStockView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" Stock -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }
}
