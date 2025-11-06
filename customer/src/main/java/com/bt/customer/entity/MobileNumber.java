package com.bt.customer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mobile_numbers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MobileNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 5)
    private String countryCode;

    @Column(nullable = false, length = 15)
    private String number;

    @OneToOne(mappedBy = "mobileNumber")
    private User user;

    public String getFullNumber() {
        return countryCode + number;
    }
}
