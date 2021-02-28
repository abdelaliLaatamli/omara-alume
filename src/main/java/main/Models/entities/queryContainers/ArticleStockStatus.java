package main.Models.entities.queryContainers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.Models.entities.StockEntity;
import main.Models.entities.StockItemsEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ArticleStockStatus {

    private StockItemsEntity stockItems;
    private StockEntity stock;
    private int numberItems;

}
