package Models.entities;


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

    @Column
    protected Instant createdAt ;

    @OneToMany
    @JoinColumn(name = "cleint_id")
    protected Set<PriceEntity> prices;


    @OneToMany
    @JoinColumn( name = "client_id")
    protected Set<CommandEntity> commands;


}
