package com.example.Account.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
//@Table(name = "customer")    use it when entity name and database table name is different.
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customer_id")
    private Long customerId;

    private String name;

    private String email;

    @Column(name="mobile_number")
    private String mobileNumber;
}
