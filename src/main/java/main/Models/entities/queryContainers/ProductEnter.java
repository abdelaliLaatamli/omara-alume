package main.Models.entities.queryContainers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.Models.entities.ArticleEntity;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductEnter {

    private ArticleEntity article;
    private double priceOfBuy;
    private double quantity ;
    private Instant dateImportation;
    private String factureLabel;
    private String providerName ;

}
