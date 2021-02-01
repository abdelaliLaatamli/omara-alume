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
import javafx.scene.control.Button;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import main.Models.entities.OrderEntity;
import main.Models.utils.DBConnection;
import main.Services.OrderService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class ListOrdersController implements Initializable {

    OrderService orderService = new OrderService();

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

    public ListOrdersController() throws JRException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.loadData();
    }


    public void loadData(){

        List<OrderEntity> orders = orderService.getAllOrders();

        observableCommand.clear();
        observableCommand.addAll( orders );

        referenceCommand.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper( String.format("REF%08d", cellData.getValue().getId())  ));
        dateCommand.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper(
                DateTimeFormatter.ofPattern( "dd/MM/yyyy" ).withZone(ZoneId.systemDefault()).format(cellData.getValue().getOrderDate())
        ));


        clientCommand.setCellValueFactory( new PropertyValueFactory<>("client") );
        paymentStatusCommand.setCellValueFactory( new PropertyValueFactory<>("paymentStatus") );
        paymentsMadesOfCommand.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper( String.format( "%.2f DH" , cellData.getValue()
                                                                            .getPaymentsMades()
                                                                            .stream()
                                                                            .map( c -> c.getAmountPaid() )
                                                                            .collect(Collectors.toList())
                                                                            .stream().reduce( 0f , ( subSum , currentElement ) -> subSum + currentElement ))) );
        totalPriceOfCommand.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper( String.format( "%.2f DH" , this.sumTotal( cellData.getValue() ))));

        Callback<TableColumn<OrderEntity, Void>, TableCell<OrderEntity, Void>> editCellFactory = new Callback<TableColumn<OrderEntity, Void>, TableCell<OrderEntity, Void>>() {
            @Override
            public TableCell<OrderEntity, Void> call(final TableColumn<OrderEntity, Void> param) {
                final TableCell<OrderEntity, Void> cell = new TableCell<OrderEntity, Void>() {

                    private final Button btn = new Button("Modifier");
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

                            OrderEntity data = getTableView().getItems().get(getIndex());
                            btn.setDisable( data.getIsLocked() );

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
                            // System.out.println("selectedData verrouiller: " + order.getId());
                            // https://code.makery.ch/blog/javafx-dialogs-official/
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Verrouiller La commande ");
                            alert.setHeaderText("Êtes-vous d'accord pour verrouiller cette commande?");

                            Optional<ButtonType> result = alert.showAndWait();
                            if (result.get() == ButtonType.OK){
                                // ... user chose OK
                                // System.out.println("OK");
                                float total = order
                                                .getArticleOrders()
                                                .stream()
                                                .map( e -> e.getPrice() * e.getQuantity() )
                                                .reduce(0f , (subTotal , currentElement) -> subTotal + currentElement );

                                float amoutPaid = order
                                                    .getPaymentsMades()
                                                    .stream()
                                                    .map( e -> e.getAmountPaid() )
                                                    .reduce( 0f , (subTotal , currentElement) -> subTotal + currentElement  );


                                if( total - amoutPaid == 0 )
                                    lockOrder( order );
                                else{
                                    Alert alertNon = new Alert(Alert.AlertType.ERROR);
                                    alertNon.setTitle("Erreur de verrouillage");
                                    alertNon.setHeaderText("solde Le paiement n'est pas égal");
                                    alertNon.showAndWait();
                                }

                            }

                            // lockOrder( order );

                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            OrderEntity order = getTableView().getItems().get(getIndex());
                            btn.setDisable( order.getIsLocked() );
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

                            OrderEntity data = getTableView().getItems().get(getIndex());
                            btn.setDisable( data.getIsLocked() );

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

                            try {
                                printOrder( order );
                            }  catch (IOException e) {
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

        editCommand.setCellFactory( editCellFactory );
        lockCommand.setCellFactory(lockCellFactory );
        cancelOrder.setCellFactory(cancelCellFactory );
        printOrder.setCellFactory( printCellFactory );
        listCommandTable.setItems( observableCommand );


    }

    private void printOrder( OrderEntity order ) throws IOException {


        FileHandler handler = new FileHandler("default.log", true);
        Logger logger = Logger.getLogger("main");
        logger.addHandler(handler);
        InputStream logo = this.getClass().getClassLoader().getResourceAsStream("logo.PNG");
        try {

            HashMap m = new HashMap();
            m.put( "order_id" , order.getId() );
            m.put( "imageUrl" , logo );


            Class.forName(DBConnection.dbDRIVER);
            Connection con= DriverManager.getConnection(
                    DBConnection.dbURL,
                    DBConnection.dbUSER,
                    DBConnection.dbPASS
            );

            InputStream is = this.getClass().getResourceAsStream("/main/views/invoice.jrxml");
            JasperReport jasperReport = JasperCompileManager.compileReport( is );
            JasperPrint jasperPrint = JasperFillManager.fillReport( jasperReport , m , con );
            JasperViewer.viewReport( jasperPrint , false);


        } catch (JRException e) {
            logger.warning( e.getMessage() );
            logger.log( Level.WARNING , "JRException" , Arrays.stream(e.getStackTrace())
                    .map(s->s.toString())
                    .collect(Collectors.joining("\n")) /* e.getStackTrace().toString() */);
            e.printStackTrace();
        }catch (SQLException e) {
            logger.warning( e.getMessage() );
            logger.log( Level.WARNING , "SQLException" , Arrays.stream(e.getStackTrace())
                    .map(s->s.toString())
                    .collect(Collectors.joining("\n")) /* e.getStackTrace().toString() */);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            logger.warning( e.getMessage() );
            logger.log( Level.WARNING , "ClassNotFoundException" , Arrays.stream(e.getStackTrace())
                    .map(s->s.toString())
                    .collect(Collectors.joining("\n")) /* e.getStackTrace().toString() */);
            e.printStackTrace();
        } catch (Exception e){
            logger.warning( e.getMessage() );
            logger.log( Level.WARNING , "Exception" , Arrays.stream(e.getStackTrace())
                    .map(s->s.toString())
                    .collect(Collectors.joining("\n")) /* e.getStackTrace().toString() */);
                    System.out.println( e.getMessage() );
        }


    }

    private void cancleOrder( OrderEntity order ){

        order.setIsCanceled( true );

        boolean canceled = orderService.updateOrder( order );

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

        boolean canceled = orderService.updateOrder( order );

        if (canceled) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Verrouillage de commande réussi");
            alert.setHeaderText("l'Ordre est verrouillé");
            alert.showAndWait();
            this.loadData();

        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de verrouillage");
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
