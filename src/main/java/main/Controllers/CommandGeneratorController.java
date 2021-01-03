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
import main.Models.enums.PaymentStatus;
import main.Models.utils.CurrentCrudOperation;
import main.Services.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CommandGeneratorController implements Initializable {

    private final CommandService commandService = new CommandService();
    private final ClientServices clientServices = new ClientServices();
    private final AccessoryService accessoryService = new AccessoryService();
    private final AluminumService aluminumService = new AluminumService();
    private final GlassService glassService = new GlassService();

    private CurrentCrudOperation operation = CurrentCrudOperation.ADD;
    private CurrentCrudOperation operationCommand = CurrentCrudOperation.ADD ;


    @FXML ComboBox<ClientEntity> clientNameForm ;
    @FXML Label totalePriceCommand ;
    @FXML ComboBox<PaymentStatus> comboPaymentStatus;
    @FXML TextField amountToPayText ;

    @FXML Label orderReference;

    // ---------- Aluminum Tab -------------
    @FXML ComboBox<AluminumEntity> aluminuimProduct;
    @FXML TextField aluminuimLabel;
    @FXML ComboBox<PriceEntity> priceAluminumCombo ;
    @FXML TextField aluminuimContity;
    @FXML Label priceAlumnuimShow;

    // ------------ Accessoire Tab --------

    @FXML ComboBox<AccessoryEntity> accessoireProduct;
    @FXML TextField accessoireLabel ;
    @FXML ComboBox<PriceEntity> accessoirePrice ;
    @FXML TextField accessoireQuentity ;
    @FXML Label  accessoireTotal ;



    // --------------- GlassTab  -----------------
    @FXML ComboBox<GlassEntity> glassProduct;
    @FXML TextField glassLabel ;
    @FXML ComboBox<PriceEntity> glassPrice ;
    @FXML TextField glassQuentity ;
    @FXML Label glassTotal ;

    // --------- TABPANE Switch Produts-----------

    @FXML TabPane tabPaneAddProducts;

    // -----------Table Products------------

    @FXML TableView<ArticleCommandEntity> tableProductsOfCommand;
    @FXML TableColumn<ArticleCommandEntity , String> lableOfCommand ;
    @FXML TableColumn<ArticleCommandEntity , String> priceProductOfCommand ;
    @FXML TableColumn<ArticleCommandEntity , String> quentityProductOfCommand ;
    @FXML TableColumn<ArticleCommandEntity , String> priceCommand ;
    @FXML TableColumn<ArticleCommandEntity , Void> editProductOfCommand ;
    @FXML TableColumn<ArticleCommandEntity , Void> deleteProductOfCommand ;
    ObservableList<ArticleCommandEntity> observableArticleCommand = FXCollections.observableArrayList();


    public CommandEntity commandDetails ;
    private PaymentStatus OldPayementStatusBkp ;
    private float OldTotal ;
    private float payedMount ;


    private boolean isWorkingPayementCombo = false;
    private boolean isWorkingPriceTextFiltred = false ;

    private ArticleCommandEntity editableCommandArticle = null;

    public void setData( CommandEntity entity ){
        operationCommand = CurrentCrudOperation.EDIT;

        OldTotal = entity.getArticleCommands().stream()
                .map( n -> n.getPrice()* n.getQuantity() )
                .collect( Collectors.toList() )
                .stream()
                .reduce( 0f ,  ( subtotal , element ) -> subtotal + element );

        payedMount = entity.getPaymentsMades().stream().map( n -> n.getAmountPaid() ).reduce( 0f , (sub , elem) -> sub+elem );

        orderReference.setText(  String.format("REF%010d", 15 )   );

        OldPayementStatusBkp = entity.getPaymentStatus();

        commandDetails = entity;

        loadDataEdit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        commandDetails = new CommandEntity();
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
/*
            private boolean isWorkingPayementCombo = false;
            private boolean isWorkingPriceTextFiltred = false ;*/

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
/*                if( OldPayementStatusBkp == PaymentStatus.CREDIT ){
                    System.out.println("bbb");
                    if( Float.valueOf( newValue ) >= (getTotal() - payedMount) && comboPaymentStatus.getSelectionModel().getSelectedItem() == PaymentStatus.PAYINPARTS ){
                        System.out.println("ccc");
                        comboPaymentStatus.getSelectionModel().select( PaymentStatus.COMPLETED );
                    }

                }else{
                    System.out.println("ddd");
                    if( Float.valueOf( newValue ) >= (getTotal() - OldTotal) && comboPaymentStatus.getSelectionModel().getSelectedItem() == PaymentStatus.PAYINPARTS ){
                        System.out.println("eee");
                        comboPaymentStatus.getSelectionModel().select( PaymentStatus.COMPLETED );
                    }
                }*/


                if( OldPayementStatusBkp == PaymentStatus.CREDIT ){
                    // System.out.println("aaaaaa");
                    if( Float.valueOf( newValue ) >= (getTotal() - payedMount) ){
                        comboPaymentStatus.getSelectionModel().select( PaymentStatus.COMPLETED );
                        amountToPayText.setText( getTotal() - payedMount + "" );
                    }

                }else if( OldPayementStatusBkp == PaymentStatus.PAYINPARTS ) {

                    if( Float.valueOf( newValue ) >= (getTotal() - payedMount) ){
                        comboPaymentStatus.getSelectionModel().select( PaymentStatus.COMPLETED );
                        amountToPayText.setText( getTotal() - payedMount + "" );
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

        glassPrice.getSelectionModel().selectedIndexProperty().addListener(  (options, oldValue, newValue) -> {

            float number = 0f;
            try{
                number = Float.valueOf( glassQuentity.getText().equals("") ? "0" : glassQuentity.getText() );
            }catch (Exception e) {
                number = 1f;
            }

            glassTotal.setText( number * (
                    ( glassPrice.getSelectionModel().getSelectedItem() == null ) ?
                            0 : glassPrice.getSelectionModel().getSelectedItem().getPrice()
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
                            0 : glassPrice.getSelectionModel().getSelectedItem().getPrice()
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

        accessoirePrice.getSelectionModel().selectedIndexProperty().addListener(  (options, oldValue, newValue) -> {

            float number = 0f;
            try{
                number = Float.valueOf( accessoireQuentity.getText().equals("") ? "0" : accessoireQuentity.getText() );
            }catch (Exception e) {
                number = 1f;
            }

            accessoireTotal.setText( number * (
                    ( accessoirePrice.getSelectionModel().getSelectedItem() == null) ?
                            0 : accessoirePrice.getSelectionModel().getSelectedItem().getPrice()
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
                            0 : accessoirePrice.getSelectionModel().getSelectedItem().getPrice()
            ) + " DH");

        } );
    }

    private void initialiseAluminumTab() {

        aluminuimProduct.setItems( FXCollections.observableArrayList( aluminumService.getAllAlumunuimProducts() ) );
        aluminuimProduct.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {
            priceAluminumCombo.getItems().clear();
            //check if Is Select item or null
            if( (int) newValue != -1 ){
                priceAluminumCombo.setItems( FXCollections.observableArrayList( aluminuimProduct.getSelectionModel().getSelectedItem().getPrices()  ) );
                priceAluminumCombo.getSelectionModel().selectFirst();
            }

        });

        priceAluminumCombo.getSelectionModel().selectedIndexProperty().addListener(  (options, oldValue, newValue) -> {

            float number = 0f;
            try{
                number = Float.valueOf( aluminuimContity.getText().equals("") ? "0" : aluminuimContity.getText() );
            }catch (Exception e) {
                number = 1f;
            }

            float total = number * (
                    ( priceAluminumCombo.getSelectionModel().getSelectedItem() == null ) ?
                            0 : priceAluminumCombo.getSelectionModel().getSelectedItem().getPrice()
            );

            priceAlumnuimShow.setText( total + " DH");

        });

        aluminuimContity.textProperty().addListener( (observable, oldValue, newValue) -> {
            float number = 0f;
            try{
                number = Float.valueOf( newValue.equals("") ? "0" : newValue );
            }catch (Exception e) {
                number = 1f;
            }
            float total = number * (
                    ( priceAluminumCombo.getSelectionModel().getSelectedItem() == null ) ?
                            0 : priceAluminumCombo.getSelectionModel().getSelectedItem().getPrice()
            );
            priceAlumnuimShow.setText( total + " DH");

        } );
    }


    public void goBack(MouseEvent mouseEvent) throws IOException {


        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ListCommandsView.fxml"));
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

    public void saveAlumnuimInCommand(MouseEvent mouseEvent) {

        if( operation == CurrentCrudOperation.ADD ){
            ArticleCommandEntity alumenuimProduct = new ArticleCommandEntity();

            alumenuimProduct.setArticle( aluminuimProduct.getSelectionModel().getSelectedItem() );
            alumenuimProduct.setName( aluminuimProduct.getSelectionModel().getSelectedItem().getName() +" " + aluminuimLabel.getText()  );
            alumenuimProduct.setPrice( priceAluminumCombo.getSelectionModel().getSelectedItem().getPrice() );
            alumenuimProduct.setQuantity( Float.valueOf( aluminuimContity.getText() ) );
            alumenuimProduct.setPriceOfArticle( priceAluminumCombo.getSelectionModel().getSelectedItem() );

            commandDetails.getArticleCommands().add( alumenuimProduct );
        }else{

            ArticleCommandEntity alumenuimProduct = editableCommandArticle;
            alumenuimProduct.setArticle( aluminuimProduct.getSelectionModel().getSelectedItem() );
            alumenuimProduct.setName( aluminuimProduct.getSelectionModel().getSelectedItem().getName() +" " + aluminuimLabel.getText()  );
            alumenuimProduct.setPrice( priceAluminumCombo.getSelectionModel().getSelectedItem().getPrice() );
            alumenuimProduct.setQuantity( Float.valueOf( aluminuimContity.getText() ) );
            alumenuimProduct.setPriceOfArticle( priceAluminumCombo.getSelectionModel().getSelectedItem() );
            editableCommandArticle = null;
        }

        this.loadDataTable();

        if( this.operationCommand == CurrentCrudOperation.EDIT )
            restorePaimentStatus();

        aluminuimProduct.getSelectionModel().select(-1);
        aluminuimLabel.setText("");
        priceAluminumCombo.getItems().clear();
        aluminuimContity.setText("1");
        priceAlumnuimShow.setText(" 00 DH");
        operation = CurrentCrudOperation.ADD;
    }



    public void saveAccessoireInCommand(MouseEvent mouseEvent) {
        if( operation == CurrentCrudOperation.ADD ) {
            ArticleCommandEntity accessoireArticle = new ArticleCommandEntity();

            accessoireArticle.setArticle(accessoireProduct.getSelectionModel().getSelectedItem());
            accessoireArticle.setName(accessoireProduct.getSelectionModel().getSelectedItem().getName() + " " + accessoireLabel.getText());
            accessoireArticle.setPrice(accessoirePrice.getSelectionModel().getSelectedItem().getPrice());
            accessoireArticle.setQuantity(Float.valueOf(accessoireQuentity.getText()));
            accessoireArticle.setPriceOfArticle(accessoirePrice.getSelectionModel().getSelectedItem());
            commandDetails.getArticleCommands().add(accessoireArticle);

        }else{
            ArticleCommandEntity accessoireArticle = editableCommandArticle;
            accessoireArticle.setArticle(accessoireProduct.getSelectionModel().getSelectedItem());
            accessoireArticle.setName(accessoireProduct.getSelectionModel().getSelectedItem().getName() + " " + accessoireLabel.getText());
            accessoireArticle.setPrice(accessoirePrice.getSelectionModel().getSelectedItem().getPrice());
            accessoireArticle.setQuantity(Float.valueOf(accessoireQuentity.getText()));
            accessoireArticle.setPriceOfArticle(accessoirePrice.getSelectionModel().getSelectedItem());
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
            ArticleCommandEntity glassArticle = new ArticleCommandEntity();

            glassArticle.setArticle(glassProduct.getSelectionModel().getSelectedItem());
            glassArticle.setName(glassProduct.getSelectionModel().getSelectedItem().getName() + " " + glassLabel.getText());
            glassArticle.setPrice(glassPrice.getSelectionModel().getSelectedItem().getPrice());
            glassArticle.setQuantity(Float.valueOf(glassQuentity.getText()));
            glassArticle.setPriceOfArticle(glassPrice.getSelectionModel().getSelectedItem());

            commandDetails.getArticleCommands().add(glassArticle);
        }else{
            ArticleCommandEntity glassArticle = editableCommandArticle ;
            glassArticle.setArticle(glassProduct.getSelectionModel().getSelectedItem());
            glassArticle.setName(glassProduct.getSelectionModel().getSelectedItem().getName() + " " + glassLabel.getText());
            glassArticle.setPrice(glassPrice.getSelectionModel().getSelectedItem().getPrice());
            glassArticle.setQuantity(Float.valueOf(glassQuentity.getText()));
            glassArticle.setPriceOfArticle(glassPrice.getSelectionModel().getSelectedItem());
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

            //System.out.println( "----------- nn ------------"  +  newF +" ++ " + OldTotal  + " -- "  +  (newF > OldTotal)  );

            //if( newF > OldTotal ){
                comboPaymentStatus.getSelectionModel().select( PaymentStatus.PAYINPARTS );
                amountToPayText.setText( newF - OldTotal + " " );
            //}

        }

    }

    void loadDataTable(){

        totalePriceCommand.setText( "00,00 DH" );
        float total = getTotal();
        totalePriceCommand.setText( total + " DH" );


        observableArticleCommand.clear();
        observableArticleCommand.addAll( commandDetails.getArticleCommands() );

        lableOfCommand.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceProductOfCommand.setCellValueFactory(new PropertyValueFactory<>("price"));
        quentityProductOfCommand.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceCommand.setCellValueFactory( cellData ->  new ReadOnlyObjectWrapper( cellData.getValue().getQuantity() * cellData.getValue().getPrice() + " DH" ) );


        Callback<TableColumn<ArticleCommandEntity, Void>, TableCell<ArticleCommandEntity, Void>> editCellFactory = new Callback<TableColumn<ArticleCommandEntity, Void>, TableCell<ArticleCommandEntity, Void>>() {
            @Override
            public TableCell<ArticleCommandEntity, Void> call(final TableColumn<ArticleCommandEntity, Void> param) {
                final TableCell<ArticleCommandEntity, Void> cell = new TableCell<ArticleCommandEntity, Void>() {

                    private final Button btn = new Button("Edit");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            ArticleCommandEntity data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data.getId());


                            switch ( data.getArticle().getType() ){
                                case "AluminumEntity"  :

                                    System.out.println( "AluminumEntity" );

                                    aluminuimProduct.getSelectionModel().select((AluminumEntity) data.getArticle());
                                    aluminuimLabel.setText( data.getName().replace(  data.getArticle().getName() , "").trim() );
                                    priceAluminumCombo.getSelectionModel().select( data.getPriceOfArticle() );
                                    aluminuimContity.setText( data.getQuantity() + "");

                                    priceAlumnuimShow.setText( Float.valueOf( data.getQuantity() ) * (
                                            ( priceAluminumCombo.getSelectionModel().getSelectedIndex() == -1 ) ?
                                                    0 : priceAluminumCombo.getSelectionModel().getSelectedItem().getPrice()
                                    ) + " DH");


                                    tabPaneAddProducts.getSelectionModel().selectFirst();
                                    editableCommandArticle = data;
                                    operation = CurrentCrudOperation.EDIT;

                                    break;

                                case "AccessoryEntity" :
                                    System.out.println( "AccessoryEntity" );

                                    accessoireProduct.getSelectionModel().select((AccessoryEntity) data.getArticle());
                                    accessoireLabel.setText( data.getName().replace( data.getArticle().getName() , "" ).trim() );
                                    accessoirePrice.getSelectionModel().select( data.getPriceOfArticle() );
                                    accessoireQuentity.setText( data.getQuantity() + "" );

                                    accessoireTotal.setText( Float.valueOf( data.getQuantity() ) * (
                                            ( accessoirePrice.getSelectionModel().getSelectedIndex() == -1 ) ?
                                                    0 : accessoirePrice.getSelectionModel().getSelectedItem().getPrice()
                                    ) + " DH");

                                    tabPaneAddProducts.getSelectionModel().select(1);
                                    editableCommandArticle = data;
                                    operation = CurrentCrudOperation.EDIT;

                                    break;

                                case "GlassEntity"     :
                                    System.out.println( "GlassEntity" );

                                    glassProduct.getSelectionModel().select((GlassEntity) data.getArticle());
                                    glassLabel.setText( data.getName().replace( data.getArticle().getName() , "" ).trim() );
                                    glassPrice.getSelectionModel().select( data.getPriceOfArticle() );
                                    glassQuentity.setText( data.getQuantity() + "" );


                                    glassTotal.setText( Float.valueOf( data.getQuantity() ) * (
                                            ( glassPrice.getSelectionModel().getSelectedIndex() == -1 ) ?
                                                    0 : glassPrice.getSelectionModel().getSelectedItem().getPrice()
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


        Callback<TableColumn<ArticleCommandEntity, Void>, TableCell<ArticleCommandEntity, Void>> deleteCellFactory = new Callback<TableColumn<ArticleCommandEntity, Void>, TableCell<ArticleCommandEntity, Void>>() {
            @Override
            public TableCell<ArticleCommandEntity, Void> call(final TableColumn<ArticleCommandEntity, Void> param) {
                final TableCell<ArticleCommandEntity, Void> cell = new TableCell<ArticleCommandEntity, Void>() {

                    private final Button btn = new Button("Delete");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            ArticleCommandEntity data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData delete: " + data.getId());
                            commandDetails.getArticleCommands().remove( data );
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

        tableProductsOfCommand.setItems( FXCollections.observableArrayList(commandDetails.getArticleCommands()) );
        tableProductsOfCommand.setItems( this.observableArticleCommand );

    }

    private Float getTotal(){

        float total = commandDetails.getArticleCommands().stream()
                          .map( n -> n.getPrice()* n.getQuantity() )
                          .collect( Collectors.toList() )
                          .stream()
                          .reduce( 0f ,  ( subtotal , element ) -> subtotal + element );
        return total;
    }

    public void saveCommandEvent(MouseEvent mouseEvent) throws IOException {


        ClientEntity clientEntity = ( operationCommand == CurrentCrudOperation.ADD ) ?
                    clientNameForm.getItems().get( clientNameForm.getSelectionModel().getSelectedIndex() ) :
                    clientNameForm.getSelectionModel().getSelectedItem() ;

        commandDetails.setClient( clientEntity );
        commandDetails.setPaymentStatus( comboPaymentStatus.getSelectionModel().getSelectedItem() );

        PaymentsMadeEntity paymentsMadeEntity = new PaymentsMadeEntity();

        paymentsMadeEntity.setAmountPaid( Float.valueOf( amountToPayText.getText()) );

        commandDetails.getPaymentsMades().add( paymentsMadeEntity );

        if( operationCommand == CurrentCrudOperation.ADD ) {

            boolean saved = commandService.addCommand(commandDetails);

            if (saved) {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("l'ajout de Command réussi");
                alert.setHeaderText("le Command est bien ajouté");
                alert.showAndWait();

                Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ListCommandsView.fxml"));
                main.JavaFxApplication.mainStage.setScene(new Scene(root));
                main.JavaFxApplication.mainStage.setTitle(" List Commands -- Aluminium et verre");
                main.JavaFxApplication.mainStage.show();

            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error D'ajouter");
                alert.setHeaderText("Oups, il y a eu une erreur!");
                alert.showAndWait();
            }
        }else{
            System.out.println(" Operation Edit ");

            //CommandEntity oo = commandDetails;

            boolean saved = commandService.updateOrder(commandDetails);

            System.out.println( saved );

        }

    }
}
