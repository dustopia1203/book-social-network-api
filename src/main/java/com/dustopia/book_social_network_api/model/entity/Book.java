package com.dustopia.book_social_network_api.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_table")
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

}
