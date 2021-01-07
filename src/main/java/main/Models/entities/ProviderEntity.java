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

@Entity(name="providers")
public class ProviderEntity {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    protected int id ;


    @Column
    protected String name ;

    @Column(nullable = true)
    protected String identify;

    @Column(nullable = true)
    protected String tele;

    @Column(nullable = true)
    protected String address;

    @Column
    protected Instant createAd = Instant.now();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id")
    protected Set<StockEntity> stocks = new HashSet<>();


    @Override
    public String toString() {
        return name ;
    }
}
