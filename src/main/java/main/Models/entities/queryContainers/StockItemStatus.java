package main.Models.entities.queryContainers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.Models.entities.ArticleEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StockItemStatus {

    private ArticleEntity article;
    private Double inProducts  ;
    private Double outProducts ;


}
