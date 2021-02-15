package main.Controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import main.Models.entities.OrderEntity;
import main.Models.entities.queryContainers.MoneyStatus;
import main.Models.entities.queryContainers.ProductEnter;
import main.Models.entities.queryContainers.TurnoverByMonth;
import main.Models.enums.MonthsOfYear;
import main.Services.StockService;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountingController implements Initializable {

    StockService stockService = new StockService();

    @FXML Label currentMonthLbl;
    @FXML Label currentYearLbl ;

    @FXML Label totalVenteLbl;
    @FXML Label totalAchatLbl;
    @FXML Label totalMonthPayementLbl;
    @FXML Label totalCreaditPayementLabel;

    @FXML Label globalPayementLbl;
    @FXML Label globalCreaditPayementLbl;
    @FXML Label globalSellinglbl;
    @FXML Label globalBuyinglbl;


    @FXML LineChart<CategoryAxis , NumberAxis> turnoverChart ;
    @FXML CategoryAxis chartX ;
    @FXML NumberAxis chartY;


//    ----------------------- TAB Entree -----------------------------------

    @FXML ComboBox <MonthsOfYear> comboChooseMonth;

    @FXML TableView<ProductEnter> tableEntreeArticle ;
    @FXML TableColumn<ProductEnter ,String> procutNameClmn;
    @FXML TableColumn<ProductEnter ,String> priceOfBuyingClmn;
    @FXML TableColumn<ProductEnter ,String> productQuentityClmn;
    @FXML TableColumn<ProductEnter ,String> dateOfImporteClmn;
    @FXML TableColumn<ProductEnter ,String> lblCommandClmn ;
    @FXML TableColumn<ProductEnter ,String> providerClmn;

    ObservableList<ProductEnter> observableProductEnter = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadAccountingLabels();

        rempplaireChart();

        fillTableEntrees( LocalDate.now().getMonthValue() );

        Map<Integer, MonthsOfYear> months = Stream.of(new Object[][] {
                { 1 , MonthsOfYear.JANUARY },
                { 2 , MonthsOfYear.FEBRUARY },
                { 3 , MonthsOfYear.MARCH },
                { 4 , MonthsOfYear.APRIL },
                { 5 , MonthsOfYear.MAY },
                { 6 , MonthsOfYear.JUNE },
                { 7 , MonthsOfYear.JULY },
                { 8 , MonthsOfYear.AUGUST },
                { 9 , MonthsOfYear.SEPTEMBER },
                { 10 , MonthsOfYear.OCTOBER },
                { 11 , MonthsOfYear.NOVEMBER },
                { 12 , MonthsOfYear.DECEMBER }
        }).collect(Collectors.toMap(data -> (Integer) data[0], data -> (MonthsOfYear) data[1]));

        comboChooseMonth.setItems( FXCollections.observableArrayList( MonthsOfYear.values() ) );
        comboChooseMonth.getSelectionModel().select( months.get( LocalDate.now().getMonthValue() ) );
        comboChooseMonth.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
            // System.out.println( newValue.getValue() );
            fillTableEntrees( newValue.getValue() );
        } );

    }

    private void fillTableEntrees( int month  ) {

        List<ProductEnter> productsEnter = stockService.getProductsEnter( month );

        observableProductEnter.clear();
        observableProductEnter.addAll( productsEnter );

        procutNameClmn.setCellValueFactory( new PropertyValueFactory<>("productName") );
        priceOfBuyingClmn.setCellValueFactory( new PropertyValueFactory<>("priceOfBuy") );
        productQuentityClmn.setCellValueFactory( new PropertyValueFactory<>("quantity") );
        dateOfImporteClmn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
                DateTimeFormatter.ofPattern( "dd/MM/yyyy" ).withZone(ZoneId.systemDefault()).format(cellData.getValue().getDateImportation())
        ));
        lblCommandClmn.setCellValueFactory( new PropertyValueFactory<>("factureLabel") );

        providerClmn.setCellValueFactory( new PropertyValueFactory<>("articleName") );


        tableEntreeArticle.setItems(observableProductEnter);
    }

    private void rempplaireChart() {
        List<TurnoverByMonth> turnoverByMonths = stockService.getTurnoverByMonth();

        XYChart.Series seriesTurnover = new XYChart.Series();
        seriesTurnover.setName("Chiffre d'affaire par mois");

        XYChart.Series seriesCharges = new XYChart.Series();
        seriesCharges.setName("Les Charges par mois");

        chartY.setLabel("Montant en DH");
        chartX.setLabel("les Mois");

        for( TurnoverByMonth turnoverByMonth : turnoverByMonths ){

            seriesTurnover.getData().add(new XYChart.Data( turnoverByMonth.getMonth() , turnoverByMonth.getTurnover()) );
            seriesCharges.getData().add(new XYChart.Data( turnoverByMonth.getMonth() , turnoverByMonth.getCharge()) );

        }


        turnoverChart.getData().addAll(seriesTurnover , seriesCharges);


        for (Object entry : seriesTurnover.getData()) {
            XYChart.Data item = ( XYChart.Data)entry;
            Tooltip t = new Tooltip(  "Chiffre d'affaire De "  + item.getXValue().toString() + " : " +  item.getYValue().toString() + " DH ");
            t.setFont( new Font(16) );
            Tooltip.install(item.getNode(), t );
        }

        for (Object entry : seriesCharges.getData()) {
            XYChart.Data item = ( XYChart.Data)entry;
            Tooltip t = new Tooltip(  "Les Charges De "  + item.getXValue().toString() + " : " +  item.getYValue().toString() + " DH ");
            t.setFont( new Font(16) );
            Tooltip.install(item.getNode(), t );
        }
    }

    private void loadAccountingLabels() {

        MoneyStatus moneyStatus = stockService.getMoneyStatus();
        totalVenteLbl.setText( moneyStatus.getSalesOfMonth() + " DH " );
        totalAchatLbl.setText( moneyStatus.getPurchaseOfMonth() + " DH ");
        totalMonthPayementLbl.setText( moneyStatus.getPaymentsOfMonth() + " DH " );
        totalCreaditPayementLabel.setText( moneyStatus.getCreditOfMonth() + " DH " );

        globalPayementLbl.setText( moneyStatus.getPaymentsGlobal() + " DH " );
        globalCreaditPayementLbl.setText( moneyStatus.getCreditGlobal() + " DH " );
        globalSellinglbl.setText( moneyStatus.getSalesGlobal() + " DH ");
        globalBuyinglbl.setText( moneyStatus.getPurchaseGlobal() + " DH " );

        currentMonthLbl.setText(  DateTimeFormatter.ofPattern( "MM/yyyy" ).withZone(ZoneId.systemDefault()).format(  Instant.now() )) ;
        currentYearLbl.setText(  DateTimeFormatter.ofPattern( "yyyy" ).withZone(ZoneId.systemDefault()).format(  Instant.now() )) ;

    }


    public void goToHome(MouseEvent mouseEvent) throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/MainView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle("Home -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }



}
