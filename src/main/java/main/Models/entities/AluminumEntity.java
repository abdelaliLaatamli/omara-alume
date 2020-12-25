package main.Models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.Models.enums.AluminumColors;
import main.Models.enums.MadeBy;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter


@Entity(name = "Aluminum")
@DiscriminatorValue("alu")
public class AluminumEntity extends ArticleEntity {

    @Column
    protected AluminumColors color ;

    @Column
    protected MadeBy madeBy ;


}
