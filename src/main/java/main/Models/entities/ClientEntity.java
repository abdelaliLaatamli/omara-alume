package main.Models.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

@Entity
@Table(name = "clients")
public class ClientEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    protected int id ;

    @Column
    protected String name;

    @Column(nullable = true)
    protected String tele ;

    @Column(nullable = true)
    protected String cin ;

    @Column(nullable = true)
    protected String address;

    @Column(nullable = true)
    protected double oldCredit = 0;

    @Column
    protected Instant createdAt = Instant.now() ;

    @OneToMany
    @JoinColumn(name = "client_id")
    protected Set<PriceEntity> prices;


    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn( name = "client_id")
    protected Set<OrderEntity> orders;

    @Override
    public String toString(){
        return this.getName();
    }


}
