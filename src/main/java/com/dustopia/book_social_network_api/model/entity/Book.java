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

    private String bookCoverUrl;

    private String url;

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
    private List<Feedback> feedbacks;

    @OneToMany(
            mappedBy = "book",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private List<BookTransaction> bookTransaction;

    @Transient
    public Double getRate() {
        if (feedbacks == null || feedbacks.isEmpty())  {
            return 0.0;
        }
        double rate = feedbacks
                .stream()
                .mapToDouble(Feedback::getStar)
                .average()
                .orElse(0.0);
        double roundedRate;
        roundedRate = Math.round(rate * 10.0) / 10.0;
        return roundedRate;
    }

}
