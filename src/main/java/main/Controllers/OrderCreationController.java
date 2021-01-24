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
    private CurrentCrudOperation operationCommand = CurrentCrudOperation.ADD ;


    @FXML ComboBox<ClientEntity> clientNameForm ;
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


    public OrderEntity commandDetails ;
    private PaymentStatus OldPayementStatusBkp ;
    private float OldTotal ;
    private float payedMount ;


    private boolean isWorkingPayementCombo = false;
    private boolean isWorkingPriceTextFiltred = false ;

    private OrderItemsEntity editableCommandArticle = null;

    public void setData( OrderEntity entity ){
        operationCommand = CurrentCrudOperation.EDIT;

        OldTotal = entity.getArticleOrders().stream()
                .map( n -> n.getPrice()* n.getQuantity() )
                .collect( Collectors.toList() )
                .stream()
                .reduce( 0f ,  ( subtotal , element ) -> subtotal + element );

        payedMount = entity.getPaymentsMades().stream().map( n -> n.getAmountPaid() ).reduce( 0f , (sub , elem) -> sub+elem );

        orderReference.setText(  String.format("REF%08d", entity.getId() ) );

        OldPayementStatusBkp = entity.getPaymentStatus();

        commandDetails = entity;

        loadDataEdit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        commandDetails = new OrderEntity();
        loadDataAdd();

    }

    void loadDataAdd(){

        List<ClientEntity> clients = clientServices.getAll();
        clientNameForm.setItems(FXCollections.observableArrayList( clients ));
        new AutoCompleteBox(clientNameForm);

        comboPaymentStatus.setItems( FXCollections.observableArrayList( PaymentStatus.values() ) );
        comboPaymentStatus.getSelectionModel().selectFirst();
        comboPaymentStatus.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {

            amountToPayText.setEditable( (int) newValue == 1 );

            if( operationCommand == CurrentCrudOperation.EDIT && !isWorkingPriceTextFiltred){

                isWorkingPayementCombo = true ;

                if( OldPayementStatusBkp == PaymentStatus.CREDIT ){
                    amountToPayText.setText("0");
                    if ( comboPaymentStatus.getSelectionModel().getSelectedItem() == PaymentStatus.COMPLETED ){
                        amountToPayText.setText( (int) newValue == 2 || (int) newValue == 1 ? getTotal() - payedMount + "" : "0.0" );
                    }

                }else if( OldPayementStatusBkp == PaymentStatus.COMPLETED ){
                    amountToPayText.setText("0");
                    if(  comboPaymentStatus.getSelectionModel().getSelectedItem() == PaymentStatus.COMPLETED ){
                        amountToPayText.setText( (int) newValue == 2 || (int) newValue == 1 ? getTotal() - payedMount + "" : "0.0" );
                    }else{
                        amountToPayText.setText( (int) newValue == 2 || (int) newValue == 1 ? getTotal() - OldTotal + "" : "0.0" );
                    }

                }else if ( OldPayementStatusBkp == PaymentStatus.PAYINPARTS) {

                    amountToPayText.setText("0");
                    if ( comboPaymentStatus.getSelectionModel().getSelectedItem() == PaymentStatus.COMPLETED ){
                        amountToPayText.setText( (int) newValue == 2 || (int) newValue == 1 ? getTotal() - payedMount + "" : "0.0" );
                    }

                }


            }else if ( operationCommand == CurrentCrudOperation.ADD && !isWorkingPriceTextFiltred ) {
                isWorkingPayementCombo = true ;
                amountToPayText.setText( (int) newValue == 2 ? getTotal() + "" : "0.0" );

            }
            isWorkingPayementCombo = false ;

        } );

        amountToPayText.textProperty().addListener( (observable, oldValue, newValue) -> {

            if( operationCommand == CurrentCrudOperation.EDIT && !isWorkingPayementCombo ){

                isWorkingPriceTextFiltred = true ;

                if( OldPayementStatusBkp == PaymentStatus.CREDIT ){

                    if( Float.valueOf( newValue ) >= (getTotal() - payedMount) ){
                        comboPaymentStatus.getSelectionModel().select( PaymentStatus.COMPLETED );
                        amountToPayText.setText( getTotal() - payedMount + "" );
                    }

                }else if( OldPayementStatusBkp == PaymentStatus.PAYINPARTS ) {

                    try {

                       if( Float.valueOf( newValue ) >= (getTotal() - payedMount) ){
                            comboPaymentStatus.getSelectionModel().select( PaymentStatus.COMPLETED );
                            amountToPayText.setText( getTotal() - payedMount + "" );
                        }

                    }catch (Exception e ){
                        //System.out.println("aaa" );
                    }



                }else if ( OldPayementStatusBkp == PaymentStatus.COMPLETED ){
                    // System.out.println("dddddd");
                }


            }
            isWorkingPriceTextFiltred = false ;

        } );


        initialiseAluminumTab();
        initialiseAccessoireTab();
        initialiseGlassTab();

    }

    void loadDataEdit(){


        clientNameForm.getSelectionModel().select( commandDetails.getClient() );
        clientNameForm.setDisable(true);

        comboPaymentStatus.setItems( FXCollections.observableArrayList( PaymentStatus.values() ) );
        comboPaymentStatus.getSelectionModel().select( commandDetails.getPaymentStatus() );

        this.loadDataTable();

    }

    private void initialiseGlassTab() {
        glassProduct.setItems( FXCollections.observableArrayList( glassService.getAllGlassProducts() ) );

        glassProduct.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {

            glassPrice.getItems().clear();
            //check if Is Select item or null
            if( (int) newValue != -1 ){
                glassPrice.setItems( FXCollections.observableArrayList( glassProduct.getSelectionModel().getSelectedItem().getPrices()  ) );
                glassPrice.getSelectionModel().selectFirst();
            }

        });

       // glassPrice.getSelectionModel().selectedIndexProperty().addListener(  (options, oldValue, newValue) -> {
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

        // accessoirePrice.getSelectionModel().selectedIndexProperty().addListener(  (options, oldValue, newValue) -> {
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

        if( operation == CurrentCrudOperation.ADD ){
            OrderItemsEntity alumenuimProduct = new OrderItemsEntity();

            alumenuimProduct.setArticle( aluminuimProduct.getSelectionModel().getSelectedItem() );
            alumenuimProduct.setName( aluminuimProduct.getSelectionModel().getSelectedItem().getName() +" " + aluminuimLabel.getText() );
            float price = this.getPrice( priceAluminumCombo) ;
            alumenuimProduct.setPrice( price );
            alumenuimProduct.setQuantity( Float.valueOf( aluminuimQuentity.getText() ) );
            commandDetails.getArticleOrders().add( alumenuimProduct );

        }else{

            OrderItemsEntity alumenuimProduct = editableCommandArticle;
            alumenuimProduct.setArticle( aluminuimProduct.getSelectionModel().getSelectedItem() );
            alumenuimProduct.setName( aluminuimProduct.getSelectionModel().getSelectedItem().getName() +" " + aluminuimLabel.getText()  );
            float price = this.getPrice( priceAluminumCombo) ;
            alumenuimProduct.setPrice( price );
            alumenuimProduct.setQuantity( Float.valueOf( aluminuimQuentity.getText() ) );

            editableCommandArticle = null;

        }

        this.loadDataTable();

        if( this.operationCommand == CurrentCrudOperation.EDIT )
            restorePaimentStatus();

        aluminuimProduct.getSelectionModel().select(-1);
        aluminuimLabel.setText("");
        priceAluminumCombo.getItems().clear();
        aluminuimQuentity.setText("1");
        priceAlumnuimShow.setText(" 00 DH");
        operation = CurrentCrudOperation.ADD;
    }

    private float getPrice(ComboBox comboPrice){
//        Object aa = comboPrice.getSelectionModel().getSelectedItem();
//        System.out.println(aa );
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
        if( operation == CurrentCrudOperation.ADD ) {
            OrderItemsEntity accessoireArticle = new OrderItemsEntity();

            accessoireArticle.setArticle(accessoireProduct.getSelectionModel().getSelectedItem());
            accessoireArticle.setName(accessoireProduct.getSelectionModel().getSelectedItem().getName() + " " + accessoireLabel.getText());
            accessoireArticle.setPrice( getPrice(accessoirePrice) ); //  accessoirePrice.getSelectionModel().getSelectedItem().getPrice());
            accessoireArticle.setQuantity(Float.valueOf(accessoireQuentity.getText()));
//            accessoireArticle.setPriceOfArticle(accessoirePrice.getSelectionModel().getSelectedItem());
            commandDetails.getArticleOrders().add(accessoireArticle);

        }else{
            OrderItemsEntity accessoireArticle = editableCommandArticle;
            accessoireArticle.setArticle(accessoireProduct.getSelectionModel().getSelectedItem());
            accessoireArticle.setName(accessoireProduct.getSelectionModel().getSelectedItem().getName() + " " + accessoireLabel.getText());
            accessoireArticle.setPrice( getPrice(accessoirePrice) );// accessoirePrice.getSelectionModel().getSelectedItem().getPrice());
            accessoireArticle.setQuantity(Float.valueOf(accessoireQuentity.getText()));
//            accessoireArticle.setPriceOfArticle(accessoirePrice.getSelectionModel().getSelectedItem());
            editableCommandArticle = null;
        }
        this.loadDataTable();

        if( this.operationCommand == CurrentCrudOperation.EDIT )
            restorePaimentStatus();

        accessoireProduct.getSelectionModel().select(-1); ;
        accessoireLabel.setText(""); ;
        accessoirePrice.getItems().clear(); ;
        accessoireQuentity.setText("1"); ;
        accessoireTotal.setText(" 00 DH"); ;
        operation = CurrentCrudOperation.ADD;

    }

    public void saveGlassInCommand(MouseEvent mouseEvent) {

        if( operation == CurrentCrudOperation.ADD ) {
            OrderItemsEntity glassArticle = new OrderItemsEntity();

            glassArticle.setArticle(glassProduct.getSelectionModel().getSelectedItem());
            glassArticle.setName(glassProduct.getSelectionModel().getSelectedItem().getName() + " " + glassLabel.getText());
            glassArticle.setPrice( getPrice(glassPrice) );//glassPrice.getSelectionModel().getSelectedItem().getPrice());
            glassArticle.setQuantity(Float.valueOf(glassQuentity.getText()));

            commandDetails.getArticleOrders().add(glassArticle);
        }else{
            OrderItemsEntity glassArticle = editableCommandArticle ;
            glassArticle.setArticle(glassProduct.getSelectionModel().getSelectedItem());
            glassArticle.setName(glassProduct.getSelectionModel().getSelectedItem().getName() + " " + glassLabel.getText());
            glassArticle.setPrice( getPrice(glassPrice) );// glassPrice.getSelectionModel().getSelectedItem().getPrice());
            glassArticle.setQuantity(Float.valueOf(glassQuentity.getText()));

            editableCommandArticle = null;
        }

        this.loadDataTable();

        if( this.operationCommand == CurrentCrudOperation.EDIT )
            restorePaimentStatus();

        glassProduct.getSelectionModel().select(-1);
        glassLabel.setText("");
        glassPrice.getItems().clear();
        glassQuentity.setText("1");
        glassTotal.setText(" 00 DH");
        this.operation = CurrentCrudOperation.ADD;
    }

    void restorePaimentStatus(){

        if( OldPayementStatusBkp == PaymentStatus.COMPLETED && comboPaymentStatus.getSelectionModel().getSelectedItem() == PaymentStatus.COMPLETED){
            float newF = this.getTotal();
            comboPaymentStatus.getSelectionModel().select( PaymentStatus.PAYINPARTS );
            amountToPayText.setText( newF - OldTotal + " " );
        }

    }

    void loadDataTable(){

        totalPriceOrder.setText( "00,00 DH" );
        totalPriceOrder.setText( String.format("%.2f DH", getTotal() ) );

        amountPaidOrder.setText("00,00 DH");
        amountPaidOrder.setText( String.format("%.2f DH", calculAmountPaid() ) );

        amountRemainedOrder.setText("00,00 DH");
        amountRemainedOrder.setText( String.format("%.2f DH ", getTotal() - calculAmountPaid() ) );

        observableArticleCommand.clear();
        observableArticleCommand.addAll( commandDetails.getArticleOrders() );

        lableOfCommand.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceProductOfCommand.setCellValueFactory(new PropertyValueFactory<>("price"));
        quentityProductOfCommand.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCommand.setCellValueFactory( cellData ->  new ReadOnlyObjectWrapper( cellData.getValue().getQuantity() * cellData.getValue().getPrice() + " DH" ) );


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
//                                    glassPrice.getSelectionModel().select( data.getPriceOfArticle() );
                                    glassPrice.getSelectionModel().select( data.getPrice()+"" );
                                    glassQuentity.setText( data.getQuantity() + "" );


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
                            commandDetails.getArticleOrders().remove( data );
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

        editProductOfCommand.setCellFactory( editCellFactory );
        deleteProductOfCommand.setCellFactory(deleteCellFactory );

        tableProductsOfCommand.setItems( FXCollections.observableArrayList(commandDetails.getArticleOrders()) );
        tableProductsOfCommand.setItems( this.observableArticleCommand );

    }

    private Float getTotal(){

        float total = commandDetails.getArticleOrders().stream()
                          .map( n -> n.getPrice()* n.getQuantity() )
                          .collect( Collectors.toList() )
                          .stream()
                          .reduce( 0f ,  ( subtotal , element ) -> subtotal + element );
        return total;
    }

    private Float calculAmountPaid(){

        payedMount = commandDetails.getPaymentsMades()
                .stream().map( n -> n.getAmountPaid() )
                .reduce( 0f , (sub , elem) -> sub+elem );
        return payedMount;
    }

    public void saveCommandEvent(MouseEvent mouseEvent) throws IOException {


        PayementMethod payementMethod = ((RadioButton) payementMethodGroup.getSelectedToggle()).getText() == "Espéce" ?
                                        PayementMethod.ESPECE : PayementMethod.CHEQUE ;

        ClientEntity clientEntity = ( operationCommand == CurrentCrudOperation.ADD ) ?
                    clientNameForm.getItems().get( clientNameForm.getSelectionModel().getSelectedIndex() ) :
                    clientNameForm.getSelectionModel().getSelectedItem() ;

        commandDetails.setClient( clientEntity );
        commandDetails.setPaymentStatus( comboPaymentStatus.getSelectionModel().getSelectedItem() );

        PaymentsMadeEntity paymentsMadeEntity = new PaymentsMadeEntity();

        paymentsMadeEntity.setAmountPaid( Float.valueOf( amountToPayText.getText()) );
        paymentsMadeEntity.setPayementMethod(payementMethod);

        commandDetails.getPaymentsMades().add( paymentsMadeEntity );

        boolean saved = false ;

        if( operationCommand == CurrentCrudOperation.ADD ) {
            saved = orderService.addCommand(commandDetails);
        }else{
            saved = orderService.updateOrder(commandDetails);
        }

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
}
