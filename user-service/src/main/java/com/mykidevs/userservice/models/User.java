package com.mykidevs.userservice.models;



import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_id")
    @SequenceGenerator(name = "user_seq_id", sequenceName = "user_seq_id", allocationSize = 20)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "password", nullable = false)
    private String password;

    @Email(message = "Invalid email!")
    @NotEmpty(message = "Email can't be empty!")
    @Size(min = 6, max = 30, message = "Email must be between 6 and 30 characters!")
    private String email;


    @Size(min = 10, max = 100, message = "Description must be between 10 and 100 characters!")
    @NotEmpty
    private String description;

    @Column(name = "has_verified_email", nullable = false)
    private boolean hasVerifiedEmail = false;

}
