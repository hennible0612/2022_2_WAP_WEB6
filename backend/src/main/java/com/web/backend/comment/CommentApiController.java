package com.web.backend.comment;

import com.web.backend.config.APIResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CommentApiController {

    @Autowired
    private CommentService commentService;

    //해당 토론 댓글 목록 조회
    // 해당 proconTopic 댓글 조회
    @GetMapping("/api/proconTopic/{proconId}/comments")
    public ResponseEntity<List<CommentDto>> proConComments(@PathVariable Long proconId) {
        // 서비스에게 위임
        List<CommentDto> dtos = commentService.proConComments(proconId);
        // 결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }
    // 대댓글 조회
    @PostMapping("/api/reply/proconTopic/{proconId}/motherComment/{parentCommentId}")
    public ResponseEntity<List<CommentDto>> replyComments(@PathVariable Long proconId,
                                                          @PathVariable Long parentCommentId,
                                                          @RequestBody Map<String, Long> map) {
        // 임시 방편
        if (map.get("userId") == null) {
            map.put("userId", 0L);
        }
        // 서비스에게 위임
        List<CommentDto> dtos = commentService.replyComments(proconId, parentCommentId, map);
        // 결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    //일반 댓글 페이징 해서 전달
    @PostMapping("/api/CommentsPaged/page/{offset}/size/{pageSize}/proconId/{proconId}")
    public APIResponse<Page<?>> getCommentsTopicPaged(@PathVariable int offset,
                                                      @PathVariable int pageSize,
                                                      @PathVariable Long proconId,
                                                      @RequestBody Map<String,Long> map
                                                      ) {

        if (map.get("userId") == null) {
            map.put("userId", 0L);
        }
        Page<CommentDto> comments = commentService.proConCommentsWithPagination(offset, pageSize, proconId, map);

        return new APIResponse<>(comments.getSize(), comments);
    }

    // 댓글 생성
    @PostMapping("/api/proconTopic/{proConTopicId}/user/{userId}")
    public ResponseEntity<CommentDto> create(@PathVariable Long proConTopicId,
                                             @PathVariable Long userId,
                                             @RequestBody CommentDto dto) {

        // 서비스에게 위임
        CommentDto createdDto = commentService.create(userId, proConTopicId, dto);
        // 결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(createdDto);

    }

    // 대댓글
    @PostMapping("/api/proconTopic/{proConTopicId}/user/{userId}/parent/{parentId}")
    public ResponseEntity<CommentDto> commentReply(@PathVariable Long proConTopicId,
                                                   @PathVariable Long userId,
                                                   @PathVariable Long parentId,
                                                   @RequestBody CommentDto dto) {


        CommentDto createdDto = commentService.createReplyComment(userId, proConTopicId, parentId, dto);
        // 결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(createdDto);
    }
    //paging 해서 전달
//    @GetMapping("/api/CommentsPaged/page/{offset}/size/{pageSize}/proconId/{proconId}")
//    public APIResponse<Page<CommentDto>> getCommentsTopicPaged(@PathVariable int offset, @PathVariable int pageSize, @PathVariable Long proconId) {
//        Page<CommentDto> comments = commentService.proConCommentsWithPagination(offset, pageSize, proconId);
//        return new APIResponse<>(comments.getSize(), comments);
//    }


    

    // 댓글 수정
    @PatchMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentDto> patch(@PathVariable Long commentId,
                                            @RequestBody CommentDto dto) {
        // 서비스에게 위임
        CommentDto updateDto = commentService.update(commentId, dto);
        // 결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(updateDto);

    }

    // 댓글 삭제
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentDto> delete(@PathVariable Long commentId) {
        // 서비스에게 위임
        CommentDto updateDto = commentService.delete(commentId);
        // 결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(updateDto);

    }




    //    //Sorting해서 전달
//    @GetMapping("/api/getCommentsSort")
//    public APIResponse<List<CommentDto>> getCommentsWithSort() {
//        List<CommentDto> dtos = commentService.getCommentsWithSorting("likeNum");
//        return new APIResponse<>(dtos.size(), dtos);
//    }
//
//    //paging 해서 전달
//    @GetMapping("/api/comment/pagination/{offset}/{proconId}")
//    public APIResponse<Page<CommentEntity>> getCommentsPaged(@PathVariable int offset, @PathVariable int proconId) {
//        Page<CommentEntity> comments = commentService.getCommentsWithPagination(offset, proconId);
//        return new APIResponse<>(comments.getSize(), comments);
//    }

}
