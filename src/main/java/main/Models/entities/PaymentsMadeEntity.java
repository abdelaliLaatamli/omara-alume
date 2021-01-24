package main.Models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.Models.enums.PayementMethod;

import javax.persistence.*;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

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
    protected PayementMethod payementMethod = PayementMethod.ESPECE ;

    @Column
    protected Instant paymentDate = Instant.now();

    @ManyToOne
    protected OrderEntity order;

}
