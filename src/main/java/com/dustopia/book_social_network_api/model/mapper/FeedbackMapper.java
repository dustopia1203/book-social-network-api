package com.dustopia.book_social_network_api.model.mapper;

import com.dustopia.book_social_network_api.model.dto.FeedbackDto;
import com.dustopia.book_social_network_api.model.entity.Feedback;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {

    public FeedbackDto toFeedbackDto(Feedback feedback, boolean isOwner) {
        return new FeedbackDto(
                feedback.getStar(),
                feedback.getComment(),
                isOwner
        );
    }

}
