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
import main.Models.enums.StockSearchProduct;
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

    @FXML ComboBox<StockSearchProduct> comboSearchByTypeIn;

    ObservableList<ProductEnter> observableProductEnter = FXCollections.observableArrayList();
    List<ProductEnter> productsEnter = new ArrayList<>();

//    ----------------------- TAB Out -----------------------------------

    @FXML ComboBox <MonthsOfYear> comboChooseMonthOut;
    @FXML TextField searchByNameTextFieldOut;

    @FXML TableView<ProductEnter> tblOutArticle ;
    @FXML TableColumn<ProductEnter ,String> productNameColumnOut;
    @FXML TableColumn<ProductEnter ,String> priceOfSellingColumn;
    @FXML TableColumn<ProductEnter ,String> productQuantityColumnOut;
    @FXML TableColumn<ProductEnter ,String> dateOfOrderColumn;
    @FXML TableColumn<ProductEnter ,String> productTypeColumnOut;
    @FXML TableColumn<ProductEnter ,String> lblOrderColumnOut;
    @FXML TableColumn<ProductEnter ,String> clientColumn;

    @FXML ComboBox<StockSearchProduct> comboSearchByTypeOut;

    ObservableList<ProductEnter> observableProductOut = FXCollections.observableArrayList();

    List<ProductEnter> productsOut = new ArrayList<>();

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadAccountingLabels();
        rempplaireChart();
        fillInTab();
        fillOutTab();


    }

    private void fillOutTab() {

        productsOut = stockService.getProductsOut( LocalDate.now().getMonthValue() );

        fillTableOut(productsOut);

        comboChooseMonthOut.setItems( FXCollections.observableArrayList( MonthsOfYear.values() ) );
        comboChooseMonthOut.getSelectionModel().select( months.get( LocalDate.now().getMonthValue() ) );
        comboChooseMonthOut.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {

            productsOut = stockService.getProductsOut( newValue.getValue() );
            fillTableOut( productsOut );

        } );

        searchByNameTextFieldOut.textProperty().addListener( (options, oldValue, newValue) -> {

            List<ProductEnter> filteredProductsEnter = productsOut.stream()
                    .filter( e -> e.getArticle().getName().toLowerCase(Locale.ROOT).contains( newValue.toLowerCase(Locale.ROOT) ))
                    .collect(Collectors.toList());

            fillTableOut( filteredProductsEnter );

        });

        comboSearchByTypeOut.setItems( FXCollections.observableArrayList( StockSearchProduct.values() ) );
        comboSearchByTypeOut.getSelectionModel().selectFirst();

        comboSearchByTypeOut.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {

            List<ProductEnter> filteredProductsEnter = productsOut.stream()
                    .filter( item -> newValue == StockSearchProduct.ALL || item.getArticle().getType() == newValue )
                    .collect(Collectors.toList());

            fillTableOut( filteredProductsEnter );
        });

    }

    private void fillTableOut( List<ProductEnter> listProductsOut ) {
        observableProductOut.clear();
        observableProductOut.addAll( listProductsOut );

        productNameColumnOut.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
                cellData.getValue().getArticle().getName()
        ));

        productTypeColumnOut.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
                cellData.getValue().getArticle().getType()
        ));

        priceOfSellingColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper( String.format( "%.2f" , cellData.getValue().getPriceOfBuy() ) ) );
        productQuantityColumnOut.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper( String.format( "%.2f" , cellData.getValue().getQuantity() ) ) );
//        productQuantityColumnOut.setCellValueFactory( new PropertyValueFactory<>("quantity") );

        dateOfOrderColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
                DateTimeFormatter.ofPattern( "dd/MM/yyyy" ).withZone(ZoneId.systemDefault()).format(cellData.getValue().getDateImportation())
        ));

        lblOrderColumnOut.setCellValueFactory( cellData -> new ReadOnlyObjectWrapper(
                String.format("REF%08d", Integer.valueOf( cellData.getValue().getFactureLabel() ) )
        ) );

        clientColumn.setCellValueFactory( new PropertyValueFactory<>("providerName") );

        tblOutArticle.setItems(observableProductOut);
    }

    private void fillInTab() {

        productsEnter = stockService.getProductsEnter( LocalDate.now().getMonthValue() );

        fillTableIn( productsEnter );

        comboChooseMonth.setItems( FXCollections.observableArrayList( MonthsOfYear.values() ) );
        comboChooseMonth.getSelectionModel().select( months.get( LocalDate.now().getMonthValue() ) );
        comboChooseMonth.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {

            productsEnter = stockService.getProductsEnter( newValue.getValue() );
            fillTableIn( productsEnter );

        } );

        searchByNameTextField.textProperty().addListener( (options, oldValue, newValue) -> {

            List<ProductEnter> filteredProductsEnter = productsEnter.stream()
                                .filter( e -> e.getArticle().getName().toLowerCase(Locale.ROOT).contains( newValue.toLowerCase(Locale.ROOT) ))
                                .collect(Collectors.toList());

            fillTableIn( filteredProductsEnter );

        });

        comboSearchByTypeIn.setItems( FXCollections.observableArrayList( StockSearchProduct.values() ) );
        comboSearchByTypeIn.getSelectionModel().selectFirst();

        comboSearchByTypeIn.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {

            List<ProductEnter> filteredProductsEnter = productsEnter.stream()
                    .filter( item -> newValue == StockSearchProduct.ALL || item.getArticle().getType() == newValue )
                    .collect(Collectors.toList());

            fillTableIn( filteredProductsEnter );
        });

    }

    private void fillTableIn( List<ProductEnter> listProductsEnter ) {

        observableProductEnter.clear();
        observableProductEnter.addAll( listProductsEnter );

        productNameColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
                cellData.getValue().getArticle().getName()
        ));

        productTypeColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper(
                cellData.getValue().getArticle().getType()
        ));


        priceOfBuyingColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper( String.format( "%.3f" , cellData.getValue().getPriceOfBuy() ) ) );
        productQuantityColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper( String.format( "%.3f" , cellData.getValue().getQuantity() ) ) );
//        productQuantityColumn.setCellValueFactory( new PropertyValueFactory<>("quantity") );

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
