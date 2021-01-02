package main.Controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import main.Models.entities.ClientEntity;
import main.Models.entities.CommandEntity;
import main.Services.CommandService;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


public class ListCommandsController implements Initializable {

    CommandService commandService = new CommandService();

    @FXML TableView<CommandEntity> listCommandTable;
    @FXML TableColumn<CommandEntity , String > referenceCommand;
    @FXML TableColumn<CommandEntity , String > dateCommand;
    @FXML TableColumn<CommandEntity , String > clientCommand;
    @FXML TableColumn<CommandEntity , String > paymentStatusCommand ;
    @FXML TableColumn<CommandEntity , String > paymentsMadesOfCommand;
    @FXML TableColumn<CommandEntity , String > totalPriceOfCommand;
    @FXML TableColumn<CommandEntity , String > editCommand ;
    @FXML TableColumn<CommandEntity , String > peuseCommand;

    ObservableList<CommandEntity> observableCommand = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.loadData();
    }


    public void loadData(){

        List<CommandEntity> commands = commandService.getAllCommands();
        observableCommand.clear();
        observableCommand.addAll( commands );
       // new ReadOnlyObjectWrapper(
        referenceCommand.setCellValueFactory( new PropertyValueFactory<>("id") );
        dateCommand.setCellValueFactory( new PropertyValueFactory<>("commandDate") );
        clientCommand.setCellValueFactory( new PropertyValueFactory<>("client") );
        paymentStatusCommand.setCellValueFactory( new PropertyValueFactory<>("paymentStatus") );
        paymentsMadesOfCommand.setCellValueFactory( new PropertyValueFactory<>("id") );
        totalPriceOfCommand.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper( this.sumTotal( cellData.getValue() )));
        editCommand.setCellValueFactory( new PropertyValueFactory<>("id") );
        peuseCommand.setCellValueFactory( new PropertyValueFactory<>("id") );

        listCommandTable.setItems( observableCommand );



    }

    private Float sumTotal(CommandEntity client){
        float total = client.getArticleCommands()
                .stream()
                .map( n -> n.getPrice() * n.getQuantity() )
                .collect(Collectors.toList())
                .stream()
                .reduce( 0f , ( subTotal , currentPrice ) -> subTotal + currentPrice );

        return total;
    }

    public void goBack(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/MainView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Home -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }


    public void goToCommandGenerator(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/CommandGeneratorView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Command Generator -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();
    }


}
