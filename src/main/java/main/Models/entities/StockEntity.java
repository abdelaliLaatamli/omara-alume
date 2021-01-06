package main.Models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter


@Entity
@Table(name = "stock")
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int Id ;

    @Column
    protected String name;

    @Column
    protected Instant importedAt = Instant.now();


    @ManyToOne
    protected ProviderEntity provider ;

}
