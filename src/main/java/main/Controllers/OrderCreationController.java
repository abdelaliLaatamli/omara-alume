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
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
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
    private final PriceService priceService = new PriceService();

    private CurrentCrudOperation operation = CurrentCrudOperation.ADD;
    private CurrentCrudOperation operationOrder = CurrentCrudOperation.ADD ;
    private CurrentCrudOperation operationPayement = CurrentCrudOperation.ADD;

    @FXML ComboBox<ClientEntity> clientNameForm ;
    @FXML Label totalPriceOrder;
    @FXML Label amountPaidOrder;
    @FXML Label amountRemainedOrder;
    @FXML ComboBox<PaymentStatus> comboPaymentStatus;
    @FXML TextField amountToPayText ;

    @FXML ToggleGroup paymentMethodGroup;
    @FXML RadioButton especeToggleButton;
    @FXML RadioButton chequeToggleButton;

    @FXML Label orderReference;
    @FXML Button savePayement ;


    // ---------- Aluminum Tab -------------
    @FXML ComboBox<AluminumEntity> aluminuimProduct;
    @FXML TextField aluminuimLabel;
    @FXML ComboBox<Object> priceAluminumCombo ;
    @FXML TextField aluminuimQuentity;
    @FXML Label priceAlumnuimShow;

    // ------------ Accessoire Tab --------

    @FXML ComboBox<AccessoryEntity> accessoireProduct;
    @FXML TextField accessoireLabel ;
    @FXML ComboBox<Object> accessoirePrice ;
    @FXML TextField accessoireQuentity ;
    @FXML Label  accessoireTotal ;

    // --------------- GlassTab  -----------------
    @FXML ComboBox<GlassEntity> glassProduct;
    @FXML TextField glassLabel ;
    @FXML ComboBox<Object> glassPrice ;
    @FXML TextField glassQuentity ;
    @FXML Label glassTotal ;
    @FXML Spinner<Integer> nombrePieceGlass ;

    // --------- TABPANE Switch Produts-----------

    @FXML TabPane tabPaneAddProducts;

    // ------------Table Payement-------------

    @FXML TableView<PaymentsMadeEntity> tableOfPayements;
    @FXML TableColumn<PaymentsMadeEntity , String> dateOfPayement;
    @FXML TableColumn<PaymentsMadeEntity , String> modeOfPayement;
    @FXML TableColumn<PaymentsMadeEntity , String> payementAmout;
    @FXML TableColumn<PaymentsMadeEntity , Void> editPayement;
    @FXML TableColumn<PaymentsMadeEntity , Void> deletePayement;
    ObservableList<PaymentsMadeEntity> observablePaymentsMades = FXCollections.observableArrayList();

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
    private float payedMount ;
    private OrderItemsEntity editableOrderArticle = null;
    private PaymentsMadeEntity editablePayementMade = null ;


    public void setData( OrderEntity entity ){
        operationOrder = CurrentCrudOperation.EDIT;

        payedMount = entity.getPaymentsMades().stream().map( n -> n.getAmountPaid() ).reduce( 0f , (sub , elem) -> sub+elem );

        orderReference.setText(  String.format("REF%08d", entity.getId() ) );

        orderDetails = entity;

        loadDataEdit();
        loadPayementTable();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        orderDetails = new OrderEntity();
        loadDataAdd();
        loadPayementTable();

    }



    void loadDataAdd(){

        List<ClientEntity> clients = clientServices.getAll();
        clientNameForm.setItems( FXCollections.observableArrayList( clients ) );

        new AutoCompleteBox(clientNameForm);

        clientNameForm.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {
            productChoosen( aluminuimProduct  , priceAluminumCombo );
            productChoosen( accessoireProduct , accessoirePrice );
            productChoosen( glassProduct      , glassPrice );
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

            float number = 0f;
            try{
                number = Float.parseFloat( newValue ) ;
            }catch (Exception e) {
                number = 0f;
            }

            if( number > 0 && number < totalOrder - totalPaid  && operationPayement == CurrentCrudOperation.ADD ){
                comboPaymentStatus.getSelectionModel().select( PaymentStatus.PARTRANCHES );
                savePayement.setDisable( false );
            } else if(totalOrder - totalPaid == 0 && operationPayement == CurrentCrudOperation.ADD){
                savePayement.setDisable( true );
            } else if( number >= totalOrder - totalPaid  && operationPayement == CurrentCrudOperation.ADD ){
                comboPaymentStatus.getSelectionModel().select( PaymentStatus.PAYÉ );
                amountToPayText.setText( totalOrder - totalPaid  + "" );
                savePayement.setDisable( false );
            }else if(operationPayement == CurrentCrudOperation.ADD){
                comboPaymentStatus.getSelectionModel().select( PaymentStatus.CRÉDIT );
                savePayement.setDisable( true );
            }


        } );


        initialiseAluminumTab();
        initialiseAccessorTab();
        initialiseGlassTab();

    }

    private void loadPayementTable() {
        
        observablePaymentsMades.clear();
        observablePaymentsMades.addAll( orderDetails.getPaymentsMades() );

        modeOfPayement.setCellValueFactory( new PropertyValueFactory<>("payementMethod"));
        payementAmout.setCellValueFactory(  new PropertyValueFactory<>("amountPaid"));
        dateOfPayement.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper(
                DateTimeFormatter.ofPattern( "dd/MM/yyyy" ).withZone(ZoneId.systemDefault()).format(  cellData.getValue().getPaymentDate() ))
        );

        Callback<TableColumn<PaymentsMadeEntity, Void>, TableCell<PaymentsMadeEntity, Void>> editCellFactory = new Callback<TableColumn<PaymentsMadeEntity, Void>, TableCell<PaymentsMadeEntity, Void>>() {
            @Override
            public TableCell<PaymentsMadeEntity, Void> call(final TableColumn<PaymentsMadeEntity, Void> param) {
                final TableCell<PaymentsMadeEntity, Void> cell = new TableCell<PaymentsMadeEntity, Void>() {

                    private final Button btn = new Button("Modifier");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            PaymentsMadeEntity data = getTableView().getItems().get(getIndex());
                            amountToPayText.setText(String.valueOf(data.getAmountPaid()));
                            paymentMethodGroup.selectToggle( ( data.getPayementMethod() == PayementMethod.CHEQUE ) ? chequeToggleButton: especeToggleButton );
                            savePayement.setDisable( false );
                            editablePayementMade = data ;
                            operationPayement = CurrentCrudOperation.EDIT;
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


        Callback<TableColumn<PaymentsMadeEntity, Void>, TableCell<PaymentsMadeEntity, Void>> deleteCellFactory = new Callback<TableColumn<PaymentsMadeEntity, Void>, TableCell<PaymentsMadeEntity, Void>>() {
            @Override
            public TableCell<PaymentsMadeEntity, Void> call(final TableColumn<PaymentsMadeEntity, Void> param) {
                final TableCell<PaymentsMadeEntity, Void> cell = new TableCell<PaymentsMadeEntity, Void>() {

                    private final Button btn = new Button("Delete");
                    {

                        btn.setOnAction((ActionEvent event) -> {
                            PaymentsMadeEntity data = getTableView().getItems().get(getIndex());
                            orderDetails.getPaymentsMades().remove( data );
                            loadPayementTable();
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
                return cell;
            }
        };

        editPayement.setCellFactory( editCellFactory );
        deletePayement.setCellFactory(deleteCellFactory );

        tableOfPayements.setItems( this.observablePaymentsMades );
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

        nombrePieceGlass.setValueFactory( new SpinnerValueFactory.IntegerSpinnerValueFactory(1 , 1000 , 1) );

        glassProduct.setItems( FXCollections.observableArrayList( glassService.getAllGlassProducts() ) );

        new AutoCompleteBox(glassProduct);

        glassProduct.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {
            productChoosen( glassProduct , glassPrice );
        });

        glassPrice.valueProperty().addListener(  (options, oldValue, newValue) -> {

            float number = 0f;
            try{
                number = Float.valueOf( glassQuentity.getText().equals("") ? "0" : glassQuentity.getText() );
            }catch (Exception e) {
                number = 1f;
            }

            glassTotal.setText( String.format( "%.2f DH" , number * (
                    ( glassPrice.getSelectionModel().getSelectedItem() == null ) ?
                            0 : getPrice(glassPrice)
            )));

        });

        glassQuentity.textProperty().addListener( (observable, oldValue, newValue) -> {

            float number = 0f;
            try{
                number = Float.valueOf( newValue.equals("") ? "0" : newValue );
            }catch (Exception e) {
                number = 1f;
            }

            glassTotal.setText( String.format( "%.2f DH" , number * (
                    ( glassPrice.getSelectionModel().getSelectedItem() == null ) ?
                            0 : getPrice(glassPrice)
            )));

        } );
    }

    private void initialiseAccessorTab() {
        accessoireProduct.setItems( FXCollections.observableArrayList(accessoryService.getAllAccessoryProducts()) );
        new AutoCompleteBox(accessoireProduct);
        accessoireProduct.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {
            productChoosen( accessoireProduct , accessoirePrice );
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
                            0 : getPrice(accessoirePrice)
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
                            0 : getPrice(accessoirePrice)
            ) + " DH");

        } );
    }

    private void initialiseAluminumTab() {

        aluminuimProduct.setItems( FXCollections.observableArrayList( aluminumService.getAllAlumunuimProducts() ) );
        new AutoCompleteBox(aluminuimProduct);
        aluminuimProduct.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {
            productChoosen( aluminuimProduct , priceAluminumCombo );
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

    private void productChoosen(ComboBox productCombo , ComboBox priceCombo) {
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
        }
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


            OrderItemsEntity alumenuimProduct = ( operation == CurrentCrudOperation.ADD ) ? new OrderItemsEntity() : editableOrderArticle;

            if( aluminuimProduct.getSelectionModel().getSelectedIndex() == -1 ){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Avertissement");
                alert.setHeaderText("veuillez choisir le produit");
                alert.showAndWait();
                return;
            }

            alumenuimProduct.setArticle( aluminuimProduct.getItems().get( aluminuimProduct.getSelectionModel().getSelectedIndex()) );
            alumenuimProduct.setName( aluminuimProduct.getItems().get( aluminuimProduct.getSelectionModel().getSelectedIndex()).getName() +" " + aluminuimLabel.getText() );
            float price = this.getPrice( priceAluminumCombo) ;
            alumenuimProduct.setPrice( price );
            alumenuimProduct.setQuantity( Float.valueOf( aluminuimQuentity.getText() ) );

        if( operation == CurrentCrudOperation.ADD ) orderDetails.getArticleOrders().add( alumenuimProduct );
        else editableOrderArticle = null;

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

    public void addAccessoryToOrder(MouseEvent mouseEvent) {

        OrderItemsEntity accessoryArticle = ( operation == CurrentCrudOperation.ADD ) ? new OrderItemsEntity() : editableOrderArticle;

        if( accessoireProduct.getSelectionModel().getSelectedIndex() == -1 ){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("veuillez choisir le produit");
            alert.showAndWait();
            return;
        }

        accessoryArticle.setArticle( accessoireProduct.getItems().get( accessoireProduct.getSelectionModel().getSelectedIndex() ) );
        accessoryArticle.setName( accessoireProduct.getItems().get( accessoireProduct.getSelectionModel().getSelectedIndex() ).getName() + " " + accessoireLabel.getText());
        accessoryArticle.setPrice( getPrice(accessoirePrice) );
        accessoryArticle.setQuantity(Float.valueOf(accessoireQuentity.getText()));

        if( operation == CurrentCrudOperation.ADD ) orderDetails.getArticleOrders().add(accessoryArticle);
        else editableOrderArticle = null;

        this.loadDataTable();

        accessoireProduct.getSelectionModel().select(-1); ;
        accessoireLabel.setText(""); ;
        accessoirePrice.getItems().clear(); ;
        accessoireQuentity.setText("1"); ;
        accessoireTotal.setText(" 00 DH"); ;
        operation = CurrentCrudOperation.ADD;

    }

    public void addGlassInOrder(MouseEvent mouseEvent) {

        OrderItemsEntity glassArticle = ( operation == CurrentCrudOperation.ADD ) ? new OrderItemsEntity() : editableOrderArticle;

        if( glassProduct.getSelectionModel().getSelectedIndex() == -1 ){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("veuillez choisir le produit");
            alert.showAndWait();
            return;
        }

        glassArticle.setArticle( glassProduct.getItems().get( glassProduct.getSelectionModel().getSelectedIndex()));
        glassArticle.setName( glassProduct.getItems().get( glassProduct.getSelectionModel().getSelectedIndex()).getName() + " " + glassLabel.getText());
        glassArticle.setPrice( getPrice(glassPrice) );
        glassArticle.setQuantity(Float.valueOf(glassQuentity.getText()));
        glassArticle.setNumberItems( nombrePieceGlass.getValue() );

        if( operation == CurrentCrudOperation.ADD ) orderDetails.getArticleOrders().add(glassArticle);
        else editableOrderArticle = null;

        this.loadDataTable();

        glassProduct.getSelectionModel().select(-1);
        glassLabel.setText("");
        glassPrice.getItems().clear();
        glassQuentity.setText("1");
        nombrePieceGlass.getValueFactory().setValue(1);//.setText("1");
        glassTotal.setText(" 00 DH");
        this.operation = CurrentCrudOperation.ADD;
    }


    void loadDataTable(){

        loadPaymentLabelsValues();


        observableArticleCommand.clear();
        observableArticleCommand.addAll( orderDetails.getArticleOrders() );

        lableOfCommand.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceProductOfCommand.setCellValueFactory(new PropertyValueFactory<>("price"));
        quentityProductOfCommand.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCommand.setCellValueFactory( cellData ->  new ReadOnlyObjectWrapper(
                String.format("%.2f DH ", cellData.getValue().getQuantity() * cellData.getValue().getPrice() )
        ) );



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
                                    System.out.println( "AluminumEntity" );
                                    loadAluminuimEdit(data);

                                    break;

                                case "AccessoryEntity" :
                                    System.out.println( "AccessoryEntity" );
                                    loadAccessoryEdit(data);

                                    break;

                                case "GlassEntity"     :
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

    private void loadPaymentLabelsValues() {
        totalPriceOrder.setText( "00,00 DH" );
        totalPriceOrder.setText( String.format("%.2f DH", getTotal() ) );

        amountPaidOrder.setText("00,00 DH");
        amountPaidOrder.setText( String.format("%.2f DH", calculAmountPaid() ) );

        amountRemainedOrder.setText("00,00 DH");
        amountRemainedOrder.setText( String.format("%.2f DH ", getTotal() - calculAmountPaid() ) );
    }

    private void loadGlassEdit(OrderItemsEntity data) {

        glassProduct.getSelectionModel().select((GlassEntity) data.getArticle());
        glassLabel.setText( data.getName().replace( data.getArticle().getName() , "" ).trim() );
        glassPrice.getSelectionModel().select( data.getPrice()+"" );
        glassQuentity.setText( data.getQuantity() + "" );
        nombrePieceGlass.getValueFactory().setValue( data.getNumberItems() );


        glassTotal.setText( String.format( "%.2f DH" , Float.valueOf( data.getQuantity() ) * (
                ( glassPrice.getSelectionModel().getSelectedItem() == null ) ?
                        0 : getPrice(glassPrice)
        )));


        tabPaneAddProducts.getSelectionModel().select(2);
        editableOrderArticle = data;
        operation = CurrentCrudOperation.EDIT;

    }

    private void loadAccessoryEdit(OrderItemsEntity data) {
        accessoireProduct.getSelectionModel().select((AccessoryEntity) data.getArticle());
        accessoireLabel.setText( data.getName().replace( data.getArticle().getName() , "" ).trim() );
        accessoirePrice.getSelectionModel().select( data.getPrice() + "" );
        accessoireQuentity.setText( data.getQuantity() + "" );

        accessoireTotal.setText( Float.valueOf( data.getQuantity() ) * (
                ( accessoirePrice.getSelectionModel().getSelectedItem() == null ) ?
                        0 :  getPrice( accessoirePrice)
        ) + " DH");

        tabPaneAddProducts.getSelectionModel().select(1);
        editableOrderArticle = data;
        operation = CurrentCrudOperation.EDIT;
    }

    private void loadAluminuimEdit(OrderItemsEntity data) {
        aluminuimProduct.getSelectionModel().select((AluminumEntity) data.getArticle());
        aluminuimLabel.setText( data.getName().replace(  data.getArticle().getName() , "").trim() );
        priceAluminumCombo.getSelectionModel().select( String.valueOf( data.getPrice() ) );
        aluminuimQuentity.setText( data.getQuantity() + "");


        priceAlumnuimShow.setText( Float.valueOf( data.getQuantity() ) * (
                ( priceAluminumCombo.getSelectionModel().getSelectedItem() == null ) ?
                        0 : getPrice( priceAluminumCombo)
        ) + " DH");


        tabPaneAddProducts.getSelectionModel().selectFirst();
        editableOrderArticle = data;
        operation = CurrentCrudOperation.EDIT;
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

        this.goBack(null);

    }

    private void savePricesClient(){

        orderDetails.getArticleOrders();

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

    public void addPaymentMade(MouseEvent mouseEvent) {

        PayementMethod payementMethod = ((RadioButton) paymentMethodGroup.getSelectedToggle()).getText().equals("Espéce") ?
                        PayementMethod.ESPECE : PayementMethod.CHEQUE ;

        PaymentsMadeEntity paymentsMadeEntity = (operationPayement == CurrentCrudOperation.ADD ) ? new PaymentsMadeEntity() : editablePayementMade;

        paymentsMadeEntity.setAmountPaid( Float.valueOf( amountToPayText.getText()) );
        paymentsMadeEntity.setPayementMethod( payementMethod );

        if(operationPayement == CurrentCrudOperation.ADD ) orderDetails.getPaymentsMades().add( paymentsMadeEntity );
        else editablePayementMade = null;

        this.loadDataTable();

        loadPayementTable();
        loadPaymentLabelsValues();
        amountToPayText.setText("0");
        paymentMethodGroup.selectToggle(especeToggleButton);
        operationPayement = CurrentCrudOperation.ADD;

    }
}
