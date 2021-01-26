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
import main.Models.enums.PayementMethod;
import main.Models.enums.PaymentStatus;
import main.Models.utils.CurrentCrudOperation;
import main.Services.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class OrderCreationController implements Initializable {

    private final OrderService orderService = new OrderService();
    private final ClientServices clientServices = new ClientServices();
    private final AccessoryService accessoryService = new AccessoryService();
    private final AluminumService aluminumService = new AluminumService();
    private final GlassService glassService = new GlassService();

    private CurrentCrudOperation operation = CurrentCrudOperation.ADD;
    private CurrentCrudOperation operationOrder = CurrentCrudOperation.ADD ;


    // @FXML ComboBox<ClientEntity> clientNameForm ;
    @FXML ComboBox<Object> clientNameForm ;
    @FXML Label totalPriceOrder;
    @FXML Label amountPaidOrder;
    @FXML Label amountRemainedOrder;
    @FXML ComboBox<PaymentStatus> comboPaymentStatus;
    @FXML TextField amountToPayText ;
    @FXML ToggleGroup payementMethodGroup;
    @FXML Label orderReference;




    // ---------- Aluminum Tab -------------
    @FXML ComboBox<AluminumEntity> aluminuimProduct;
    @FXML TextField aluminuimLabel;
    @FXML ComboBox<Object> priceAluminumCombo ;
    @FXML TextField aluminuimQuentity;
    @FXML Label priceAlumnuimShow;

    // ------------ Accessoire Tab --------

    @FXML ComboBox<AccessoryEntity> accessoireProduct;
    @FXML TextField accessoireLabel ;
//    @FXML ComboBox<PriceEntity> accessoirePrice ;
    @FXML ComboBox<Object> accessoirePrice ;
    @FXML TextField accessoireQuentity ;
    @FXML Label  accessoireTotal ;



    // --------------- GlassTab  -----------------
    @FXML ComboBox<GlassEntity> glassProduct;
    @FXML TextField glassLabel ;
    //@FXML ComboBox<PriceEntity> glassPrice ;
    @FXML ComboBox<Object> glassPrice ;
    @FXML TextField glassQuentity ;
    @FXML Label glassTotal ;
    @FXML Spinner<Integer> nombrePieceGlass ;

    // --------- TABPANE Switch Produts-----------

    @FXML TabPane tabPaneAddProducts;

    // -----------Table Products------------

    @FXML TableView<OrderItemsEntity> tableProductsOfCommand;
    @FXML TableColumn<OrderItemsEntity, String> lableOfCommand ;
    @FXML TableColumn<OrderItemsEntity, String> priceProductOfCommand ;
    @FXML TableColumn<OrderItemsEntity, String> quentityProductOfCommand ;
    @FXML TableColumn<OrderItemsEntity, String> priceCommand ;
    @FXML TableColumn<OrderItemsEntity, Void> editProductOfCommand ;
    @FXML TableColumn<OrderItemsEntity, Void> deleteProductOfCommand ;
    ObservableList<OrderItemsEntity> observableArticleCommand = FXCollections.observableArrayList();


    public OrderEntity orderDetails;
    private PaymentStatus OldPayementStatusBkp ;
    private float OldTotal ;
    private float payedMount ;


    private boolean isWorkingPayementCombo = false;
    private boolean isWorkingPriceTextFiltred = false ;

    private OrderItemsEntity editableCommandArticle = null;

    public void setData( OrderEntity entity ){
        operationOrder = CurrentCrudOperation.EDIT;

        OldTotal = entity.getArticleOrders().stream()
                .map( n -> n.getPrice()* n.getQuantity() )
                .collect( Collectors.toList() )
                .stream()
                .reduce( 0f ,  ( subtotal , element ) -> subtotal + element );

        payedMount = entity.getPaymentsMades().stream().map( n -> n.getAmountPaid() ).reduce( 0f , (sub , elem) -> sub+elem );

        orderReference.setText(  String.format("REF%08d", entity.getId() ) );

        OldPayementStatusBkp = entity.getPaymentStatus();

        orderDetails = entity;

        loadDataEdit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        orderDetails = new OrderEntity();
        loadDataAdd();

    }

    void loadDataAdd(){

        List<ClientEntity> clients = clientServices.getAll();
        clientNameForm.setItems( FXCollections.observableArrayList( clients ) );
        //clientNameForm.setItems(FXCollections.observableArrayList( clients ));
        new AutoCompleteBox(clientNameForm);

        comboPaymentStatus.setItems( FXCollections.observableArrayList( PaymentStatus.values() ) );
        comboPaymentStatus.getSelectionModel().selectFirst();

        comboPaymentStatus.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {

            amountToPayText.setEditable( (int) newValue == 1 );

            switch ( (int) newValue ){
                case 0 :
                case 1 :
                    amountToPayText.setText( 0 + "" );
                    break;

                case 2 :

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

                    amountToPayText.setText( ( totalOrder  - totalPaid )  + "" );
                    break;
            }


        });


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

            if( comboPaymentStatus.getSelectionModel().getSelectedItem() == PaymentStatus.PARTRANCHES ){

                if( Float.valueOf( newValue ) >= ( totalOrder  - totalPaid ) ){
                        comboPaymentStatus.getSelectionModel().select( PaymentStatus.PAYÉ );
                        amountToPayText.setText( totalOrder - totalPaid  + "" );
                }

            }

        } );

