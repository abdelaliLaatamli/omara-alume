package main.Models.entities;

import lombok.Getter;
import lombok.Setter;
import main.Models.enums.GlassColor;
import main.Models.enums.ThicknessType;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Setter
@Getter

@Entity(name = "glass")
@DiscriminatorValue("gls")
public class GlassEntity extends ArticleEntity {

//    @Column
//    protected String type ;

    // سماكة / كثافة
    @Column
    protected ThicknessType thicknessType ;

    @Column
    protected GlassColor color;

}
