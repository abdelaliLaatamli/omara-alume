package main.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class MainController {

    public void displayClientsPage(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ClientView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" Clients -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }

    public void displayProductsPage(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ProductsView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" Products -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();
    }

    public void displayOrdersPage(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ListOrdersView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" List Commands -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }

    public void displayAccountingPage(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/AccountingView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" Accounting -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }

    public void displayStockView(MouseEvent mouseEvent) throws IOException {

//        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ListStockView.fxml"));
//        main.JavaFxApplication.mainStage.setScene(new Scene(root));
//        main.JavaFxApplication.mainStage.setTitle(" Stock -- Aluminium et verre");
//        main.JavaFxApplication.mainStage.show();

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/StockManagementView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" Stock -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();

    }

    public void goToProvidersView(MouseEvent mouseEvent) throws IOException {

        Parent root = FXMLLoader.load(this.getClass().getResource("/main/views/ProviderView.fxml"));
        main.JavaFxApplication.mainStage.setScene(new Scene(root));
        main.JavaFxApplication.mainStage.setTitle(" Fournisseur -- Aluminium et verre");
        main.JavaFxApplication.mainStage.show();
    }
}
