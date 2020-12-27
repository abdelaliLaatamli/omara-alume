package main.Controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import main.Models.entities.ClientEntity;
import main.Services.ClientServices;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class CommandGeneratorController implements Initializable {


    private ClientServices clientServices = new ClientServices();


    @FXML ComboBox<String> clientNameForm ;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        loadData();
    }

    void loadData(){

        List<ClientEntity> clients = clientServices.getAll();
        clientNameForm.setItems(FXCollections.observableArrayList( clients.stream().map( c -> c.getName()  ).collect(Collectors.toList()) ));
        AutoCompleteBox aa = new AutoCompleteBox(clientNameForm);

    }



    public void goBack(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/CommandsView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" Commands -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }

    public void goHome(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/MainView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Home -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }




}
