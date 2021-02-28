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
import main.Models.entities.queryContainers.ArticleStockStatus;
import main.Models.entities.queryContainers.StockArticleItems;
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
    @FXML RadioButton especeToggleButton;
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

    // --------------- GlassTab  -----------------
    @FXML ComboBox<GlassEntity> glassProduct;
    @FXML TextField glassLabel ;
    @FXML ComboBox<Object> glassPrice ;
    @FXML TextField glassQuantity;
    @FXML Label glassTotal ;
    @FXML Spinner<Integer> numberPieceGlass;

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
    private float payedMount ;
    private OrderItemsEntity editableOrderArticle = null;
    private PaymentsMadeEntity editablePaymentMade = null ;


    public void setData( OrderEntity entity ){
        operationOrder = CurrentCrudOperation.EDIT;

        payedMount = entity.getPaymentsMades().stream().map( n -> n.getAmountPaid() ).reduce( 0f , (sub , elem) -> sub+elem );

        orderReference.setText(  String.format("REF%08d", entity.getId() ) );

        orderDetails = entity;

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
            productChosen(aluminumProduct, priceAluminumCombo );
            productChosen(accessoryProduct, accessoryPrice);
            productChosen( glassProduct      , glassPrice );
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
                final TableCell<PaymentsMadeEntity, Void> cell = new TableCell<PaymentsMadeEntity, Void>() {

                    private final Button btn = new Button("Modifier");
                    {
                        btn.setOnAction((ActionEvent event) -> {
                            PaymentsMadeEntity data = getTableView().getItems().get(getIndex());
                            amountToPayText.setText(String.valueOf(data.getAmountPaid()));
                            paymentMethodGroup.selectToggle( ( data.getPayementMethod() == PayementMethod.CHEQUE ) ? chequeToggleButton: especeToggleButton );
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
                return cell;
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

            float number = 0f;
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
        accessoryProduct.setItems( FXCollections.observableArrayList(accessoryService.getAllAccessoryProducts()) );
        new AutoCompleteBox(accessoryProduct);
        accessoryProduct.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {
            productChosen(accessoryProduct, accessoryPrice);
        });

        accessoryPrice.valueProperty().addListener(  (options, oldValue, newValue) -> {

            float number = 0f;
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

            float number = 0f;
            try{
                number = Float.valueOf( newValue.equals("") ? "0" : newValue );
            }catch (Exception e) {
                number = 1f;
            }

            accessoryTotal.setText( number * (
                    ( accessoryPrice.getSelectionModel().getSelectedItem() == null ) ?
                            0 : getPrice(accessoryPrice)
            ) + " DH");

        } );
    }

    private void initialiseAluminumTab() {

        aluminumProduct.setItems( FXCollections.observableArrayList( aluminumService.getAllAlumunuimProducts() ) );
        new AutoCompleteBox(aluminumProduct);
        aluminumProduct.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {
            productChosen(aluminumProduct, priceAluminumCombo );
        });

        priceAluminumCombo.valueProperty().addListener( (options, oldValue, newValue) -> {

            float number = 0f;
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

            float number = 0f;
            try{
                number = Float.valueOf( newValue.equals("") ? "0" : newValue );
            }catch (Exception e) {
                number = 0f;
            }

            if( comboAlumStockArticle.getSelectionModel().getSelectedIndex() != -1 ) {

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

            float number = 0f;
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


            AluminumEntity aluminumEntity = (AluminumEntity) productCombo.getItems().get( productCombo.getSelectionModel().getSelectedIndex() );

            List<StockArticleItems> listProductsStockStatus = stockService.getProductsStockStatus( aluminumEntity.getId() );

            Collections.sort(listProductsStockStatus);
            comboAlumStockArticle.getItems().clear();
            comboAlumStockArticle.setItems( FXCollections.observableArrayList( listProductsStockStatus ) );
            comboAlumStockArticle.getSelectionModel().selectFirst();


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

    public void addAluminumToOrder(MouseEvent mouseEvent) {


        OrderItemsEntity aluminumProduct = ( operation == CurrentCrudOperation.ADD ) ? new OrderItemsEntity() : editableOrderArticle;

        if( this.aluminumProduct.getSelectionModel().getSelectedIndex() == -1 ){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("veuillez choisir le produit");
            alert.showAndWait();
            return;
        }

        aluminumProduct.setArticle( this.aluminumProduct.getItems().get( this.aluminumProduct.getSelectionModel().getSelectedIndex()) );
        aluminumProduct.setName( this.aluminumProduct.getItems().get( this.aluminumProduct.getSelectionModel().getSelectedIndex()).getName() +" " + aluminumLabel.getText() );
        float price = this.getPrice( priceAluminumCombo) ;
        aluminumProduct.setPrice( price );
        float quantity = Float.valueOf( aluminumQuantity.getText() );
        aluminumProduct.setQuantity( quantity );
        if( comboAlumStockArticle.getSelectionModel().getSelectedIndex() != -1 ) {
            aluminumProduct.setStockItemId( comboAlumStockArticle.getSelectionModel().getSelectedItem().getStockItems().getId() );
        }


        if( operation == CurrentCrudOperation.ADD )
            orderDetails.getArticleOrders().add(aluminumProduct);
        else
            editableOrderArticle = null;


        this.loadDataTable();

        this.aluminumProduct.getSelectionModel().select(-1);
        aluminumLabel.setText("");
        priceAluminumCombo.getItems().clear();
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

    public void addAccessoryToOrder(MouseEvent mouseEvent) {

        OrderItemsEntity accessoryArticle = ( operation == CurrentCrudOperation.ADD ) ? new OrderItemsEntity() : editableOrderArticle;

        if( accessoryProduct.getSelectionModel().getSelectedIndex() == -1 ){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Avertissement");
            alert.setHeaderText("veuillez choisir le produit");
            alert.showAndWait();
            return;
        }

        accessoryArticle.setArticle( accessoryProduct.getItems().get( accessoryProduct.getSelectionModel().getSelectedIndex() ) );
        accessoryArticle.setName( accessoryProduct.getItems().get( accessoryProduct.getSelectionModel().getSelectedIndex() ).getName() + " " + accessoryLabel.getText());
        accessoryArticle.setPrice( getPrice(accessoryPrice) );
        accessoryArticle.setQuantity(Float.valueOf(accessoryQuantity.getText()));

        if( operation == CurrentCrudOperation.ADD ) orderDetails.getArticleOrders().add(accessoryArticle);
        else editableOrderArticle = null;

        this.loadDataTable();

        accessoryProduct.getSelectionModel().select(-1); ;
        accessoryLabel.setText(""); ;
        accessoryPrice.getItems().clear(); ;
        accessoryQuantity.setText("1"); ;
        accessoryTotal.setText(" 00 DH"); ;
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
        glassArticle.setQuantity(Float.valueOf(glassQuantity.getText()));
        glassArticle.setNumberItems( numberPieceGlass.getValue() );

        if( operation == CurrentCrudOperation.ADD ) orderDetails.getArticleOrders().add(glassArticle);
        else editableOrderArticle = null;

        this.loadDataTable();

        glassProduct.getSelectionModel().select(-1);
        glassLabel.setText("");
        glassPrice.getItems().clear();
        glassQuantity.setText("1");
        numberPieceGlass.getValueFactory().setValue(1);//.setText("1");
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
                final TableCell<OrderItemsEntity, Void> cell = new TableCell<OrderItemsEntity, Void>() {

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
        aluminumProduct.getSelectionModel().select((AluminumEntity) data.getArticle());
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

        float total = orderDetails.getArticleOrders().stream()
                          .map( n -> n.getPrice()* n.getQuantity() )
                          .collect( Collectors.toList() )
                          .stream()
                          .reduce( 0f ,  ( subtotal , element ) -> subtotal + element );
        return total;
    }

    private Float calculateAmountPaid(){

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

        PaymentsMadeEntity paymentsMadeEntity = (operationPayment == CurrentCrudOperation.ADD ) ? new PaymentsMadeEntity() : editablePaymentMade;

        paymentsMadeEntity.setAmountPaid( Float.valueOf( amountToPayText.getText()) );
        paymentsMadeEntity.setPayementMethod( payementMethod );

        if(operationPayment == CurrentCrudOperation.ADD ) orderDetails.getPaymentsMades().add( paymentsMadeEntity );
        else editablePaymentMade = null;

        this.loadDataTable();

        loadPaymentTable();
        loadPaymentLabelsValues();
        amountToPayText.setText("0");
        paymentMethodGroup.selectToggle(especeToggleButton);
        operationPayment = CurrentCrudOperation.ADD;

    }
}
