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
import javafx.util.Callback;
import main.Models.entities.*;
import main.Models.entities.queryContainers.StockArticleItems;
import main.Models.enums.PayementMethod;
import main.Models.enums.PaymentStatus;
import main.Models.utils.CurrentCrudOperation;
import main.Services.*;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OrderCreationController implements Initializable {

    private final OrderService orderService = new OrderService();
    private final ClientServices clientServices = new ClientServices();
    private final AccessoryService accessoryService = new AccessoryService();
    private final AluminumService aluminumService = new AluminumService();
    private final GlassService glassService = new GlassService();
    private final PriceService priceService = new PriceService();
    private final StockService stockService = new StockService();

    private CurrentCrudOperation operation = CurrentCrudOperation.ADD;
    private CurrentCrudOperation operationOrder = CurrentCrudOperation.ADD ;
    private CurrentCrudOperation operationPayment = CurrentCrudOperation.ADD;

    @FXML ComboBox<ClientEntity> clientNameForm ;
    @FXML Label totalPriceOrder;
    @FXML Label amountPaidOrder;
    @FXML Label amountRemainedOrder;
    @FXML ComboBox<PaymentStatus> comboPaymentStatus;
    @FXML TextField amountToPayText ;

    @FXML ToggleGroup paymentMethodGroup;
    @FXML RadioButton cashToggleButton;
    @FXML RadioButton chequeToggleButton;

    @FXML Label orderReference;
    @FXML Button savePayment;


    // ---------- Aluminum Tab -------------
    @FXML ComboBox<AluminumEntity> aluminumProduct;
    @FXML TextField aluminumLabel;
    @FXML ComboBox<Object> priceAluminumCombo ;
    @FXML TextField aluminumQuantity;
    @FXML Label priceAluminumShow;
    @FXML ComboBox<StockArticleItems> comboAlumStockArticle;

    // ------------ Accessory Tab --------

    @FXML ComboBox<AccessoryEntity> accessoryProduct;
    @FXML TextField accessoryLabel;
    @FXML ComboBox<Object> accessoryPrice;
    @FXML TextField accessoryQuantity;
    @FXML Label accessoryTotal;
    @FXML ComboBox<StockArticleItems> comboAccesStockArticle;
    // --------------- GlassTab  -----------------

    @FXML ComboBox<GlassEntity> glassProduct;
    @FXML TextField glassLabel ;
    @FXML ComboBox<Object> glassPrice ;
    @FXML TextField glassQuantity;
    @FXML Label glassTotal ;
    @FXML Spinner<Integer> numberPieceGlass;
    @FXML ComboBox<StockArticleItems> comboGlassStockArticle;

    // --------- TABPANE Switch Products-----------

    @FXML TabPane tabPaneAddProducts;

    // ------------Table Payment-------------

    @FXML TableView<PaymentsMadeEntity> tableOfPayments;
    @FXML TableColumn<PaymentsMadeEntity , String> dateOfPayment;
    @FXML TableColumn<PaymentsMadeEntity , String> modeOfPayment;
    @FXML TableColumn<PaymentsMadeEntity , String> paymentAmount;
    @FXML TableColumn<PaymentsMadeEntity , Void> editPayment;
    @FXML TableColumn<PaymentsMadeEntity , Void> deletePayment;
    ObservableList<PaymentsMadeEntity> observablePaymentsMade = FXCollections.observableArrayList();

    // -----------Table Products------------

    @FXML TableView<OrderItemsEntity> tableProductsOfOrder;
    @FXML TableColumn<OrderItemsEntity, String> labelOfOrder;
    @FXML TableColumn<OrderItemsEntity, String> priceProductOfOrder;
    @FXML TableColumn<OrderItemsEntity, String> quantityProductOfOrder;
    @FXML TableColumn<OrderItemsEntity, String> priceOrder;
    @FXML TableColumn<OrderItemsEntity, Void> editProductOfOrder;
    @FXML TableColumn<OrderItemsEntity, Void> deleteProductOfOrder;
    ObservableList<OrderItemsEntity> observableArticleOrder = FXCollections.observableArrayList();

    public OrderEntity orderDetails;
    public OrderEntity orderDetailsReal;
    private float payedMount ;
    private OrderItemsEntity editableOrderArticle = null;
    private PaymentsMadeEntity editablePaymentMade = null ;


    public void setData( OrderEntity entity ){
        operationOrder = CurrentCrudOperation.EDIT;

        payedMount = entity.getPaymentsMades().stream().map( n -> n.getAmountPaid() ).reduce( 0f , (sub , elem) -> sub+elem );

        orderReference.setText(  String.format("REF%08d", entity.getId() ) );

        orderDetails = entity;

        orderDetailsReal = orderService.getOrder( entity.getId() );

        loadDataEdit();
        loadPaymentTable();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        orderDetails = new OrderEntity();
        loadDataAdd();
        loadPaymentTable();

    }



    void loadDataAdd(){

        List<ClientEntity> clients = clientServices.getAll();
        clientNameForm.setItems( FXCollections.observableArrayList( clients ) );

        new AutoCompleteBox(clientNameForm);

        clientNameForm.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {
            productChosen(aluminumProduct  , priceAluminumCombo );
            productChosen(accessoryProduct , accessoryPrice);
            productChosen( glassProduct    , glassPrice );
        } );

        comboPaymentStatus.setItems( FXCollections.observableArrayList( PaymentStatus.values() ) );
        comboPaymentStatus.getSelectionModel().selectFirst();


        amountToPayText.textProperty().addListener( (observable, oldValue, newValue) -> {


            float totalPaid = orderDetails
                    .getPaymentsMades()
                    .stream()
                    .map( e -> e.getAmountPaid() )
                    .reduce( 0f , ( subTotal , element ) -> subTotal + element );

            float totalOrder = orderDetails
                    .getArticleOrders()
                    .stream()
                    .map( e -> e.getPrice() * e.getQuantity() )
                    .reduce( 0f , ( subTotal , element ) -> subTotal + element );

            float number ;
            try{
                number = Float.parseFloat( newValue ) ;
            }catch (Exception e) {
                number = 0f;
            }

            if( number > 0 && number < totalOrder - totalPaid  && operationPayment == CurrentCrudOperation.ADD ){
                comboPaymentStatus.getSelectionModel().select( PaymentStatus.PARTRANCHES );
                savePayment.setDisable( false );
            } else if(totalOrder - totalPaid == 0 && operationPayment == CurrentCrudOperation.ADD){
                savePayment.setDisable( true );
            } else if( number >= totalOrder - totalPaid  && operationPayment == CurrentCrudOperation.ADD ){
                comboPaymentStatus.getSelectionModel().select( PaymentStatus.PAYÉ );
                amountToPayText.setText( totalOrder - totalPaid  + "" );
                savePayment.setDisable( false );
            }else if(operationPayment == CurrentCrudOperation.ADD){
                comboPaymentStatus.getSelectionModel().select( PaymentStatus.CRÉDIT );
                savePayment.setDisable( true );
            }


        } );


        initialiseAluminumTab();
        initialiseAccessorTab();
        initialiseGlassTab();

    }

    private void loadPaymentTable() {
        
        observablePaymentsMade.clear();
        observablePaymentsMade.addAll( orderDetails.getPaymentsMades() );

        modeOfPayment.setCellValueFactory( new PropertyValueFactory<>("payementMethod"));
        paymentAmount.setCellValueFactory(  new PropertyValueFactory<>("amountPaid"));
        dateOfPayment.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
                DateTimeFormatter.ofPattern( "dd/MM/yyyy" ).withZone(ZoneId.systemDefault()).format(  cellData.getValue().getPaymentDate() ))
        );

        Callback<TableColumn<PaymentsMadeEntity, Void>, TableCell<PaymentsMadeEntity, Void>> editCellFactory = new Callback<TableColumn<PaymentsMadeEntity, Void>, TableCell<PaymentsMadeEntity, Void>>() {
            @Override
            public TableCell<PaymentsMadeEntity, Void> call(final TableColumn<PaymentsMadeEntity, Void> param) {
                return new TableCell<PaymentsMadeEntity, Void>() {

                    private final Button btn = new Button("Modifier");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            PaymentsMadeEntity data = getTableView().getItems().get(getIndex());
                            amountToPayText.setText(String.valueOf(data.getAmountPaid()));
                            paymentMethodGroup.selectToggle(
                                    ( data.getPayementMethod() == PayementMethod.CHEQUE ) ? chequeToggleButton: cashToggleButton
                            );
                            savePayment.setDisable( false );
                            editablePaymentMade = data ;
                            operationPayment = CurrentCrudOperation.EDIT;
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
            }
        };


        Callback<TableColumn<PaymentsMadeEntity, Void>, TableCell<PaymentsMadeEntity, Void>> deleteCellFactory = new Callback<TableColumn<PaymentsMadeEntity, Void>, TableCell<PaymentsMadeEntity, Void>>() {
            @Override
            public TableCell<PaymentsMadeEntity, Void> call(final TableColumn<PaymentsMadeEntity, Void> param) {
                return new TableCell<PaymentsMadeEntity, Void>() {

                    private final Button btn = new Button("Delete");
                    {

                        btn.setOnAction((ActionEvent event) -> {
                            PaymentsMadeEntity data = getTableView().getItems().get(getIndex());
                            orderDetails.getPaymentsMades().remove( data );
                            loadPaymentTable();
                            loadPaymentLabelsValues();

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

            }
        };

        editPayment.setCellFactory( editCellFactory );
        deletePayment.setCellFactory(deleteCellFactory );

        tableOfPayments.setItems( this.observablePaymentsMade);
    }

    void loadDataEdit(){

        if( orderDetails.getClient() != null ) {
            ClientEntity clientEntity = clientNameForm.getItems().stream().filter(e -> e.getId() == orderDetails.getClient().getId()).findFirst().get();
            clientNameForm.getSelectionModel().select(clientEntity);
            clientNameForm.setDisable(true);
        }

        comboPaymentStatus.setItems( FXCollections.observableArrayList( PaymentStatus.values() ) );
        comboPaymentStatus.getSelectionModel().select( orderDetails.getPaymentStatus() );

        this.loadDataTable();

    }

    private void initialiseGlassTab() {

        numberPieceGlass.setValueFactory( new SpinnerValueFactory.IntegerSpinnerValueFactory(1 , 1000 , 1) );

        glassProduct.setItems( FXCollections.observableArrayList( glassService.getAllGlassProducts() ) );

        new AutoCompleteBox(glassProduct);

        glassProduct.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {
            productChosen( glassProduct , glassPrice );
        });

        glassPrice.valueProperty().addListener(  (options, oldValue, newValue) -> {

            float number ;
            try{
                number = Float.valueOf( glassQuantity.getText().equals("") ? "0" : glassQuantity.getText() );
            }catch (Exception e) {
                number = 1f;
            }

            glassTotal.setText( String.format( "%.2f DH" , number * (
                    ( glassPrice.getSelectionModel().getSelectedItem() == null ) ?
                            0 : getPrice(glassPrice)
            )));

        });

        glassQuantity.textProperty().addListener( (observable, oldValue, newValue) -> {

            float number ;
            try{
                number = Float.valueOf( newValue.equals("") ? "0" : newValue );
            }catch (Exception e) {
                number = 1f;
            }

            if( comboGlassStockArticle.getSelectionModel().getSelectedIndex() != -1 && operationOrder != CurrentCrudOperation.EDIT ) {

                StockArticleItems stockArticleItems = comboGlassStockArticle.getSelectionModel().getSelectedItem();

                if( number > stockArticleItems.getStockItems().getQuantity() - stockArticleItems.getSold() ) {
                    glassQuantity.setText( String.valueOf( stockArticleItems.getStockItems().getQuantity() - stockArticleItems.getSold() ) );
                }
            }

            glassTotal.setText( String.format( "%.2f DH" , number * (
                    ( glassPrice.getSelectionModel().getSelectedItem() == null ) ?
                            0 : getPrice(glassPrice)
            )));

        } );

        comboGlassStockArticle.valueProperty().addListener(  (observable, oldValue, newValue) -> {

            float number ;
            try{
                number = Float.valueOf( glassQuantity.getText().equals("") ? "0" : glassQuantity.getText() );
            }catch (Exception e) {
                number = 0f;
            }
            if( comboGlassStockArticle.getSelectionModel().getSelectedIndex() != -1 ) {

                StockArticleItems stockArticleItems = comboGlassStockArticle.getSelectionModel().getSelectedItem();

                if( number > stockArticleItems.getStockItems().getQuantity() - stockArticleItems.getSold() ) {
                    glassQuantity.setText( String.valueOf( stockArticleItems.getStockItems().getQuantity() - stockArticleItems.getSold() ) );
                }
            }

        } );
    }

    private void initialiseAccessorTab() {
        accessoryProduct.setItems( FXCollections.observableArrayList(accessoryService.getAllAccessoryProducts()) );
        new AutoCompleteBox(accessoryProduct);
        accessoryProduct.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {
            productChosen(accessoryProduct, accessoryPrice);
        });

        accessoryPrice.valueProperty().addListener(  (options, oldValue, newValue) -> {

            float number ;
            try{
                number = Float.valueOf( accessoryQuantity.getText().equals("") ? "0" : accessoryQuantity.getText() );
            }catch (Exception e) {
                number = 1f;
            }

            accessoryTotal.setText( number * (
                    ( accessoryPrice.getSelectionModel().getSelectedItem() == null) ?
                            0 : getPrice(accessoryPrice)
            ) + " DH");

        });

        accessoryQuantity.textProperty().addListener( (observable, oldValue, newValue) -> {

            float number;
            try{
                number = Float.valueOf( newValue.equals("") ? "0" : newValue );
            }catch (Exception e) {
                number = 1f;
            }


            if( comboAccesStockArticle.getSelectionModel().getSelectedIndex() != -1 && operationOrder != CurrentCrudOperation.EDIT ) {

                StockArticleItems stockArticleItems = comboAccesStockArticle.getSelectionModel().getSelectedItem();

                if( number > stockArticleItems.getStockItems().getQuantity() - stockArticleItems.getSold() ) {
                    accessoryQuantity.setText( String.valueOf( stockArticleItems.getStockItems().getQuantity() - stockArticleItems.getSold() ) );
                }
            }

            accessoryTotal.setText( number * (
                    ( accessoryPrice.getSelectionModel().getSelectedItem() == null ) ?
                            0 : getPrice(accessoryPrice)
            ) + " DH");

        } );

        comboAccesStockArticle.valueProperty().addListener(  (observable, oldValue, newValue) -> {

            float number ;
            try{
                number = Float.valueOf( accessoryQuantity.getText().equals("") ? "0" : accessoryQuantity.getText() );
            }catch (Exception e) {
                number = 0f;
            }
            if( comboAccesStockArticle.getSelectionModel().getSelectedIndex() != -1 ) {

                StockArticleItems stockArticleItems = comboAccesStockArticle.getSelectionModel().getSelectedItem();

                if( number > stockArticleItems.getStockItems().getQuantity() - stockArticleItems.getSold() ) {
                    accessoryQuantity.setText( String.valueOf( stockArticleItems.getStockItems().getQuantity() - stockArticleItems.getSold() ) );
                }
            }

        } );
    }

    private void initialiseAluminumTab() {

        aluminumProduct.setItems( FXCollections.observableArrayList( aluminumService.getAllAlumunuimProducts() ) );
        new AutoCompleteBox(aluminumProduct);
        aluminumProduct.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {
            productChosen(aluminumProduct, priceAluminumCombo );
        });

        priceAluminumCombo.valueProperty().addListener( (options, oldValue, newValue) -> {

            float number ;
            try{
                number = Float.valueOf( aluminumQuantity.getText().equals("") ? "0" : aluminumQuantity.getText() );
            }catch (Exception e) {
                number = 1f;
            }

            float total = number * (
                    ( priceAluminumCombo.getSelectionModel().getSelectedItem() == null ) ?
                            0 : getPrice(priceAluminumCombo)
            );

            priceAluminumShow.setText( total + " DH");

        });

        aluminumQuantity.textProperty().addListener( (observable, oldValue, newValue) -> {

            float number;
            try{
                number = Float.valueOf( newValue.equals("") ? "0" : newValue );
            }catch (Exception e) {
                number = 0f;
            }

            if( comboAlumStockArticle.getSelectionModel().getSelectedIndex() != -1 && operationOrder != CurrentCrudOperation.EDIT ) {

                StockArticleItems stockArticleItems = comboAlumStockArticle.getSelectionModel().getSelectedItem();

                if( number > stockArticleItems.getStockItems().getQuantity() - stockArticleItems.getSold() ) {
                    aluminumQuantity.setText( String.valueOf( stockArticleItems.getStockItems().getQuantity() - stockArticleItems.getSold() ) );
                }
            }


            float total = number * (
                    ( priceAluminumCombo.getSelectionModel().getSelectedItem() == null ) ?
                            0 : getPrice( priceAluminumCombo )
            );
            priceAluminumShow.setText( total + " DH");
        } );

        comboAlumStockArticle.valueProperty().addListener(  (observable, oldValue, newValue) -> {

            float number ;
            try{
                number = Float.valueOf( aluminumQuantity.getText().equals("") ? "0" : aluminumQuantity.getText() );
            }catch (Exception e) {
                number = 0f;
            }
            if( comboAlumStockArticle.getSelectionModel().getSelectedIndex() != -1 ) {

                StockArticleItems stockArticleItems = comboAlumStockArticle.getSelectionModel().getSelectedItem();

                if( number > stockArticleItems.getStockItems().getQuantity() - stockArticleItems.getSold() ) {
                    aluminumQuantity.setText( String.valueOf( stockArticleItems.getStockItems().getQuantity() - stockArticleItems.getSold() ) );
                }
            }

        } );


    }

    private void productChosen(ComboBox productCombo , ComboBox priceCombo) {
        priceCombo.getItems().clear();

        if( productCombo.getSelectionModel().getSelectedIndex() != -1 ){
            ClientEntity clientChosen = ( clientNameForm.getSelectionModel().getSelectedIndex() != -1 ) ?
                                            clientNameForm.getItems().get( clientNameForm.getSelectionModel().getSelectedIndex() ) : null ;

            List<PriceEntity> listPrices = ( (ArticleEntity) productCombo
                                                    .getItems()
                                                    .get( productCombo
                                                            .getSelectionModel()
                                                            .getSelectedIndex() )  )
                                                .getPrices()
                                                .stream()
                                                .filter(e -> e.getClient() == null ||
                                                        ( clientChosen != null && clientChosen.getId() == e.getClient().getId() )
                                                ).collect(Collectors.toList());

            Collections.sort( listPrices );

            priceCombo.setItems( FXCollections.observableArrayList( listPrices ) );
            priceCombo.getSelectionModel().selectFirst();


            if( productCombo == aluminumProduct )
                loadChangeDataAlumStatus(productCombo);
            else if( productCombo == accessoryProduct )
                loadChangeDataAccesStatus(productCombo);
            else if( productCombo == glassProduct ){
                loadChangeDataGlassStatus(productCombo);
            }



        }
    }

    private void loadChangeDataAlumStatus(ComboBox productCombo) {

        AluminumEntity aluminumEntity =  ( productCombo.getSelectionModel().getSelectedIndex() != -1 ) ?
                (AluminumEntity) productCombo.getItems().get( productCombo.getSelectionModel().getSelectedIndex() ):
                (AluminumEntity) productCombo.getSelectionModel().getSelectedItem();

        if( aluminumEntity == null ) return;

        List<StockArticleItems> listProductsStockStatus ;
        if( operationOrder == CurrentCrudOperation.ADD ){
            listProductsStockStatus = stockService.getProductsStockStatus( aluminumEntity.getId() ).stream()
                    .filter( e -> e.getStockItems().getQuantity() - e.getSold() >= 0 )
                    .map(
                            e -> {
                                for ( OrderItemsEntity orderItems:  orderDetails.getArticleOrders() ) {
                                    if( orderItems.getStockItemId() == e.getStockItems().getId() )
                                        e.setSold( e.getSold() + orderItems.getQuantity() );
                                }

                                return e ;
                            }).collect(Collectors.toList());
        }else{
            listProductsStockStatus = getStockArticleItemsUpdate( stockService.getProductsStockStatus( aluminumEntity.getId() ) ) ;
        }

        Collections.sort(listProductsStockStatus);

        comboAlumStockArticle.getItems().clear();
        comboAlumStockArticle.setItems( FXCollections.observableArrayList( listProductsStockStatus ) );
        comboAlumStockArticle.getSelectionModel().selectFirst();
    }

    private void loadChangeDataAccesStatus( ComboBox productCombo ) {

        AccessoryEntity accessoryEntity =  ( productCombo.getSelectionModel().getSelectedIndex() != -1 ) ?
                (AccessoryEntity) productCombo.getItems().get( productCombo.getSelectionModel().getSelectedIndex() ):
                (AccessoryEntity) productCombo.getSelectionModel().getSelectedItem();

        if( accessoryEntity == null ) return;

        List<StockArticleItems> listProductsStockStatus ;
        if( operationOrder == CurrentCrudOperation.ADD ){
            listProductsStockStatus = stockService.getProductsStockStatus( accessoryEntity.getId() ).stream()
                    .filter( e -> e.getStockItems().getQuantity() - e.getSold() >= 0 )
                    .map(
                            e -> {
                                for ( OrderItemsEntity orderItems:  orderDetails.getArticleOrders() ) {
                                    if( orderItems.getStockItemId() == e.getStockItems().getId() )
                                        e.setSold( e.getSold() + orderItems.getQuantity() );
                                }

                                return e ;
                            }).collect(Collectors.toList());
        }else{
            listProductsStockStatus = getStockArticleItemsUpdate( stockService.getProductsStockStatus( accessoryEntity.getId() ) ) ;
        }

        Collections.sort(listProductsStockStatus);

        comboAccesStockArticle.getItems().clear();
        comboAccesStockArticle.setItems( FXCollections.observableArrayList( listProductsStockStatus ) );
        comboAccesStockArticle.getSelectionModel().selectFirst();
    }

    private void loadChangeDataGlassStatus( ComboBox productCombo ) {

        GlassEntity glassEntity =  ( productCombo.getSelectionModel().getSelectedIndex() != -1 ) ?
                (GlassEntity) productCombo.getItems().get( productCombo.getSelectionModel().getSelectedIndex() ):
                (GlassEntity) productCombo.getSelectionModel().getSelectedItem();

        if( glassEntity == null ) return;

        List<StockArticleItems> listProductsStockStatus ;
        if( operationOrder == CurrentCrudOperation.ADD ){
            listProductsStockStatus = stockService.getProductsStockStatus( glassEntity.getId() ).stream()
                    .filter( e -> e.getStockItems().getQuantity() - e.getSold() >= 0 )
                    .map(
                            e -> {
                                for ( OrderItemsEntity orderItems:  orderDetails.getArticleOrders() ) {
                                    if( orderItems.getStockItemId() == e.getStockItems().getId() )
                                        e.setSold( e.getSold() + orderItems.getQuantity() );
                                }

                                return e ;
                            }).collect(Collectors.toList());
        }else{
            listProductsStockStatus = getStockArticleItemsUpdate( stockService.getProductsStockStatus( glassEntity.getId() ) ) ;
        }

        Collections.sort(listProductsStockStatus);

        comboGlassStockArticle.getItems().clear();
        comboGlassStockArticle.setItems( FXCollections.observableArrayList( listProductsStockStatus ) );
        comboGlassStockArticle.getSelectionModel().selectFirst();
    }

    private List<StockArticleItems> getStockArticleItemsUpdate(List<StockArticleItems> listStockArticleItems) {

        HashMap< Integer , Float > items = new HashMap<>() ;

        for ( OrderItemsEntity oi : orderDetailsReal.getArticleOrders() ) {
            if( oi.getStockItemId() != -1 ){
                items.put( oi.getStockItemId() , ( items.containsKey( oi.getStockItemId() ) ? items.get( oi.getStockItemId() ) : 0 ) + oi.getQuantity() );
            }
        }

        List<StockArticleItems> newListStockArticleItems = listStockArticleItems.stream()
                .map( e -> {
                    for ( OrderItemsEntity orderItems:  orderDetails.getArticleOrders() ) {
                        if( orderItems.getStockItemId() == e.getStockItems().getId() )
                            e.setSold( e.getSold() + orderItems.getQuantity() );
                    }
                     e.setSold( e.getSold() - ( items.containsKey( e.getStockItems().getId() ) ? items.get( e.getStockItems().getId() ) : 0 ) );
                    return e ;
                }).collect(Collectors.toList());

        return  newListStockArticleItems ;
    }


    public void goBack() throws IOException {


        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ListOrdersView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" List Commands -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }

    public void goHome() throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/MainView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Home -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }


    public void goToClientView() throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ClientView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" Clients -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }

    public void addAluminumToOrder() {


        OrderItemsEntity aluminumProduct = ( operation == CurrentCrudOperation.ADD ) ? new OrderItemsEntity() : editableOrderArticle;


        AluminumEntity aluminumEntity = ( this.aluminumProduct.getSelectionModel().getSelectedIndex() != -1 ) ?
                    this.aluminumProduct.getItems().get( this.aluminumProduct.getSelectionModel().getSelectedIndex()) :
                    (this.aluminumProduct.getSelectionModel().getSelectedItem() != null ) ?
                            this.aluminumProduct.getSelectionModel().getSelectedItem() : null ;

        if( aluminumEntity == null ){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("veuillez choisir le produit");
            alert.showAndWait();
            return;
        }


        aluminumProduct.setArticle( aluminumEntity );
        aluminumProduct.setName( aluminumEntity + " " + aluminumLabel.getText() );
        float price = this.getPrice( priceAluminumCombo) ;
        aluminumProduct.setPrice( price );
        float quantity = Float.valueOf( aluminumQuantity.getText() );
        aluminumProduct.setQuantity( quantity );

        aluminumProduct.setStockItemId( comboAlumStockArticle.getSelectionModel().getSelectedIndex() != -1 ?
                comboAlumStockArticle.getSelectionModel().getSelectedItem().getStockItems().getId() : -1 );

        if( operation == CurrentCrudOperation.ADD )
            orderDetails.getArticleOrders().add(aluminumProduct);
        else
            editableOrderArticle = null;


        this.loadDataTable();

        this.aluminumProduct.getSelectionModel().select(-1);
        this.aluminumProduct.getSelectionModel().select(null);
        aluminumLabel.setText("");
        priceAluminumCombo.getItems().clear();
        priceAluminumCombo.getSelectionModel().select(null);
        comboAlumStockArticle.getItems().clear();
        aluminumQuantity.setText("1");
        priceAluminumShow.setText(" 00 DH");
        operation = CurrentCrudOperation.ADD;
    }

    private float getPrice(ComboBox comboPrice){

        if( comboPrice.getSelectionModel().getSelectedItem() != null ){
            if( (comboPrice.getSelectionModel().getSelectedItem()) instanceof String ){
                return getPriceFromString( comboPrice );
            }else if( (comboPrice.getSelectionModel().getSelectedItem()) instanceof PriceEntity){
                return ( (PriceEntity) comboPrice.getSelectionModel().getSelectedItem()).getPrice();
            }
        }

        return 0f ;
    }

    private float getPriceFromString( ComboBox comboPrice ){

        String priceName = (String) comboPrice.getSelectionModel().getSelectedItem() ;
        final Pattern pattern = Pattern.compile("([a-zA-Z0-9]+)\\s+-\\s+([0-9]+\\.[0-9]+)DH");
        final Matcher matcher = pattern.matcher(priceName);


        if( matcher.matches() ){

            List<PriceEntity> pricesOfItems = comboPrice.getItems();

            PriceEntity priceEntity = pricesOfItems.stream()
                    .filter(e->e.getName().contains(matcher.group(1))).findFirst().orElse(null);

            if( priceEntity == null )
                return 0f;

            return priceEntity.getPrice();

        }else{
            try {
                return Float.valueOf( priceName );
            }catch (Exception e ){
                return  0f;
            }
        }

    }

    public void addAccessoryToOrder() {

        OrderItemsEntity accessoryArticle = ( operation == CurrentCrudOperation.ADD ) ? new OrderItemsEntity() : editableOrderArticle;

        AccessoryEntity accessoryEntity = ( this.accessoryProduct.getSelectionModel().getSelectedIndex() != -1 ) ?
                this.accessoryProduct.getItems().get( this.accessoryProduct.getSelectionModel().getSelectedIndex()) :
                (this.accessoryProduct.getSelectionModel().getSelectedItem() != null ) ?
                        this.accessoryProduct.getSelectionModel().getSelectedItem() : null ;

        if( accessoryEntity == null ){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("veuillez choisir le produit");
            alert.showAndWait();
            return;
        }


        accessoryArticle.setArticle( accessoryEntity );
        accessoryArticle.setName( accessoryEntity.getName() + " " + accessoryLabel.getText());
        accessoryArticle.setPrice( getPrice(accessoryPrice) );

        float quantity =Float.valueOf(accessoryQuantity.getText()) ;
        accessoryArticle.setQuantity(quantity);

        accessoryArticle.setStockItemId(  comboAccesStockArticle.getSelectionModel().getSelectedIndex() != -1 ?
                comboAccesStockArticle.getSelectionModel().getSelectedItem().getStockItems().getId() : -1  );


        if( operation == CurrentCrudOperation.ADD ) orderDetails.getArticleOrders().add(accessoryArticle);
        else editableOrderArticle = null;

        this.loadDataTable();

        accessoryProduct.getSelectionModel().select(-1);
        accessoryProduct.getSelectionModel().select(null);
        accessoryLabel.setText("");
        accessoryPrice.getItems().clear();
        accessoryPrice.getSelectionModel().select(null);
        comboAccesStockArticle.getItems().clear();
        accessoryQuantity.setText("1");
        accessoryTotal.setText(" 00 DH");
        operation = CurrentCrudOperation.ADD;

    }

    public void addGlassInOrder() {

        OrderItemsEntity glassArticle = ( operation == CurrentCrudOperation.ADD ) ? new OrderItemsEntity() : editableOrderArticle;

        GlassEntity glassEntity = ( this.glassProduct.getSelectionModel().getSelectedIndex() != -1 ) ?
                this.glassProduct.getItems().get( this.glassProduct.getSelectionModel().getSelectedIndex()) :
                (this.glassProduct.getSelectionModel().getSelectedItem() != null ) ?
                        this.glassProduct.getSelectionModel().getSelectedItem() : null ;

//        if( glassProduct.getSelectionModel().getSelectedIndex() == -1 ){
        if( glassEntity == null ){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("veuillez choisir le produit");
            alert.showAndWait();
            return;
        }

//        glassArticle.setArticle( glassProduct.getItems().get( glassProduct.getSelectionModel().getSelectedIndex()));
//        glassArticle.setName( glassProduct.getItems().get( glassProduct.getSelectionModel().getSelectedIndex()).getName() + " " + glassLabel.getText());
        glassArticle.setArticle( glassEntity );
        glassArticle.setName( glassEntity + " " + glassLabel.getText());
        glassArticle.setPrice( getPrice(glassPrice) );
        glassArticle.setNumberItems( numberPieceGlass.getValue() );

        float quantity =Float.valueOf(glassQuantity.getText());
        glassArticle.setQuantity( quantity );

        glassArticle.setStockItemId( comboGlassStockArticle.getSelectionModel().getSelectedIndex() != -1 ?
                comboGlassStockArticle.getSelectionModel().getSelectedItem().getStockItems().getId() : -1  );


        if( operation == CurrentCrudOperation.ADD ) orderDetails.getArticleOrders().add(glassArticle);
        else editableOrderArticle = null;

        this.loadDataTable();

        glassProduct.getSelectionModel().select(-1);
        glassProduct.getSelectionModel().select(null);
        glassLabel.setText("");
        glassPrice.getItems().clear();
        comboGlassStockArticle.getItems().clear();
        glassQuantity.setText("1");
        numberPieceGlass.getValueFactory().setValue(1);
        glassTotal.setText(" 00 DH");
        this.operation = CurrentCrudOperation.ADD;
    }


    void loadDataTable(){

        loadPaymentLabelsValues();


        observableArticleOrder.clear();
        observableArticleOrder.addAll( orderDetails.getArticleOrders() );

        labelOfOrder.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceProductOfOrder.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityProductOfOrder.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceOrder.setCellValueFactory(cellData ->  new ReadOnlyObjectWrapper(
                String.format("%.2f DH ", cellData.getValue().getQuantity() * cellData.getValue().getPrice() )
        ) );



        Callback<TableColumn<OrderItemsEntity, Void>, TableCell<OrderItemsEntity, Void>> editCellFactory = new Callback<TableColumn<OrderItemsEntity, Void>, TableCell<OrderItemsEntity, Void>>() {
            @Override
            public TableCell<OrderItemsEntity, Void> call(final TableColumn<OrderItemsEntity, Void> param) {
                return new TableCell<OrderItemsEntity, Void>() {

                    private final Button btn = new Button("Modifier");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            OrderItemsEntity data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data.getId());


                            switch ( data.getArticle().getType() ){
                                case ALUMINIUM:
                                    System.out.println( "AluminumEntity" );
                                    loadAluminumEdit(data);

                                    break;

                                case ACCESSOIRE:
                                    System.out.println( "AccessoryEntity" );
                                    loadAccessoryEdit(data);

                                    break;

                                case VERRE:
                                    System.out.println( "GlassEntity" );
                                    loadGlassEdit(data);
                                    break;

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
            }
        };


        Callback<TableColumn<OrderItemsEntity, Void>, TableCell<OrderItemsEntity, Void>> deleteCellFactory = new Callback<TableColumn<OrderItemsEntity, Void>, TableCell<OrderItemsEntity, Void>>() {
            @Override
            public TableCell<OrderItemsEntity, Void> call(final TableColumn<OrderItemsEntity, Void> param) {
                return  new TableCell<OrderItemsEntity, Void>() {

                    private final Button btn = new Button("Delete");

                    {

                        btn.setOnAction((ActionEvent event) -> {
                            OrderItemsEntity data = getTableView().getItems().get(getIndex());

                            switch ( data.getArticle().getType() ){
                                case ALUMINIUM :
                                    loadChangeDataAlumStatus(comboAlumStockArticle);
                                break ;
                                case ACCESSOIRE:
                                    loadChangeDataAlumStatus(comboAccesStockArticle);
                                    break;

                            }


                            System.out.println("selectedData delete: " + data.getId());
                            orderDetails.getArticleOrders().remove( data );
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
            }
        };

        editProductOfOrder.setCellFactory( editCellFactory );
        deleteProductOfOrder.setCellFactory(deleteCellFactory );

        tableProductsOfOrder.setItems( FXCollections.observableArrayList(orderDetails.getArticleOrders()) );
        tableProductsOfOrder.setItems( this.observableArticleOrder);

    }

    private void loadPaymentLabelsValues() {
        totalPriceOrder.setText( "00,00 DH" );
        totalPriceOrder.setText( String.format("%.2f DH", getTotal() ) );

        amountPaidOrder.setText("00,00 DH");
        amountPaidOrder.setText( String.format("%.2f DH", calculateAmountPaid() ) );

        amountRemainedOrder.setText("00,00 DH");
        amountRemainedOrder.setText( String.format("%.2f DH ", getTotal() - calculateAmountPaid() ) );
    }

    private void loadGlassEdit(OrderItemsEntity data) {

        glassProduct.getSelectionModel().select((GlassEntity) data.getArticle());

        loadChangeDataGlassStatus(glassProduct);

        for (StockArticleItems stockArticleItems: comboGlassStockArticle.getItems() ) {
            if( stockArticleItems.getStockItems().getId() == data.getStockItemId() )
                comboGlassStockArticle.getSelectionModel().select(stockArticleItems);
        }

        glassLabel.setText( data.getName().replace( data.getArticle().getName() , "" ).trim() );
        glassPrice.getSelectionModel().select( data.getPrice()+"" );
        glassQuantity.setText( data.getQuantity() + "" );
        numberPieceGlass.getValueFactory().setValue( data.getNumberItems() );


        glassTotal.setText( String.format( "%.2f DH" , Float.valueOf( data.getQuantity() ) * (
                ( glassPrice.getSelectionModel().getSelectedItem() == null ) ?
                        0 : getPrice(glassPrice)
        )));


        tabPaneAddProducts.getSelectionModel().select(2);
        editableOrderArticle = data;
        operation = CurrentCrudOperation.EDIT;

    }

    private void loadAccessoryEdit(OrderItemsEntity data) {

        accessoryProduct.getSelectionModel().select((AccessoryEntity) data.getArticle());

        loadChangeDataAccesStatus( accessoryProduct );

        for (StockArticleItems stockArticleItems: comboAccesStockArticle.getItems() ) {
            if( stockArticleItems.getStockItems().getId() == data.getStockItemId() )
                comboAccesStockArticle.getSelectionModel().select(stockArticleItems);
        }

        accessoryLabel.setText( data.getName().replace( data.getArticle().getName() , "" ).trim() );
        accessoryPrice.getSelectionModel().select( data.getPrice() + "" );
        accessoryQuantity.setText( data.getQuantity() + "" );

        accessoryTotal.setText( Float.valueOf( data.getQuantity() ) * (
                ( accessoryPrice.getSelectionModel().getSelectedItem() == null ) ?
                        0 :  getPrice(accessoryPrice)
        ) + " DH");

        tabPaneAddProducts.getSelectionModel().select(1);
        editableOrderArticle = data;
        operation = CurrentCrudOperation.EDIT;
    }

    private void loadAluminumEdit(OrderItemsEntity data) {

        aluminumProduct.getSelectionModel().select( (AluminumEntity) data.getArticle() );

        loadChangeDataAlumStatus(aluminumProduct);

        for (StockArticleItems stockArticleItems: comboAlumStockArticle.getItems() ) {
            if( stockArticleItems.getStockItems().getId() == data.getStockItemId() )
                comboAlumStockArticle.getSelectionModel().select(stockArticleItems);
        }

        aluminumLabel.setText( data.getName().replace(  data.getArticle().getName() , "").trim() );
        priceAluminumCombo.getSelectionModel().select( String.valueOf( data.getPrice() ) );
        aluminumQuantity.setText( data.getQuantity() + "");

        priceAluminumShow.setText( Float.valueOf( data.getQuantity() ) * (
                ( priceAluminumCombo.getSelectionModel().getSelectedItem() == null ) ?
                        0 : getPrice( priceAluminumCombo)
        ) + " DH");


        tabPaneAddProducts.getSelectionModel().selectFirst();
        editableOrderArticle = data;
        operation = CurrentCrudOperation.EDIT;
    }

    private Float getTotal(){

        return orderDetails.getArticleOrders().stream()
                          .map( n -> n.getPrice()* n.getQuantity() )
                          .collect( Collectors.toList() )
                          .stream()
                          .reduce( 0f ,  ( subtotal , element ) -> subtotal + element );
    }

    private Float calculateAmountPaid(){

        payedMount = orderDetails.getPaymentsMades()
                .stream().map( n -> n.getAmountPaid() )
                .reduce( 0f , (sub , elem) -> sub+elem );
        return payedMount;
    }

    public void saveOrderEvent() throws IOException {

        if( clientNameForm.getSelectionModel().getSelectedIndex() == -1 ){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur D'enregistrement");
            alert.setHeaderText("veuillez choisir un client");
            alert.showAndWait();
            return;
        }

        orderDetails.setClient( clientNameForm.getItems().get( clientNameForm.getSelectionModel().getSelectedIndex() ) );
        orderDetails.setPaymentStatus( comboPaymentStatus.getSelectionModel().getSelectedItem() );

        boolean saved =( operationOrder == CurrentCrudOperation.ADD ) ?
                orderService.addOrder(orderDetails) :
                orderService.updateOrder(orderDetails);

        if (saved) {
            this.savePricesClient();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("l'enregistrement en Command réussi");
            alert.setHeaderText("le Command est bien enregistrer");
            alert.showAndWait();

        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error D'enregistrement");
            alert.setHeaderText("Oups, il y a eu une erreur!");
            alert.showAndWait();
        }

        this.goBack();

    }

    private void savePricesClient(){

        List<PriceEntity> newPrices = new ArrayList<>();
        for( OrderItemsEntity articleOrder : orderDetails.getArticleOrders()){
            float priceChosen = articleOrder.getPrice();
            boolean alreadyUsed = false ;
            List<PriceEntity> priceItems = articleOrder
                                            .getArticle()
                                            .getPrices()
                                            .stream()
                                            .filter( e -> e.getClient() == null || e.getClient().getId() == orderDetails.getClient().getId() )
                                            .collect(Collectors.toList());
            for (PriceEntity price : priceItems ) {
                if( priceChosen == price.getPrice()){
                    alreadyUsed = true ;
                    break;
                }
            }

            if( !alreadyUsed ){
                PriceEntity newPrice = new PriceEntity();
                newPrice.setName( "px" + priceItems.size() );
                newPrice.setPrice( priceChosen );
                newPrice.setArticle( articleOrder.getArticle() );
                newPrice.setClient( orderDetails.getClient() );
                newPrices.add( newPrice );
            }

        }

        priceService.addPrices( newPrices );

    }

    public void addPaymentMade() {

        PayementMethod payementMethod = ((RadioButton) paymentMethodGroup.getSelectedToggle()).getText().equals("Espéce") ?
                        PayementMethod.ESPECE : PayementMethod.CHEQUE ;

        PaymentsMadeEntity paymentsMadeEntity = (operationPayment == CurrentCrudOperation.ADD ) ? new PaymentsMadeEntity() : editablePaymentMade;

        paymentsMadeEntity.setAmountPaid( Float.valueOf( amountToPayText.getText()) );
        paymentsMadeEntity.setPayementMethod( payementMethod );

        if(operationPayment == CurrentCrudOperation.ADD ) orderDetails.getPaymentsMades().add( paymentsMadeEntity );
        else editablePaymentMade = null;

        this.loadDataTable();

        loadPaymentTable();
        loadPaymentLabelsValues();
        amountToPayText.setText("0");
        paymentMethodGroup.selectToggle(cashToggleButton);
        operationPayment = CurrentCrudOperation.ADD;

    }
}
