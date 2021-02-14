package main.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import main.Models.entities.queryContainers.MoneyStatus;
import main.Models.entities.queryContainers.TurnoverByMonth;
import main.Services.StockService;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

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


    @FXML LineChart turnoverChart ;
    @FXML CategoryAxis chartX ;
    @FXML NumberAxis chartY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadAccountingLabels();

        List<TurnoverByMonth> turnoverByMonths = stockService.getTurnoverByMonth();

        XYChart.Series series = new XYChart.Series();
        series.setName("Chiffre d'affaire par mois");
        chartY.setLabel("Montant en DH");
        chartX.setLabel("les Mois");

        for( TurnoverByMonth turnoverByMonth : turnoverByMonths ){
            series.getData().add(new XYChart.Data( turnoverByMonth.getMonth() , turnoverByMonth.getTurnover()) );
        }

        turnoverChart.getData().addAll(series);

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
