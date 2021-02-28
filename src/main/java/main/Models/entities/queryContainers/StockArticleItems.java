package main.Models.entities.queryContainers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.Models.entities.StockEntity;
import main.Models.entities.StockItemsEntity;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class StockArticleItems implements Comparable<StockArticleItems>{

    private StockEntity stock;
    private StockItemsEntity stockItems;
    private Double sold;

    @Override
    public String toString() {

        return stock.getName() + " - " + (int) ( stockItems.getQuantity() -  sold ) ;
    }

    @Override
    public int compareTo(@NotNull StockArticleItems o) {

        return ( this.stock.getImportedAt().compareTo( o.getStock().getImportedAt() ) < 0 )  ? 1  : -1;

    }
}
