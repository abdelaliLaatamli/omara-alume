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
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import main.Models.dao.RepositoryDao;
import main.Models.entities.ClientEntity;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.*;

public class ClientController implements Initializable {

    RepositoryDao<ClientEntity> clientsDao ;

    @FXML
    TableView<ClientEntity> listClients ;

    @FXML
    TableColumn<ClientEntity , String> clientName ;

    @FXML
    TableColumn<ClientEntity , String> clientTele ;

    @FXML
    TableColumn<ClientEntity , String> clientCin ;

    @FXML
    TableColumn<ClientEntity , String> clientAddress ;

    @FXML
    TableColumn<ClientEntity , Instant> clientCreationDate ;

    @FXML
    TableColumn<ClientEntity , Integer> clientNumberCommand ;

    @FXML
    TableColumn<ClientEntity , Void> clientEdit;

    ObservableList<ClientEntity> observableClients = FXCollections.observableArrayList();



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        clientsDao = new RepositoryDao<ClientEntity>();

        this.loadData();

    }



    private void loadData(){

        List<ClientEntity> clients =  clientsDao.getAll("ClientEntity") ;

        observableClients.addAll( clients );

        clientName.setCellValueFactory(new PropertyValueFactory<>("name"));
        clientTele.setCellValueFactory(new PropertyValueFactory<>("tele"));
        clientCin.setCellValueFactory(new PropertyValueFactory<>("cin"));
        clientAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        clientCreationDate.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        clientNumberCommand.setCellValueFactory(cellDate -> new ReadOnlyObjectWrapper<>(cellDate.getValue().getCommands().size()) );



        Callback<TableColumn<ClientEntity, Void>, TableCell<ClientEntity, Void>> cellFactory = new Callback<TableColumn<ClientEntity, Void>, TableCell<ClientEntity, Void>>() {
            @Override
            public TableCell<ClientEntity, Void> call(final TableColumn<ClientEntity, Void> param) {
                final TableCell<ClientEntity, Void> cell = new TableCell<ClientEntity, Void>() {

                    private final Button btn = new Button("Action");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            ClientEntity data = getTableView().getItems().get(getIndex());
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

        clientEdit.setCellFactory(cellFactory);

        listClients.getColumns().add(clientEdit);

        listClients.setItems( observableClients );
    }



    public void goToHome(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/Main.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Aluminium et verre");
        main.JavaFxApplication.mainStage.show();
    }

}
