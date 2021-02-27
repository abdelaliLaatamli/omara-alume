package main.Models.entities.queryContainers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.Models.entities.StockEntity;
import main.Models.entities.StockItemsEntity;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StockArticleItems {

    private StockEntity stock;
    private StockItemsEntity stockItemsEntity;

}
