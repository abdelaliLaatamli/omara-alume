package main.Models.entities.queryContainers;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MoneyStatus {

    private double salesOfMonth;
    private double purchaseOfMonth;
    private double salesGlobal;
    private double purchaseGlobal;
    private double paymentsOfMonth;
    private double creditOfMonth;
    private double paymentsGlobal;
    private double creditGlobal;

}
