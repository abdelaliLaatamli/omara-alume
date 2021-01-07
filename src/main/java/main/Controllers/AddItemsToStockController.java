package main.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import main.Models.entities.*;
import main.Models.enums.ProductsType;
import main.Services.AccessoryService;
import main.Services.AluminumService;
import main.Services.GlassService;
import main.Services.ProviderService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

public class AddItemsToStockController implements Initializable {

    private final ProviderService providerService = new ProviderService();
    private final AccessoryService accessoryService = new AccessoryService();
    private final AluminumService aluminumService = new AluminumService();
    private final GlassService glassService = new GlassService();


    @FXML ComboBox<ProviderEntity> sProvider;
    @FXML TextField sLable;
    @FXML ComboBox<ProductsType> sProduitType;
    @FXML ComboBox<ArticleEntity> sProduit;
    @FXML TextField sQuentity;
    @FXML TextField sPriceOfBuy;


    StockEntity stockEntity ;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        stockEntity = new StockEntity();

        sProvider.setItems( FXCollections.observableArrayList( providerService.getAll() ) );
        sProduitType.setItems( FXCollections.observableArrayList( ProductsType.values() ) );


        sProduitType.getSelectionModel().selectedIndexProperty().addListener( ( (options, oldValue, newValue) -> {

            switch ( (int) newValue ){
                case 0 :
                    sProduit.getItems().clear();
                    sProduit.setItems( FXCollections.observableArrayList( aluminumService.getAllAlumunuimProducts() ) );
                    break;

                case 1 :
                    sProduit.getItems().clear();
                    sProduit.setItems( FXCollections.observableArrayList( accessoryService.getAllAccessoryProducts() ) );
                    break;

                case 2:
                    sProduit.getItems().clear();
                    sProduit.setItems( FXCollections.observableArrayList( glassService.getAllGlassProducts() ) );
                    break;

            }


        } )  );

        sProduitType.getSelectionModel().selectFirst();

    }

    public void goBack(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/StockView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" Stock -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();
    }

    public void saveArticleInStock(MouseEvent mouseEvent) {

        StockItemsEntity stockItemsEntity = new StockItemsEntity();

        stockItemsEntity.setArticle( sProduit.getSelectionModel().getSelectedItem());
        stockItemsEntity.setQuantity( Float.valueOf( sQuentity.getText()) );
        stockItemsEntity.setPriceOfBuy( Float.valueOf( sPriceOfBuy.getText() ) );
        stockEntity.getStockItems().add( stockItemsEntity );


        sProduitType.getSelectionModel().select(0);
        sProduit.getSelectionModel().select(-1);
        sQuentity.setText( "1" );
        sPriceOfBuy.setText( "0" );


    }

    public void saveStockOrder(MouseEvent mouseEvent) {
    }


}
