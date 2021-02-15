package main.Models.entities.queryContainers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductEnter {

    private String productName;
    private double priceOfBuy;
    private double quantity ;
    private Instant dateImportation;
    private String factureLabel;
    private String articleName ;

}
