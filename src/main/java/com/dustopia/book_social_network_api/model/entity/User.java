package com.dustopia.book_social_network_api.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_user")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    @Column(unique = true)
    private String email;

    private String password;

    private String role;

    private boolean accountLocked;

    private boolean isEnabled;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<Book> books;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<FeedBack> feedBacks;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<BookTransaction> bookTransaction;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    @LastModifiedBy
    @Column(insertable = false)
    private LocalDateTime lastModifiedBy;

    public String getFullName() {
        return firstName + " " + lastName;
    }

}
