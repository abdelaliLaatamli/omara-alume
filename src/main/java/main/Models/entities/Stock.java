package main.Models.entities;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int Id ;

    @Column
    protected Instant importedAt = Instant.now();


}
