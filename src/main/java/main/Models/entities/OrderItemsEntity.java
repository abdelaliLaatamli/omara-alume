package main.Models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "order_items")
public class OrderItemsEntity {

    @Id
    @GeneratedValue
    protected int id ;

    @Column
    protected String name ;

    @Column
    protected float quantity;

    @Column
    protected float price ;

    @ManyToOne
    protected OrderEntity command ;

    @ManyToOne
    protected ArticleEntity article ;

    @ManyToOne
    protected PriceEntity priceOfArticle ;

}

