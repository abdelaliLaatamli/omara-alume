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
import main.Services.AccessoryService;
import main.Services.AluminumService;
import main.Services.ClientServices;
import main.Services.GlassService;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CommandGeneratorController implements Initializable {


    private final ClientServices clientServices = new ClientServices();
    private final AccessoryService accessoryService = new AccessoryService();
    private final AluminumService aluminumService = new AluminumService();
    private final GlassService glassService = new GlassService();


    @FXML ComboBox<String> clientNameForm ;

    @FXML Label totalePriceCommand ;
    @FXML ComboBox<PaymentStatus> comboPaymentStatus;
    @FXML TextField amountToPayText ;


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

    // --------------------
    @FXML TableView<ArticleCommandEntity> tableProductsOfCommand;
    @FXML TableColumn<ArticleCommandEntity , String> lableOfCommand ;
    //@FXML TableColumn<ArticleCommandEntity , String> nameProductOfCommand;
    @FXML TableColumn<ArticleCommandEntity , String> priceProductOfCommand ;
    @FXML TableColumn<ArticleCommandEntity , String> quentityProductOfCommand ;
    @FXML TableColumn<ArticleCommandEntity , String> priceCommand ;
    @FXML TableColumn<ArticleCommandEntity , Void> editProductOfCommand ;
    @FXML TableColumn<ArticleCommandEntity , Void> deleteProductOfCommand ;
    ObservableList<ArticleCommandEntity> observableArticleCommand = FXCollections.observableArrayList();

    List<ArticleCommandEntity> list = new ArrayList<>();


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadData();

    }

    void loadData(){

        List<ClientEntity> clients = clientServices.getAll();
        clientNameForm.setItems(FXCollections.observableArrayList( clients.stream().map( c -> c.getName()  ).collect(Collectors.toList()) ));
        new AutoCompleteBox(clientNameForm);

        comboPaymentStatus.setItems( FXCollections.observableArrayList( PaymentStatus.values() ) );
        comboPaymentStatus.getSelectionModel().selectFirst();
        comboPaymentStatus.getSelectionModel().selectedIndexProperty().addListener( (options, oldValue, newValue) -> {

            // System.out.println( newValue );

            amountToPayText.setEditable( (int) newValue == 1 );
            amountToPayText.setText( (int) newValue == 2 ? getTotal() + "" : "0.0" );

        } );


        initialiseAluminumTab();
        initialiseAccessoireTab();
        initialiseGlassTab();

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
                    ( glassPrice.getSelectionModel().getSelectedIndex() == -1 ) ?
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
                    ( glassPrice.getSelectionModel().getSelectedIndex() == -1 ) ?
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
                    ( accessoirePrice.getSelectionModel().getSelectedIndex() == -1 ) ?
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
                    ( accessoirePrice.getSelectionModel().getSelectedIndex() == -1 ) ?
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

            priceAlumnuimShow.setText( number * (
                                                    ( priceAluminumCombo.getSelectionModel().getSelectedIndex() == -1 ) ?
                                                            0 : priceAluminumCombo.getSelectionModel().getSelectedItem().getPrice()
                                                ) + " DH");

        });

        aluminuimContity.textProperty().addListener( (observable, oldValue, newValue) -> {

            float number = 0f;
            try{
                number = Float.valueOf( newValue.equals("") ? "0" : newValue );
            }catch (Exception e) {
                number = 1f;
            }

            priceAlumnuimShow.setText( number * (
                                                    ( priceAluminumCombo.getSelectionModel().getSelectedIndex() == -1 ) ?
                                                        0 : priceAluminumCombo.getSelectionModel().getSelectedItem().getPrice()
                                                ) + " DH");

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

    public void ajouterAlumnuimToFucture(MouseEvent mouseEvent) {

        ArticleCommandEntity alumenuimProduct = new ArticleCommandEntity();

        alumenuimProduct.setArticle( aluminuimProduct.getSelectionModel().getSelectedItem() );
        alumenuimProduct.setName( aluminuimProduct.getSelectionModel().getSelectedItem().getName() +" " + aluminuimLabel.getText()  );
        alumenuimProduct.setPrice( priceAluminumCombo.getSelectionModel().getSelectedItem().getPrice() );
        alumenuimProduct.setQuantity( Float.valueOf( aluminuimContity.getText() ) );


        list.add( alumenuimProduct );
        this.loadDataTable();

        aluminuimProduct.getSelectionModel().select(-1);
        aluminuimLabel.setText("");
        priceAluminumCombo.getItems().clear();
        aluminuimContity.setText("");
        priceAlumnuimShow.setText(" 00 DH");
    }



    public void ajouterAccessoireToCommand(MouseEvent mouseEvent) {

        ArticleCommandEntity accessoireArticle = new ArticleCommandEntity();

        accessoireArticle.setArticle( accessoireProduct.getSelectionModel().getSelectedItem() );
        accessoireArticle.setName( accessoireProduct.getSelectionModel().getSelectedItem().getName() +" " + aluminuimLabel.getText()  );
        accessoireArticle.setPrice( accessoirePrice.getSelectionModel().getSelectedItem().getPrice() );
        accessoireArticle.setQuantity( Float.valueOf( accessoireQuentity.getText() ) );


        list.add( accessoireArticle );
        this.loadDataTable();
        accessoireProduct.getSelectionModel().select(-1); ;
        accessoireLabel.setText(""); ;
        accessoirePrice.getItems().clear(); ;
        accessoireQuentity.setText(""); ;
        accessoireTotal.setText(" 00 DH"); ;

    }

    public void ajouterGlassToCommand(MouseEvent mouseEvent) {

        ArticleCommandEntity glassArticle = new ArticleCommandEntity();

        glassArticle.setArticle( glassProduct.getSelectionModel().getSelectedItem() );
        glassArticle.setName( glassProduct.getSelectionModel().getSelectedItem().getName() +" " + glassLabel.getText()  );
        glassArticle.setPrice( glassPrice.getSelectionModel().getSelectedItem().getPrice() );
        glassArticle.setQuantity( Float.valueOf( glassQuentity.getText() ) );


        list.add( glassArticle );
        this.loadDataTable();
        glassProduct.getSelectionModel().select(-1); ;
        glassLabel.setText(""); ;
        glassPrice.getItems().clear(); ;
        glassQuentity.setText(""); ;
        glassTotal.setText(" 00 DH"); ;
    }


    void loadDataTable(){

        totalePriceCommand.setText( "00,00 DH" );
        float total = getTotal(); // list.stream().map( n -> n.getPrice()* n.getQuantity() ).collect( Collectors.toList() ).stream().reduce( 0f ,  ( subtotal , element ) -> subtotal + element );
        totalePriceCommand.setText( total + " DH" );


        observableArticleCommand.clear();
        observableArticleCommand.addAll( list );

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

        tableProductsOfCommand.setItems( FXCollections.observableArrayList(list) );
        tableProductsOfCommand.setItems( this.observableArticleCommand );

    }

    private Float getTotal(){
        float total = list.stream()
                          .map( n -> n.getPrice()* n.getQuantity() )
                          .collect( Collectors.toList() )
                          .stream()
                          .reduce( 0f ,  ( subtotal , element ) -> subtotal + element );
        System.out.println( total );
        return total;
    }
}
