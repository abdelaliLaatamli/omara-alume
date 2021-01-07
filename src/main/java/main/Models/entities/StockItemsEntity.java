package main.Models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "stock_items")
public class StockItemsEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    protected int id ;

    @Column
    private float quantity;

    @Column float priceOfBuy;


    @ManyToOne
    protected StockEntity stock;

    @ManyToOne
    protected ArticleEntity article;


}
