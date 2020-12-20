package main.Models.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "Glass")
@DiscriminatorValue("gls")
public class GlassEntity extends ArticleEntity {

    @Column
    protected String type ;

    // سماكة / كثافة
    @Column
    protected String thicknessType ;



}
