package main.Models.entities.queryContainers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.Models.entities.ClientEntity;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientCredit {

    private ClientEntity client ;
    private double credit ;


}
