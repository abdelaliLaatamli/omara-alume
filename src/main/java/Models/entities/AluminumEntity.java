package Models.entities;

import Models.enums.AluminumColors;
import Models.enums.ContryType;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "Aluminum")
@DiscriminatorValue("alu")
public class AluminumEntity extends ArticleEntity {

    @Column
    protected AluminumColors color ;

    @Column
    protected ContryType contryType ;

}
