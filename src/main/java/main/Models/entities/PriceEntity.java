package main.Models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter


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

    @OneToMany
    @JoinColumn(name = "price_id")
    protected Set<ArticleCommandEntity> articleCommands ;

}
