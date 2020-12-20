package main.Models.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = "accessorie")
@DiscriminatorValue("acc")
public class AccessorieEntity extends ArticleEntity {


}
