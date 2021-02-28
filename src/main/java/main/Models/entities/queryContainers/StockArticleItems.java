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

        return stock.getName() + " " + (int) ( stockItems.getQuantity() -  sold ) ;
//        return "StockArticleItems{" +
//                "stock=" + stock +
//                ", stockItems=" + stockItems +
//                ", sold=" + sold +
//                '}';
    }

    @Override
    public int compareTo(@NotNull StockArticleItems o) {
//        if( this.stock.getImportedAt() > o.getStock().getImportedAt() )
        if( this.stock.getImportedAt().compareTo( o.getStock().getImportedAt() ) < 0 )
            return 1 ;
        return -1 ;

//        return 0;
    }
}
