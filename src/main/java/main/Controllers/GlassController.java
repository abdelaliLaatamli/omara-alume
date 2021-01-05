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
import main.Models.entities.AluminumEntity;
import main.Models.entities.GlassEntity;
import main.Models.entities.PriceEntity;
import main.Models.enums.GlassColor;
import main.Models.enums.ThicknessType;
import main.Services.GlassService;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class GlassController implements Initializable {

    GlassService glassService = new GlassService();

    @FXML TableView<GlassEntity> tableViewOfGlass ;
    @FXML TableColumn<GlassEntity,String> glassName ;
    @FXML TableColumn<GlassEntity,String> glassColor ;
    @FXML TableColumn<GlassEntity,String> glassThicknessType;
    @FXML TableColumn<GlassEntity,String> glassCreationDate ;
    @FXML TableColumn<GlassEntity,Float> glassSellingPrice ;
    @FXML TableColumn glassEdit ;

    ObservableList<GlassEntity> observableEntities = FXCollections.observableArrayList();

    @FXML TextField glassIdForm;
    @FXML TextField glassNameForm;
    @FXML ComboBox<GlassColor> glassColorForm;
    @FXML ComboBox<ThicknessType> glassThicknessTypeForm;
    @FXML TextField glassSellPriceForm;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        glassColorForm.setItems(FXCollections.observableArrayList( GlassColor.values() ));
        glassColorForm.getSelectionModel().selectFirst();

        glassThicknessTypeForm.setItems( FXCollections.observableArrayList( ThicknessType.values() ) );
        glassThicknessTypeForm.getSelectionModel().selectFirst();

        loadData();

    }

    private void loadData() {


        this.tableViewOfGlass.getItems().clear();
        this.tableViewOfGlass.refresh();

        List<GlassEntity> listGlass = glassService.getAllGlassProducts();

        observableEntities.addAll( listGlass );

        glassName.setCellValueFactory(new PropertyValueFactory<>("name"));
        glassColor.setCellValueFactory( new PropertyValueFactory<>("color") );
        glassThicknessType.setCellValueFactory( new PropertyValueFactory<>("thicknessType") );
        glassCreationDate.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper(
                DateTimeFormatter.ofPattern( "dd/MM/yyyy" ).withZone(ZoneId.systemDefault()).format(cellData.getValue().getCreatedAt())
        ));
        glassSellingPrice.setCellValueFactory(cellDate -> new ReadOnlyObjectWrapper(
                ( cellDate.getValue().getPrices().size() > 0 ) ?
                        cellDate.getValue().getPrices().stream().findFirst().get().getPrice() :
                        cellDate.getValue().getPrices().size()
        ));

        Callback<TableColumn<GlassEntity, Void>, TableCell<GlassEntity, Void>> cellFactory = new Callback<TableColumn<GlassEntity, Void>, TableCell<GlassEntity, Void>>() {
            @Override
            public TableCell<GlassEntity, Void> call(final TableColumn<GlassEntity, Void> param) {
                final TableCell<GlassEntity, Void> cell = new TableCell<GlassEntity, Void>() {

                    private final Button btn = new Button("Edit");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            GlassEntity data = getTableView().getItems().get(getIndex());
                            System.out.println("selectedData: " + data.getId());

                            glassIdForm.setText( String.valueOf( data.getId() ) );
                            glassNameForm.setText( data.getName() );


                            glassColorForm.getSelectionModel().select( data.getColor() );
                            glassThicknessTypeForm.getSelectionModel().select( data.getThicknessType() );
                            glassSellPriceForm.setText( String.valueOf( data.getPrices().stream().findFirst().get().getPrice() ) );


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

        glassEdit.setCellFactory(cellFactory);
        tableViewOfGlass.setItems( FXCollections.observableArrayList(listGlass) );
        tableViewOfGlass.setItems( observableEntities );


    }


    public void goBack(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ProductsView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" Products -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();
    }

    public void goHome(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/MainView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Home -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();
    }

    public void saveGlassForm(MouseEvent mouseEvent) {

        if( glassIdForm.getText() == null || glassIdForm.getText().equals("") ) {
            this.AddGlass();
        }else{
            updateGlass();
        }

        loadData();

        glassIdForm.setText("");
        glassNameForm.setText("");
        glassColorForm.getSelectionModel().selectFirst();
        glassSellPriceForm.setText("");

    }

    private void updateGlass() {
        GlassEntity glassEntity = glassService.getGlass( Integer.valueOf( glassIdForm.getText() ) );

        glassEntity.setName(glassNameForm.getText());


        glassEntity.setColor( glassColorForm.getSelectionModel().getSelectedItem());
        glassEntity.setThicknessType( glassThicknessTypeForm.getSelectionModel().getSelectedItem() );

        if(!glassSellPriceForm.getText().equals(""))
            glassEntity
                    .getPrices()
                    .stream()
                    .filter( price -> price.getName().equals("default") )
                    .findFirst()
                    .get()
                    .setPrice( Float.valueOf( glassSellPriceForm.getText()) );

        boolean updated = glassService.updateProduct( glassEntity );


        if( updated ){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Réussite de la mise à jour");
            alert.setHeaderText("le produit est mise à jour");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error D'ajouter");
            alert.setHeaderText("Oups, il y a eu une erreur!");
            alert.showAndWait();
        }
    }

    private void AddGlass() {

        GlassEntity glassEntity = new GlassEntity();
        glassEntity.setName(glassNameForm.getText());
        glassEntity.setColor( glassColorForm.getSelectionModel().getSelectedItem());
        glassEntity.setThicknessType( glassThicknessTypeForm.getSelectionModel().getSelectedItem() );

        PriceEntity defaultPrice  = new PriceEntity();
        defaultPrice.setName( "default" );
        defaultPrice.setPrice( (!glassSellPriceForm.getText().equals("")) ? Float.valueOf( glassSellPriceForm.getText()) : 0f );
        glassEntity.getPrices().add( defaultPrice );


        boolean added = glassService.addProduct( glassEntity , defaultPrice);

        System.out.println( added );

        if( added ){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("l'ajout de produit réussi");
            alert.setHeaderText("le produit est bien ajouté");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error D'ajouter");
            alert.setHeaderText("Oups, il y a eu une erreur!");
            alert.showAndWait();
        }
    }


}
