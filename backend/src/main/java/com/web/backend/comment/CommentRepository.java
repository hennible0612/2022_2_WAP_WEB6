package com.web.backend.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {

    //찬반 토론 댓글 조회
    @Query(value = "SELECT * FROM comment WHERE procontopic_id = :proConTopicId", nativeQuery = true)
    List<CommentEntity> findByProConTopicId(@Param("proConTopicId") long proConTopicId);

    @Query(value = "SELECT * FROM comment WHERE procontopic_id = :proConTopicId AND parent_comment_id = :parentCommentId", nativeQuery = true)
//    List<CommentEntity> findByCommentId(@Param("proConTopicId") long proConTopicId);
    List<CommentEntity> findByCommentId(@Param("proConTopicId") long proConTopicId, @Param("parentCommentId") long parentCommentId);
}
