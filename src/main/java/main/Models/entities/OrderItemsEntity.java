package main.Models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "order_items")
public class OrderItemsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id ;

    @Column
    protected String name ;

    @Column(nullable = false)
    protected float quantity;

    @Column(nullable = false)
    protected float price ;

    @Column
    protected int numberItems = 0 ;

    @ManyToOne
    protected OrderEntity order;

    @ManyToOne
    protected ArticleEntity article ;


}

