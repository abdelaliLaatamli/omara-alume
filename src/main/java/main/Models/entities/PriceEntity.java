package main.Models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter


@Entity(name ="prices")
public class PriceEntity implements Comparable<PriceEntity> {

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


    @Override
    public String toString(){
        return this.name + " - " + this.price + "DH" ;
    }

    @Override
    public int compareTo(@NotNull PriceEntity o) {
        return ( id > o.id ) ? 1 : -1 ;
    }
}
