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
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import main.Models.entities.queryContainers.StockItemStatus;
import main.Services.StockService;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class StockManagementController implements Initializable {

    StockService stockService = new StockService();

    @FXML TableView<StockItemStatus> tableOfInventory;
    @FXML TableColumn<StockItemStatus , String> nameOfProductColumn;
    @FXML TableColumn<StockItemStatus , String> stockStatusColumn;
    @FXML TableColumn<StockItemStatus , String> numberItemsInStockColumn;
    @FXML TableColumn<StockItemStatus , String> typeOfProductColumn ;
    @FXML TableColumn<StockItemStatus , Void> showDetailsColumn ;


    ObservableList<StockItemStatus> observableStock = FXCollections.observableArrayList();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadDataAndFill();
    }

    private void loadDataAndFill() {

        List<StockItemStatus> listStockItemStatus =  stockService.getStockProductStatus();

        observableStock.clear();
        observableStock.addAll( listStockItemStatus );

        nameOfProductColumn.setCellValueFactory( new PropertyValueFactory<>("article") );
        stockStatusColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper( String.format("%.2f", cellData.getValue().getInProducts())  ) );
        numberItemsInStockColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper( String.format("%.2f", cellData.getValue().getOutProducts())  )  );
        typeOfProductColumn.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper( getTypeOfProduct(cellData.getValue().getArticle().getType())  ) );


        Callback<TableColumn<StockItemStatus, Void>, TableCell<StockItemStatus, Void>> showDetailsCellFactory = new Callback<TableColumn<StockItemStatus, Void>, TableCell<StockItemStatus, Void>>() {
            @Override
            public TableCell<StockItemStatus, Void> call(final TableColumn<StockItemStatus, Void> param) {
                final TableCell<StockItemStatus, Void> cell = new TableCell<StockItemStatus, Void>() {

                    private final Button btn = new Button("Details");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            StockItemStatus order = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData Annuler : " + order.getArticle());

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

    private String getTypeOfProduct(String type) {
        switch ( type ){
            case "AluminumEntity" : return "Aluminium" ;
            case "AccessoryEntity": return "Accessoire" ;
            case "GlassEntity"    : return "Verre" ;
            default: return "autre" ;
        }


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
