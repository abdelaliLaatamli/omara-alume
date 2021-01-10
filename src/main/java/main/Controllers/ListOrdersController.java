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
import main.Models.entities.OrderEntity;
import main.Services.CommandService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class ListOrdersController implements Initializable {

    CommandService commandService = new CommandService();

    @FXML TableView<OrderEntity> listCommandTable;
    @FXML TableColumn<OrderEntity, String > referenceCommand;
    @FXML TableColumn<OrderEntity, String > dateCommand;
    @FXML TableColumn<OrderEntity, String > clientCommand;
    @FXML TableColumn<OrderEntity, String > paymentStatusCommand ;
    @FXML TableColumn<OrderEntity, String > paymentsMadesOfCommand;
    @FXML TableColumn<OrderEntity, String > totalPriceOfCommand;
    @FXML TableColumn<OrderEntity, Void > editCommand ;
    @FXML TableColumn<OrderEntity, Void > lockCommand;
    @FXML TableColumn<OrderEntity, Void > cancelOrder;
    @FXML TableColumn<OrderEntity, Void > printOrder;

    ObservableList<OrderEntity> observableCommand = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.loadData();
    }


    public void loadData(){

        List<OrderEntity> commands = commandService.getAllCommands();
        observableCommand.clear();
        observableCommand.addAll( commands );

        referenceCommand.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper( String.format("REF%010d", cellData.getValue().getId())  ));
        dateCommand.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper(
                DateTimeFormatter.ofPattern( "dd/MM/yyyy" ).withZone(ZoneId.systemDefault()).format(cellData.getValue().getOrderDate())
        ));


        clientCommand.setCellValueFactory( new PropertyValueFactory<>("client") );
        paymentStatusCommand.setCellValueFactory( new PropertyValueFactory<>("paymentStatus") );
        paymentsMadesOfCommand.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper( cellData.getValue()
                                                                            .getPaymentsMades()
                                                                            .stream()
                                                                            .map( c -> c.getAmountPaid() )
                                                                            .collect(Collectors.toList())
                                                                            .stream().reduce( 0f , ( subSum , currentElemnt ) -> subSum + currentElemnt )) );
        totalPriceOfCommand.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper( this.sumTotal( cellData.getValue() )));



        Callback<TableColumn<OrderEntity, Void>, TableCell<OrderEntity, Void>> editCellFactory = new Callback<TableColumn<OrderEntity, Void>, TableCell<OrderEntity, Void>>() {
            @Override
            public TableCell<OrderEntity, Void> call(final TableColumn<OrderEntity, Void> param) {
                final TableCell<OrderEntity, Void> cell = new TableCell<OrderEntity, Void>() {

                    private final Button btn = new Button("Edit");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            OrderEntity data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData Edit: " + data.getId());


                            try {

                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/OrderCreationView.fxml"));
                                Parent root = loader.load();
                                OrderCreationController controller = loader.<OrderCreationController>getController();
                                controller.setData(data);
                                main.JavaFxApplication.mainStage.setScene(new Scene(root));
                                main.JavaFxApplication.mainStage.setTitle("Command Generator -- Aluminium et verre");
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


        Callback<TableColumn<OrderEntity, Void>, TableCell<OrderEntity, Void>> lockCellFactory = new Callback<TableColumn<OrderEntity, Void>, TableCell<OrderEntity, Void>>() {
            @Override
            public TableCell<OrderEntity, Void> call(final TableColumn<OrderEntity, Void> param) {
                final TableCell<OrderEntity, Void> cell = new TableCell<OrderEntity, Void>() {

                    private final Button btn = new Button("Verrouiller");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            OrderEntity order = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData verrouiller: " + order.getId());
                            lockOrder( order );

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

        Callback<TableColumn<OrderEntity, Void>, TableCell<OrderEntity, Void>> cancelCellFactory = new Callback<TableColumn<OrderEntity, Void>, TableCell<OrderEntity, Void>>() {
            @Override
            public TableCell<OrderEntity, Void> call(final TableColumn<OrderEntity, Void> param) {
                final TableCell<OrderEntity, Void> cell = new TableCell<OrderEntity, Void>() {

                    private final Button btn = new Button("Annuler");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            OrderEntity order = getTableView().getItems().get(getIndex());
                            //System.out.println("selectedData Annuler : " + order.getId());
                            cancleOrder( order );

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

        Callback<TableColumn<OrderEntity, Void>, TableCell<OrderEntity, Void>> printCellFactory = new Callback<TableColumn<OrderEntity, Void>, TableCell<OrderEntity, Void>>() {
            @Override
            public TableCell<OrderEntity, Void> call(final TableColumn<OrderEntity, Void> param) {
                final TableCell<OrderEntity, Void> cell = new TableCell<OrderEntity, Void>() {

                    private final Button btn = new Button("Imprimer");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            OrderEntity order = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData print : " + order.getId());
                            //cancleOrder( order );
                            printOrder( order );

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

        editCommand.setCellFactory( editCellFactory );
        lockCommand.setCellFactory(lockCellFactory );
        cancelOrder.setCellFactory(cancelCellFactory );
        printOrder.setCellFactory( printCellFactory );
        listCommandTable.setItems( observableCommand );


    }

    private void printOrder( OrderEntity order ) throws JRException {
        System.out.println( order.getId() );

        HashMap m = new HashMap();
        m.put( "order_id" , order.getId() );

       // System.out.println( this.getClass().getResource("/main/views/invoice.jrxml") );

        JasperDesign jasperDesign = JRXmlLoader.load( this.getClass().getResource("/main/views/invoice.jrxml").toString() );
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport , m , );
    }

    private void cancleOrder( OrderEntity order ){

        order.setIsCanceled( true );

        boolean canceled = commandService.updateOrder( order );

        if (canceled) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Verrouillage du Command réussi");
            alert.setHeaderText("le Command est annulé");
            alert.showAndWait();
            this.loadData();

        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error De verrouillage");
            alert.setHeaderText("Oups, il y a eu une erreur!");
            alert.showAndWait();
        }

    }

    private void lockOrder( OrderEntity order ){
        order.setIsLocked( true );

        boolean canceled = commandService.updateOrder( order );

        if (canceled) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("annulation du Command réussi");
            alert.setHeaderText("le Command est annulé");
            alert.showAndWait();
            this.loadData();

        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error D'annulation");
            alert.setHeaderText("Oups, il y a eu une erreur!");
            alert.showAndWait();
        }

    }

    private Float sumTotal(OrderEntity client){
        float total = client.getArticleOrders()
                .stream()
                .map( n -> n.getPrice() * n.getQuantity() )
                .collect(Collectors.toList())
                .stream()
                .reduce( 0f , ( subTotal , currentPrice ) -> subTotal + currentPrice );

        return total;
    }

    public void goBack(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/MainView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Home -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }


    public void goToCommandGenerator(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/OrderCreationView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Command Generator -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();
    }


}
