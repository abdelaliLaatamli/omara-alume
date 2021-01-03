package main.Models.entities;

import main.Models.enums.PaymentStatus;
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
@Setter
@Getter

@Entity
@Table(name = "commands")
public class CommandEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id ;

    @Column
    protected PaymentStatus paymentStatus;

    @Column
    protected Instant commandDate = Instant.now();

    @Column
    protected Boolean isLocked = false ;

    @Column
    protected Boolean isCanceled = false ;

    @ManyToOne
    protected ClientEntity client ;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "payment_made_id")
    protected Set<PaymentsMadeEntity> paymentsMades = new HashSet<>();

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "command_id")
    protected Set<ArticleCommandEntity> articleCommands = new HashSet<>() ;


}
