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
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import main.Models.entities.OrderEntity;
import main.Models.entities.StockEntity;
import main.Services.StockService;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class StockController implements Initializable {

    final StockService stockService = new StockService();

    @FXML TableView<StockEntity> tableOfStockOrders;
    @FXML TableColumn<StockEntity , String> sNameOfStock;
    @FXML TableColumn<StockEntity , String> sProviderName;
    @FXML TableColumn<StockEntity , String> sTotalOrder;
    @FXML TableColumn<StockEntity , String> numberItems;
    @FXML TableColumn<StockEntity , String> importAt;
    @FXML TableColumn<StockEntity , Void> editProductOfCommand;
    @FXML TableColumn<StockEntity , Void> deleteProductOfCommand;

    ObservableList<StockEntity> observableList = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadData();
    }

    void loadData(){


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

                    private final Button btn = new Button("Edit");

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
                                main.JavaFxApplication.mainStage.setTitle("Ajouter Order -- Aluminium et verre");
                                main.JavaFxApplication.mainStage.show();


                            } catch (IOException e) {
                                e.printStackTrace();
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

                    private final Button btn = new Button("Delete");

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

        editProductOfCommand.setCellFactory( editCellFactory  );
        deleteProductOfCommand.setCellFactory(deleteCellFactory);

        tableOfStockOrders.setItems( observableList );


    }


    public void goBack(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/MainView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Home -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }


    public void goToAddToStock(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/AddItemsToStockView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Ajouter Order -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }


}
