package com.bt.customer.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "names")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Name {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(length = 50)
    private String middleName;

    @Column(nullable = false, length = 50)
    private String lastName;

    @OneToOne(mappedBy = "name")
    private User user;

    public String getFullName() {
        StringBuilder fullName = new StringBuilder(firstName);
        if (middleName != null && !middleName.isBlank()) {
            fullName.append(" ").append(middleName);
        }
        fullName.append(" ").append(lastName);
        return fullName.toString();
    }
}
