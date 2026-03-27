package com.mykidevs.userservice.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Table(name = "verification_tokens")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class VerifyToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = true)
    private Long id;

    @Column(name = "value")
    private String value;
    @OneToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    @Column(name = "exp_date", nullable = false)
    private Instant expDate;
}
