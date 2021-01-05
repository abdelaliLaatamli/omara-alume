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

    @ManyToOne
    protected StockEntity stock;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "import_items_id")
    protected Set<ArticleEntity> articles = new HashSet<>();

}
