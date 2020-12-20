package main.Models.entities;

import javax.persistence.*;


@Entity(name ="prices")
public class PriceEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    protected int id ;

    @Column
    protected String name ;

    @Column
    protected float price ;

    @ManyToOne
    protected ClientEntity client;

    @ManyToOne
    protected ArticleEntity article;

}
