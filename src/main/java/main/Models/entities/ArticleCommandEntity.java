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
@Table(name = "articles_command")
public class ArticleCommandEntity {

    @Id
    @GeneratedValue
    protected int id ;

    @Column String name ;

    @Column
    protected float quantity;

    @Column
    protected float price ;

    @ManyToOne
    protected CommandEntity command ;

    @ManyToOne
    protected ArticleEntity article ;

    @ManyToOne
    protected PriceEntity priceOfArticle ;

}

