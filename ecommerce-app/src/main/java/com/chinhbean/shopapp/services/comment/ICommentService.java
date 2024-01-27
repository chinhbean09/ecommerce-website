package com.chinhbean.shopapp.services.comment;

import com.chinhbean.shopapp.responses.comment.CommentResponse;
import com.chinhbean.shopapp.dtos.CommentDTO;
import com.chinhbean.shopapp.exceptions.DataNotFoundException;
import com.chinhbean.shopapp.models.Comment;
import com.chinhbean.shopapp.responses.comment.CommentResponse;

import java.util.List;

public interface ICommentService {
    Comment insertComment(CommentDTO comment);

    void deleteComment(Long commentId);
    void updateComment(Long id, CommentDTO commentDTO) throws DataNotFoundException;

    List<CommentResponse> getCommentsByUserAndProduct(Long userId, Long productId);
    List<CommentResponse> getCommentsByProduct(Long productId);
}
