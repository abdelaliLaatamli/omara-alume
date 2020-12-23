package main.Models.entities;

import main.Models.enums.AluminumColors;
import main.Models.enums.MadeBy;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "Aluminum")
@DiscriminatorValue("alu")
public class AluminumEntity extends ArticleEntity {

    @Column
    protected AluminumColors color ;

    @Column
    protected MadeBy madeBy ;

}
