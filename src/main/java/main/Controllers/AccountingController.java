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

    @FXML Label totalSellingLbl;
    @FXML Label totalBuyingLbl;
    @FXML Label totalMonthPaymentLbl;
    @FXML Label totalCreditPaymentLbl;
    @FXML Label lblBaseBuyMonth ;

    @FXML Label globalPaymentLbl;
    @FXML Label globalCreditPaymentLbl;
    @FXML Label globalSellingLbl;
    @FXML Label globalBuyingLbl;
    @FXML Label lblBaseBuyYear;


    @FXML LineChart<CategoryAxis , NumberAxis> turnoverChart ;
    @FXML CategoryAxis chartX ;
    @FXML NumberAxis chartY;


//    ----------------------- TAB Entree -----------------------------------

    @FXML ComboBox <MonthsOfYear> comboChooseMonth;
    @FXML TextField searchByNameTextField;

    @FXML TableView<ProductEnter> tableEntreeArticle ;
    @FXML TableColumn<ProductEnter ,String> productNameColumn;
    @FXML TableColumn<ProductEnter ,String> priceOfBuyingColumn;
    @FXML TableColumn<ProductEnter ,String> productQuantityColumn;
    @FXML TableColumn<ProductEnter ,String> dateOfImportColumn;
    @FXML TableColumn<ProductEnter ,String> lblOrderColumn;
    @FXML TableColumn<ProductEnter ,String> providerColumn;
    @FXML TableColumn<ProductEnter ,String> productTypeColumn;


    ObservableList<ProductEnter> observableProductEnter = FXCollections.observableArrayList();

    List<ProductEnter> productsEnter = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadAccountingLabels();
        rempplaireChart();

        fillEntreeTab();

    }

    private void fillEntreeTab() {
        productsEnter = stockService.getProductsEnter( LocalDate.now().getMonthValue() );

        fillTableEntrees( productsEnter );

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

            productsEnter = stockService.getProductsEnter( newValue.getValue() );
            fillTableEntrees( productsEnter );

        } );

        searchByNameTextField.textProperty().addListener( (options, oldValue, newValue) -> {

            List<ProductEnter> filteredProductsEnter = productsEnter.stream()
                                .filter( e -> e.getArticle().getName().toLowerCase(Locale.ROOT).contains( newValue.toLowerCase(Locale.ROOT) ))
                                .collect(Collectors.toList());

            fillTableEntrees( filteredProductsEnter );

        });
    }

    private void fillTableEntrees( List<ProductEnter> listProductsEnter    ) {

        observableProductEnter.clear();
        observableProductEnter.addAll( listProductsEnter );

        productNameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
                cellData.getValue().getArticle().getName()
        ));

        productTypeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
                cellData.getValue().getArticle().getType()
        ));

//        priceOfBuyingClmn.setCellValueFactory( new PropertyValueFactory<>("priceOfBuy") );
        priceOfBuyingColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper( String.format( "%.3f" , cellData.getValue().getPriceOfBuy() ) ) );
        productQuantityColumn.setCellValueFactory( new PropertyValueFactory<>("quantity") );

        dateOfImportColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
                DateTimeFormatter.ofPattern( "dd/MM/yyyy" ).withZone(ZoneId.systemDefault()).format(cellData.getValue().getDateImportation())
        ));

        lblOrderColumn.setCellValueFactory( new PropertyValueFactory<>("factureLabel") );

        providerColumn.setCellValueFactory( new PropertyValueFactory<>("providerName") );


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

//        lblBaseBuyMonth
//        lblBaseBuyYear

        MoneyStatus moneyStatus = stockService.getMoneyStatus();
        totalSellingLbl.setText( moneyStatus.getSalesOfMonth() + " DH " );
        totalBuyingLbl.setText( moneyStatus.getPurchaseOfMonth() + " DH ");
        totalMonthPaymentLbl.setText( moneyStatus.getPaymentsOfMonth() + " DH " );
        totalCreditPaymentLbl.setText( moneyStatus.getCreditOfMonth() + " DH " );
        lblBaseBuyMonth.setText( moneyStatus.getTotalBuyPriceBase() + " DH "  );

        globalPaymentLbl.setText( moneyStatus.getPaymentsGlobal() + " DH " );
        globalCreditPaymentLbl.setText( moneyStatus.getCreditGlobal() + " DH " );
        globalSellingLbl.setText( moneyStatus.getSalesGlobal() + " DH ");
        globalBuyingLbl.setText( moneyStatus.getPurchaseGlobal() + " DH " );
        lblBaseBuyYear.setText( moneyStatus.getTotalBuyGlobalBase() + " DH "  );

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
