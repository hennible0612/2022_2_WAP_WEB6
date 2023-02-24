package com.web.backend.likedislikecomment;

import com.web.backend.likecomment.LikeCommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LikeDislikeCommentRepository extends JpaRepository<LikeDislikeCommentEntity, Long> {

    @Query(value = "SELECT * FROM like_dislike_table WHERE user_id = :userId AND comment_id = :commentId", nativeQuery = true)
    LikeDislikeCommentEntity getByUserId(@Param("userId") long userId, @Param("commentId") long commentId);


    @Query(value = "SELECT * FROM like_dislike_table WHERE user_id = :userId AND debate_id = :proconId", nativeQuery = true)
    List<LikeDislikeCommentEntity> getByUserIdAndProconId(@Param("userId") long userId, @Param("proconId") long proconId);

}
