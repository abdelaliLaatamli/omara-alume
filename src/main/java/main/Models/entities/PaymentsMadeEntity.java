package main.Models.entities;

import javax.persistence.*;
import java.time.Instant;

//Paiements Effectués
@Entity
@Table(name = "payements_made")
public class PaymentsMadeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id ;

    @Column
    protected float amountPaid ;

    @Column
    protected Instant paymentDate = Instant.now();

    @ManyToOne
    protected CommandEntity command;

}
