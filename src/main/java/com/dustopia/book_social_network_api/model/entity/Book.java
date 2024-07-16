package com.dustopia.book_social_network_api.model.entity;

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

    private String isbn;

    private String bookCover;

    private boolean isArchived;

    private boolean isShareable;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(
            mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<BookTransaction> bookTransaction;

}
