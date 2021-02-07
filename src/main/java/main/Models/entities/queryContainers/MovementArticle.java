package main.Models.entities.queryContainers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class MovementArticle {

    private Instant date;
    private String  type ;
    private String  reference;
    private Float   quantity ;
    private Float   price ;

}
