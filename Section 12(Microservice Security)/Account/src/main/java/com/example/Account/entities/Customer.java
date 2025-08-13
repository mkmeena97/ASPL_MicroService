package com.example.Account.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Customer extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // for AUTO_INCREMENT in MySQL
    @Column(name = "customer_id")
    private Integer customerId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(name="mobile_number", nullable = false, length = 20)
    private String mobileNumber;


    // Optionally, you can define OneToMany relationship with Accounts here
    // @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    // private List<Accounts> accounts;

}

