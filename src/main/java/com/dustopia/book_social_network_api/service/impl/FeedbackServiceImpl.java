package com.dustopia.book_social_network_api.service.impl;

import com.dustopia.book_social_network_api.exception.OperationNotPermittedException;
import com.dustopia.book_social_network_api.exception.PermissionDeniedAccessException;
import com.dustopia.book_social_network_api.model.dto.FeedbackDto;
import com.dustopia.book_social_network_api.model.entity.Book;
import com.dustopia.book_social_network_api.model.entity.Feedback;
import com.dustopia.book_social_network_api.model.entity.User;
import com.dustopia.book_social_network_api.model.mapper.FeedbackMapper;
import com.dustopia.book_social_network_api.model.request.FeedbackRequest;
import com.dustopia.book_social_network_api.model.response.PageData;
import com.dustopia.book_social_network_api.repository.BookRepository;
import com.dustopia.book_social_network_api.repository.FeedbackRepository;
import com.dustopia.book_social_network_api.security.CustomUserDetails;
import com.dustopia.book_social_network_api.service.FeedbackService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final BookRepository bookRepository;

    private final FeedbackRepository feedbackRepository;

    private final FeedbackMapper feedbackMapper;

    @Override
    public Feedback addFeedback(FeedbackRequest feedbackRequest, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Book book = bookRepository
                .findById(feedbackRequest.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book is not found with id " + feedbackRequest.bookId()));
        if (!book.isShareable()) {
            throw new OperationNotPermittedException("Cannot add feedback for this book");
        }
        if (book.getUser().getId().equals(user.getId())) {
            throw new OperationNotPermittedException("Cannot add feedback to your book");
        }
        Feedback feedback = Feedback
                .builder()
                .star(feedbackRequest.star())
                .comment(feedbackRequest.comment())
                .user(user)
                .book(book)
                .build();
        feedbackRepository.save(feedback);
        return feedback;
    }

    @Override
    public Feedback updateFeedback(Long id, FeedbackRequest feedbackRequest, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Feedback feedback = feedbackRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feedback not found"));
        if (!feedback.getUser().getId().equals(user.getId())) {
            throw new PermissionDeniedAccessException("You don't have permission to update this feedback");
        }
        if (feedbackRequest.star() != null) feedback.setStar(feedbackRequest.star());
        if (feedbackRequest.comment() != null) feedback.setComment(feedbackRequest.comment());
        feedbackRepository.save(feedback);
        return feedback;
    }

    @Override
    public PageData<FeedbackDto> getAllFeedbacksByBook(Long id, int page, int size, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Feedback> feedbacks = feedbackRepository.findAllFeedbackByBook(id, pageable);
        List<FeedbackDto> feedbackDtos = feedbacks
                .stream()
                .map(feedback -> feedbackMapper.toFeedbackDto(feedback, id.equals(user.getId())))
                .toList();
        return new PageData<>(
                feedbackDtos,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }

}
