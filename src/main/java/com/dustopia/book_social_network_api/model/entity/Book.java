package com.dustopia.book_social_network_api.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_book")
public class Book extends BaseObject {

    private String title;

    private String author;

    private String synopsis;

    private String bookCover;

    private boolean isArchived;

    private boolean isShareable;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonBackReference
    private User user;

    @OneToMany(
            mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<BookTransaction> bookTransaction;

}
