package main.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import main.Models.entities.*;
import main.Services.AccessoryService;
import main.Services.AluminumService;
import main.Services.ClientServices;
import main.Services.GlassService;

import java.io.IOException;
import java.net.URL;
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

    @FXML ComboBox<AccessoryEntity> accessoireProduct;

    @FXML ComboBox<GlassEntity> glassProduct;


    @FXML ComboBox<PriceEntity> priceAluminumCombo ;


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
            //System.out.println(newValue);

            //System.out.println( aluminuimProduct.getSelectionModel().getSelectedItem() );
            priceAluminumCombo.setItems( FXCollections.observableArrayList( aluminuimProduct.getSelectionModel().getSelectedItem().getPrices()  ) );

        });

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
}
