package com.dustopia.book_social_network_api.service;

import com.dustopia.book_social_network_api.model.dto.FeedbackDto;
import com.dustopia.book_social_network_api.model.entity.Feedback;
import com.dustopia.book_social_network_api.model.request.FeedbackRequest;
import com.dustopia.book_social_network_api.model.response.PageData;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public interface FeedbackService {

    Feedback addFeedback(FeedbackRequest feedbackRequest, Authentication connectedUser);

    Feedback updateFeedback(Long id, FeedbackRequest feedbackRequest, Authentication connectedUser);

    PageData<FeedbackDto> getAllFeedbacksByBook(Long id, int page, int size, Authentication connectedUser);
}
