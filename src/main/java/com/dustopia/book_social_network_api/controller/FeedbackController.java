package com.dustopia.book_social_network_api.controller;

import com.dustopia.book_social_network_api.model.dto.FeedbackDto;
import com.dustopia.book_social_network_api.model.entity.Feedback;
import com.dustopia.book_social_network_api.model.request.FeedbackRequest;
import com.dustopia.book_social_network_api.model.response.PageData;
import com.dustopia.book_social_network_api.model.response.ResponseObject;
import com.dustopia.book_social_network_api.service.FeedbackService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("")
    public ResponseEntity<ResponseObject> addFeedback(
            @RequestBody @Valid FeedbackRequest feedbackRequest,
            Authentication connectedUser
    ) {
        Feedback feedback = feedbackService.addFeedback(feedbackRequest, connectedUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseObject("success", feedback));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateFeedback(
            @PathVariable Long id,
            @RequestBody FeedbackRequest feedbackRequest,
            Authentication connectedUser
    ) {
        Feedback feedback = feedbackService.updateFeedback(id, feedbackRequest, connectedUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseObject("success", feedback));
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<ResponseObject> getAllFeedbacksByBook(
            @PathVariable Long id,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            Authentication connectedUser
    ) {
        PageData<FeedbackDto> feedbackDtos = feedbackService.getAllFeedbacksByBook(id, page, size, connectedUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseObject("success", feedbackDtos));
    }

}