//        comboPaymentStatus.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {
//
//            amountToPayText.setEditable( (int) newValue == 1 );
//
//            if( operationOrder == CurrentCrudOperation.EDIT && !isWorkingPriceTextFiltred){
//
//                isWorkingPayementCombo = true ;
//
//                if( OldPayementStatusBkp == PaymentStatus.CRÉDIT ){
//                    amountToPayText.setText("0");
//                    if ( comboPaymentStatus.getSelectionModel().getSelectedItem() == PaymentStatus.PAYÉ ){
//                        amountToPayText.setText( (int) newValue == 2 || (int) newValue == 1 ? getTotal() - payedMount + "" : "0.0" );
//                    }
//
//                }else if( OldPayementStatusBkp == PaymentStatus.PAYÉ ){
//                    amountToPayText.setText("0");
//                    if(  comboPaymentStatus.getSelectionModel().getSelectedItem() == PaymentStatus.PAYÉ ){
//                        amountToPayText.setText( (int) newValue == 2 || (int) newValue == 1 ? getTotal() - payedMount + "" : "0.0" );
//                    }else{
//                        amountToPayText.setText( (int) newValue == 2 || (int) newValue == 1 ? getTotal() - OldTotal + "" : "0.0" );
//                    }
//
//                }else if ( OldPayementStatusBkp == PaymentStatus.PARTRANCHES) {
//
//                    amountToPayText.setText("0");
//                    if ( comboPaymentStatus.getSelectionModel().getSelectedItem() == PaymentStatus.PAYÉ ){
//                        amountToPayText.setText( (int) newValue == 2 || (int) newValue == 1 ? getTotal() - payedMount + "" : "0.0" );
//                    }
//
//                }
//
//
//            }else if ( operationOrder == CurrentCrudOperation.ADD && !isWorkingPriceTextFiltred ) {
//                isWorkingPayementCombo = true ;
//                amountToPayText.setText( (int) newValue == 2 ? getTotal() + "" : "0.0" );
//
//            }
//            isWorkingPayementCombo = false ;
//
//        } );
//
//        amountToPayText.textProperty().addListener( (observable, oldValue, newValue) -> {
//
//            if( operationOrder == CurrentCrudOperation.EDIT && !isWorkingPayementCombo ){
//
//                isWorkingPriceTextFiltred = true ;
//
//                if( OldPayementStatusBkp == PaymentStatus.CRÉDIT ){
//
//                    if( Float.valueOf( newValue ) >= (getTotal() - payedMount) ){
//                        comboPaymentStatus.getSelectionModel().select( PaymentStatus.PAYÉ );
//                        amountToPayText.setText( getTotal() - payedMount + "" );
//                    }
//
//                }else if( OldPayementStatusBkp == PaymentStatus.PARTRANCHES ) {
//
//                    try {
//
//                       if( Float.valueOf( newValue ) >= (getTotal() - payedMount) ){
//                            comboPaymentStatus.getSelectionModel().select( PaymentStatus.PAYÉ );
//                            amountToPayText.setText( getTotal() - payedMount + "" );
//                        }
//
//                    }catch (Exception e ){
//                        //System.out.println("aaa" );
//                    }
//
//
//
//                }
////                else if ( OldPayementStatusBkp == PaymentStatus.PAYÉ ){
////                    // System.out.println("dddddd");
////                }
//
//
//            }
//            isWorkingPriceTextFiltred = false ;
//
//        } );


        initialiseAluminumTab();
        initialiseAccessoireTab();
        initialiseGlassTab();

    }

    void loadDataEdit(){


        clientNameForm.getSelectionModel().select( orderDetails.getClient() );
        clientNameForm.setDisable(true);

        comboPaymentStatus.setItems( FXCollections.observableArrayList( PaymentStatus.values() ) );
        comboPaymentStatus.getSelectionModel().select( orderDetails.getPaymentStatus() );

        this.loadDataTable();

    }

    private void initialiseGlassTab() {

        nombrePieceGlass.setValueFactory( new SpinnerValueFactory.IntegerSpinnerValueFactory(1 , 1000 , 1) );

        glassProduct.setItems( FXCollections.observableArrayList( glassService.getAllGlassProducts() ) );

        glassProduct.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {

            glassPrice.getItems().clear();
            //check if Is Select item or null
            if( (int) newValue != -1 ){
                glassPrice.setItems( FXCollections.observableArrayList( glassProduct.getSelectionModel().getSelectedItem().getPrices()  ) );
                glassPrice.getSelectionModel().selectFirst();
            }

        });

        glassPrice.valueProperty().addListener(  (options, oldValue, newValue) -> {

            float number = 0f;
            try{
                number = Float.valueOf( glassQuentity.getText().equals("") ? "0" : glassQuentity.getText() );
            }catch (Exception e) {
                number = 1f;
            }

            glassTotal.setText( number * (
                    ( glassPrice.getSelectionModel().getSelectedItem() == null ) ?
                            0 : getPrice(glassPrice) // glassPrice.getSelectionModel().getSelectedItem().getPrice()
            ) + " DH");

        });

        glassQuentity.textProperty().addListener( (observable, oldValue, newValue) -> {

            float number = 0f;
            try{
                number = Float.valueOf( newValue.equals("") ? "0" : newValue );
            }catch (Exception e) {
                number = 1f;
            }

            glassTotal.setText( number * (
                    ( glassPrice.getSelectionModel().getSelectedItem() == null ) ?
                            0 : getPrice(glassPrice) //glassPrice.getSelectionModel().getSelectedItem().getPrice()
            ) + " DH");

        } );
    }

    private void initialiseAccessoireTab() {
        accessoireProduct.setItems( FXCollections.observableArrayList(accessoryService.getAllAccessoryProducts()) );

        accessoireProduct.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {
            accessoirePrice.getItems().clear();
            //check if Is Select item or null
            if( (int) newValue != -1 ){
                accessoirePrice.setItems( FXCollections.observableArrayList( accessoireProduct.getSelectionModel().getSelectedItem().getPrices()  ) );
                accessoirePrice.getSelectionModel().selectFirst();
            }

        });

        accessoirePrice.valueProperty().addListener(  (options, oldValue, newValue) -> {

            float number = 0f;
            try{
                number = Float.valueOf( accessoireQuentity.getText().equals("") ? "0" : accessoireQuentity.getText() );
            }catch (Exception e) {
                number = 1f;
            }

            accessoireTotal.setText( number * (
                    ( accessoirePrice.getSelectionModel().getSelectedItem() == null) ?
                            0 : getPrice(accessoirePrice) //accessoirePrice.getSelectionModel().getSelectedItem().getPrice()
            ) + " DH");

        });

        accessoireQuentity.textProperty().addListener( (observable, oldValue, newValue) -> {

            float number = 0f;
            try{
                number = Float.valueOf( newValue.equals("") ? "0" : newValue );
            }catch (Exception e) {
                number = 1f;
            }

            accessoireTotal.setText( number * (
                    ( accessoirePrice.getSelectionModel().getSelectedItem() == null ) ?
                            0 : getPrice(accessoirePrice) //accessoirePrice.getSelectionModel().getSelectedItem().getPrice()
            ) + " DH");

        } );
    }

    private void initialiseAluminumTab() {

        aluminuimProduct.setItems( FXCollections.observableArrayList( aluminumService.getAllAlumunuimProducts() ) );
        aluminuimProduct.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {
            priceAluminumCombo.getItems().clear();
            //check if Is Select item or null
            if( (int) newValue != -1 ){
                priceAluminumCombo.setItems( FXCollections.observableArrayList( aluminuimProduct.getSelectionModel().getSelectedItem().getPrices() ) );
                priceAluminumCombo.getSelectionModel().selectFirst();
            }

        });

        priceAluminumCombo.valueProperty().addListener( (options, oldValue, newValue) -> {

            float number = 0f;
            try{
                number = Float.valueOf( aluminuimQuentity.getText().equals("") ? "0" : aluminuimQuentity.getText() );
            }catch (Exception e) {
                number = 1f;
            }

            float total = number * (
                    ( priceAluminumCombo.getSelectionModel().getSelectedItem() == null ) ?
                            0 : getPrice(priceAluminumCombo)
            );

            priceAlumnuimShow.setText( total + " DH");

        });


        aluminuimQuentity.textProperty().addListener( (observable, oldValue, newValue) -> {
            float number = 0f;
            try{
                number = Float.valueOf( newValue.equals("") ? "0" : newValue );
            }catch (Exception e) {
                number = 1f;
            }
            float total = number * (
                    ( priceAluminumCombo.getSelectionModel().getSelectedItem() == null ) ?
                            0 : getPrice( priceAluminumCombo )
            );
            priceAlumnuimShow.setText( total + " DH");
        } );
    }


    public void goBack(MouseEvent mouseEvent) throws IOException {


        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ListOrdersView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" List Commands -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }

    public void goHome(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/MainView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Home -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }


    public void goToClientView(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ClientView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" Clients -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }

    public void addAlumnuimToOrder(MouseEvent mouseEvent) {


            OrderItemsEntity alumenuimProduct = ( operation == CurrentCrudOperation.ADD ) ? new OrderItemsEntity() : editableCommandArticle;

            alumenuimProduct.setArticle( aluminuimProduct.getSelectionModel().getSelectedItem() );
            alumenuimProduct.setName( aluminuimProduct.getSelectionModel().getSelectedItem().getName() +" " + aluminuimLabel.getText() );
            float price = this.getPrice( priceAluminumCombo) ;
            alumenuimProduct.setPrice( price );
            alumenuimProduct.setQuantity( Float.valueOf( aluminuimQuentity.getText() ) );

        if( operation == CurrentCrudOperation.ADD ) orderDetails.getArticleOrders().add( alumenuimProduct );
        else editableCommandArticle = null;

        this.loadDataTable();

        aluminuimProduct.getSelectionModel().select(-1);
        aluminuimLabel.setText("");
        priceAluminumCombo.getItems().clear();
        aluminuimQuentity.setText("1");
        priceAlumnuimShow.setText(" 00 DH");
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

    public void addAccessoireToOrder(MouseEvent mouseEvent) {

        OrderItemsEntity accessoireArticle = ( operation == CurrentCrudOperation.ADD ) ? new OrderItemsEntity() : editableCommandArticle ;

        accessoireArticle.setArticle(accessoireProduct.getSelectionModel().getSelectedItem());
        accessoireArticle.setName(accessoireProduct.getSelectionModel().getSelectedItem().getName() + " " + accessoireLabel.getText());
        accessoireArticle.setPrice( getPrice(accessoirePrice) );
        accessoireArticle.setQuantity(Float.valueOf(accessoireQuentity.getText()));

        if( operation == CurrentCrudOperation.ADD ) orderDetails.getArticleOrders().add(accessoireArticle);
        else editableCommandArticle = null;

        this.loadDataTable();

        accessoireProduct.getSelectionModel().select(-1); ;
        accessoireLabel.setText(""); ;
        accessoirePrice.getItems().clear(); ;
        accessoireQuentity.setText("1"); ;
        accessoireTotal.setText(" 00 DH"); ;
        operation = CurrentCrudOperation.ADD;

    }

    public void addGlassInOrder(MouseEvent mouseEvent) {

        OrderItemsEntity glassArticle = ( operation == CurrentCrudOperation.ADD ) ? new OrderItemsEntity() : editableCommandArticle;

        glassArticle.setArticle(glassProduct.getSelectionModel().getSelectedItem());
        glassArticle.setName(glassProduct.getSelectionModel().getSelectedItem().getName() + " " + glassLabel.getText());
        glassArticle.setPrice( getPrice(glassPrice) );
        glassArticle.setQuantity(Float.valueOf(glassQuentity.getText()));
        glassArticle.setNumberItems( nombrePieceGlass.getValue() );

        if( operation == CurrentCrudOperation.ADD ) orderDetails.getArticleOrders().add(glassArticle);
        else editableCommandArticle = null;

        this.loadDataTable();

        glassProduct.getSelectionModel().select(-1);
        glassLabel.setText("");
        glassPrice.getItems().clear();
        glassQuentity.setText("1");
        glassTotal.setText(" 00 DH");
        this.operation = CurrentCrudOperation.ADD;
    }


    void loadDataTable(){

        totalPriceOrder.setText( "00,00 DH" );
        totalPriceOrder.setText( String.format("%.2f DH", getTotal() ) );

        amountPaidOrder.setText("00,00 DH");
        amountPaidOrder.setText( String.format("%.2f DH", calculAmountPaid() ) );

        amountRemainedOrder.setText("00,00 DH");
        amountRemainedOrder.setText( String.format("%.2f DH ", getTotal() - calculAmountPaid() ) );


        if( comboPaymentStatus.getSelectionModel().getSelectedItem() == PaymentStatus.PAYÉ ){

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

            amountToPayText.setText( totalOrder - totalPaid  + "" );

        }

        observableArticleCommand.clear();
        observableArticleCommand.addAll( orderDetails.getArticleOrders() );

        lableOfCommand.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceProductOfCommand.setCellValueFactory(new PropertyValueFactory<>("price"));
        quentityProductOfCommand.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCommand.setCellValueFactory( cellData ->  new ReadOnlyObjectWrapper( cellData.getValue().getQuantity() * cellData.getValue().getPrice()
                //String.format("%.2f DH ", cellData.getValue().getQuantity() * cellData.getValue().getPrice() )
        ) );
//        priceCommand.setCellValueFactory( cellData ->  new ReadOnlyObjectWrapper(
//                Math.round(cellData.getValue().getQuantity() * cellData.getValue().getPrice() * 2) / 2.0f + " DH"
//        ) );




        Callback<TableColumn<OrderItemsEntity, Void>, TableCell<OrderItemsEntity, Void>> editCellFactory = new Callback<TableColumn<OrderItemsEntity, Void>, TableCell<OrderItemsEntity, Void>>() {
            @Override
            public TableCell<OrderItemsEntity, Void> call(final TableColumn<OrderItemsEntity, Void> param) {
                final TableCell<OrderItemsEntity, Void> cell = new TableCell<OrderItemsEntity, Void>() {

                    private final Button btn = new Button("Modifier");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            OrderItemsEntity data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data.getId());


                            switch ( data.getArticle().getType() ){
                                case "AluminumEntity"  :

                                    aluminuimProduct.getSelectionModel().select((AluminumEntity) data.getArticle());
                                    aluminuimLabel.setText( data.getName().replace(  data.getArticle().getName() , "").trim() );
                                    priceAluminumCombo.getSelectionModel().select( String.valueOf( data.getPrice() ) );
                                    aluminuimQuentity.setText( data.getQuantity() + "");


                                    priceAlumnuimShow.setText( Float.valueOf( data.getQuantity() ) * (
                                            ( priceAluminumCombo.getSelectionModel().getSelectedItem() == null ) ?
                                                    0 : getPrice( priceAluminumCombo)
                                    ) + " DH");


                                    tabPaneAddProducts.getSelectionModel().selectFirst();
                                    editableCommandArticle = data;
                                    operation = CurrentCrudOperation.EDIT;

                                    break;

                                case "AccessoryEntity" :
                                    System.out.println( "AccessoryEntity" );

                                    accessoireProduct.getSelectionModel().select((AccessoryEntity) data.getArticle());
                                    accessoireLabel.setText( data.getName().replace( data.getArticle().getName() , "" ).trim() );
                                    accessoirePrice.getSelectionModel().select( data.getPrice() + "" );
                                    accessoireQuentity.setText( data.getQuantity() + "" );

                                    accessoireTotal.setText( Float.valueOf( data.getQuantity() ) * (
                                            ( accessoirePrice.getSelectionModel().getSelectedItem() == null ) ?
                                                    0 :  getPrice( accessoirePrice) // accessoirePrice.getSelectionModel().getSelectedItem().getPrice()
                                    ) + " DH");

                                    tabPaneAddProducts.getSelectionModel().select(1);
                                    editableCommandArticle = data;
                                    operation = CurrentCrudOperation.EDIT;

                                    break;

                                case "GlassEntity"     :
                                    System.out.println( "GlassEntity" );

                                    glassProduct.getSelectionModel().select((GlassEntity) data.getArticle());
                                    glassLabel.setText( data.getName().replace( data.getArticle().getName() , "" ).trim() );
                                    glassPrice.getSelectionModel().select( data.getPrice()+"" );
                                    glassQuentity.setText( data.getQuantity() + "" );
                                    nombrePieceGlass.getValueFactory().setValue( data.getNumberItems() );


                                    glassTotal.setText( Float.valueOf( data.getQuantity() ) * (
                                            ( glassPrice.getSelectionModel().getSelectedIndex() == -1 ) ?
                                                    0 : getPrice(glassPrice) // glassPrice.getSelectionModel().getSelectedItem().getPrice()
                                    ) + " DH");


                                    tabPaneAddProducts.getSelectionModel().select(2);
                                    editableCommandArticle = data;
                                    operation = CurrentCrudOperation.EDIT;
                                    break;

                                default:
                                    System.out.println(" there no type of avalaibele types ");
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


        Callback<TableColumn<OrderItemsEntity, Void>, TableCell<OrderItemsEntity, Void>> deleteCellFactory = new Callback<TableColumn<OrderItemsEntity, Void>, TableCell<OrderItemsEntity, Void>>() {
            @Override
            public TableCell<OrderItemsEntity, Void> call(final TableColumn<OrderItemsEntity, Void> param) {
                final TableCell<OrderItemsEntity, Void> cell = new TableCell<OrderItemsEntity, Void>() {

                    private final Button btn = new Button("Delete");

                    {

                        btn.setOnAction((ActionEvent event) -> {
                            OrderItemsEntity data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData delete: " + data.getId());
                            orderDetails.getArticleOrders().remove( data );
                            loadDataTable();
                        });
                        //btn.setDisable();
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

        editProductOfCommand.setCellFactory( editCellFactory );
        deleteProductOfCommand.setCellFactory(deleteCellFactory );

        tableProductsOfCommand.setItems( FXCollections.observableArrayList(orderDetails.getArticleOrders()) );
        tableProductsOfCommand.setItems( this.observableArticleCommand );

    }

    private Float getTotal(){

        float total = orderDetails.getArticleOrders().stream()
                          .map( n -> n.getPrice()* n.getQuantity() )
                          .collect( Collectors.toList() )
                          .stream()
                          .reduce( 0f ,  ( subtotal , element ) -> subtotal + element );
        return total;
    }

    private Float calculAmountPaid(){

        payedMount = orderDetails.getPaymentsMades()
                .stream().map( n -> n.getAmountPaid() )
                .reduce( 0f , (sub , elem) -> sub+elem );
        return payedMount;
    }

    public void saveOrderEvent(MouseEvent mouseEvent) throws IOException {

        PayementMethod payementMethod = ((RadioButton) payementMethodGroup.getSelectedToggle()).getText().equals("Espéce") ?
                                        PayementMethod.ESPECE : PayementMethod.CHEQUE ;

        ClientEntity clientEntity = getSelectedClientEntity(clientNameForm);

        if( clientEntity == null  ){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur D'enregistrement");
            alert.setHeaderText("veuillez choisir un client");
            alert.showAndWait();
            return;
        }

        orderDetails.setClient( clientEntity );
        orderDetails.setPaymentStatus( comboPaymentStatus.getSelectionModel().getSelectedItem() );

        PaymentsMadeEntity paymentsMadeEntity = new PaymentsMadeEntity();

        paymentsMadeEntity.setAmountPaid( Float.valueOf( amountToPayText.getText()) );
        paymentsMadeEntity.setPayementMethod(payementMethod);

        orderDetails.getPaymentsMades().add( paymentsMadeEntity );

        boolean saved =( operationOrder == CurrentCrudOperation.ADD ) ?
                orderService.addOrder(orderDetails) :
                orderService.updateOrder(orderDetails);
//        boolean saved = false ;

//        if( operationOrder == CurrentCrudOperation.ADD ) {
//            saved = orderService.addOrder(orderDetails);
//        }else{
//            saved = orderService.updateOrder(orderDetails);
//        }

        if (saved) {

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

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ListOrdersView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" List Commands -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }

    private ClientEntity getSelectedClientEntity(ComboBox clientBox) {
        ClientEntity clientEntity = null;
        if( operationOrder == CurrentCrudOperation.ADD ){
            if( clientBox.getSelectionModel().getSelectedItem() == null ) return null ;

            if( clientBox.getSelectionModel().getSelectedItem() instanceof String ){
                List<ClientEntity> clients = clientBox.getItems() ;
                return clients.stream()
                        .filter( e -> e.getName().contains( clientNameForm.getSelectionModel().getSelectedItem().toString() ) )
                        .findFirst().orElse(null);
            }else {
                clientEntity = (ClientEntity) clientNameForm.getSelectionModel().getSelectedItem();
            }

        }else{
            clientEntity = (ClientEntity) clientBox.getSelectionModel().getSelectedItem() ;
        }
        return clientEntity;
    }
}
