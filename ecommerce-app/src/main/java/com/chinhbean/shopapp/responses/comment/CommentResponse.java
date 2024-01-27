package com.chinhbean.shopapp.responses.comment;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.chinhbean.shopapp.models.Comment;
import com.chinhbean.shopapp.responses.user.UserResponse;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {
    @JsonProperty("content")
    private String content;

    //user's information
    @JsonProperty("user")
    private UserResponse userResponse;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    public static CommentResponse fromComment(Comment comment) {
        return CommentResponse.builder()
                .content(comment.getContent())
                .userResponse(UserResponse.fromUser(comment.getUser()))
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
