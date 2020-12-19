package Models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "articles_command")
public class ArticleCommandEntity {

    @Id
    @GeneratedValue
    protected int id ;


    @Column
    protected float numberOfArticle;

    @ManyToOne
    protected ArticleEntity article ;

    @ManyToOne
    protected CommandEntity command ;



}
