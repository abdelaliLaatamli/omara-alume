package main.Models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity(name = "articles")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "article_type")
public abstract class ArticleEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id ;

    @Column
    protected String name ;

    @Column
    protected Float priceOfBuy;

    @Column
    protected float quantity;

    @Column
    protected Instant createdAt = Instant.now() ;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id")
    protected Set<PriceEntity> prices = new HashSet<>();

    @Override
    public String toString(){
        return this.name;
    }


}
