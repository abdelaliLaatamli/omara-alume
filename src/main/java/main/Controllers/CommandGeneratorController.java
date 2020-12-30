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


    private ClientServices clientServices = new ClientServices();
    private AccessoryService accessoryService = new AccessoryService();
    private AluminumService aluminumService = new AluminumService();
    private GlassService glassService = new GlassService();


    @FXML ComboBox<String> clientNameForm ;

    @FXML ComboBox<AluminumEntity> aluminuimProduct;

    @FXML TextField aluminuimLabel;

    @FXML ComboBox<PriceEntity> priceAluminumCombo ;

    @FXML TextField aluminuimContity;


    @FXML Label priceAlumnuimShow;





    // --------------------

    @FXML ComboBox<AccessoryEntity> accessoireProduct;

    @FXML ComboBox<GlassEntity> glassProduct;


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


        accessoireProduct.setItems( FXCollections.observableArrayList(accessoryService.getAllAccessoryProducts()) );
        aluminuimProduct.setItems( FXCollections.observableArrayList( aluminumService.getAllAlumunuimProducts() ) );
        glassProduct.setItems( FXCollections.observableArrayList( glassService.getAllGlassProducts() ) );


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
            //System.out.println( priceAluminumCombo.getSelectionModel().getSelectedIndex() );

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

        ArticleCommandEntity aa = new ArticleCommandEntity();

        aa.setArticle( aluminuimProduct.getSelectionModel().getSelectedItem() );
        aa.setName( aluminuimProduct.getSelectionModel().getSelectedItem().getName() +" " + aluminuimLabel.getText()  );
        aa.setPrice( priceAluminumCombo.getSelectionModel().getSelectedItem().getPrice() );
        aa.setQuantity( Float.valueOf( aluminuimContity.getText() ) );




        list.add( aa );
        this.loadDataTable();

        aluminuimProduct.getSelectionModel().select(-1);
        //aluminuimProduct.getSelectionModel().select(-1);
        aluminuimLabel.setText("");
        priceAluminumCombo.getItems().clear();
        aluminuimContity.setText("");
    }

    void loadDataTable(){
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

        /*
        editProductOfCommand.setCellValueFactory(   cellDate -> new ReadOnlyObjectWrapper(cellDate.getValue().getArticle()) );
        deleteProductOfCommand.setCellValueFactory( cellDate -> new ReadOnlyObjectWrapper(cellDate.getValue().getArticle()) );
    */
        editProductOfCommand.setCellFactory( editCellFactory );
        deleteProductOfCommand.setCellFactory(deleteCellFactory );

        tableProductsOfCommand.setItems( FXCollections.observableArrayList(list) );
        tableProductsOfCommand.setItems( this.observableArticleCommand );

    }
}
