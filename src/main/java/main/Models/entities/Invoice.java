package main.Models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.Models.enums.PaymentStatus;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Invoice {
    @Id
    public int order_items_id;
    public String order_items_name;
    public float order_items_quantity;
    public float order_items_price;
    public Instant orders_orderDate;
    public PaymentStatus orders_paymentStatus;
    public String clients_name;
    public float total;
    public float paid;

}
