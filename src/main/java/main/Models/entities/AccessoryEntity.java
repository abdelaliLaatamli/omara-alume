package main.Models.entities;

import lombok.Getter;
import lombok.Setter;
import main.Models.enums.AccessoryColor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;


@Getter
@Setter

@Entity(name = "accessory")
@DiscriminatorValue("acc")
public class AccessoryEntity extends ArticleEntity {

    @Column
    protected AccessoryColor color ;

}
