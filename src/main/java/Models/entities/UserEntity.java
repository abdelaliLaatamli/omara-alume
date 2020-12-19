package Models.entities;


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
@Table(name="users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    protected int id;

    @Column(name="username")
    protected String username;

    @Column(name = "email", unique = true)
    protected String email ;

    @Column(name = "password")
    protected String password ;

    @Column
    protected Instant createdAt = Instant.now();

}
