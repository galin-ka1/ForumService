package cohort_65.java.forumservice.post.controller;

import cohort_65.java.forumservice.post.dto.*;
import cohort_65.java.forumservice.post.dto.exception.PostNotFoundException;
import cohort_65.java.forumservice.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Set;

@RestController
@RequestMapping("/forum")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;


    @PostMapping("/post/{author}")
    public PostDto addNewPost(@RequestBody NewPostDto newPostDto,
                              @PathVariable String author) {
        return postService.addNewPost(newPostDto, author);
    }


    @GetMapping("/post/{postId}")
    public ResponseEntity<PostDto> findPostById(@PathVariable String id) {
        PostDto postDto = postService.findPostById(id);
        if (postDto == null) {
            throw new PostNotFoundException();
        }
        return ResponseEntity.ok(postDto);
    }


    @PostMapping("/post/{{postId}}/like")
    public ResponseEntity<Void> addLike(@PathVariable String id,
                                        @RequestBody PostDto postDto) {
        boolean success = postService.addLike(postDto, id);
        if (!success) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/posts/author/{author}")
    public ResponseEntity<Set<PostDto>> findPostsByAuthor(@PathVariable String author) {
        Set<PostDto> posts = postService.findPostsByAuthor(author);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/post/{{postId}}/comment/{author}")
    public ResponseEntity<CommentDto> addNewComment(@RequestBody NewCommentDto newCommentDto,
                                                    @PathVariable String author) {
        CommentDto commentDto = postService.addNewComment(newCommentDto, author);
        return new ResponseEntity<>(commentDto, HttpStatus.CREATED);
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<PostDto> deletePostById(@PathVariable("postId") String postId) {
        PostDto deletedPost = postService.deletePostById(postId);
        return ResponseEntity.ok(deletedPost);
    }

    @GetMapping("/posts/tags")
    public ResponseEntity<Set<PostDto>> findPostsByTag(@PathVariable String tag) {
        Set<PostDto> posts = postService.findPostsByTag(tag);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/period")
    public ResponseEntity<Set<PostDto>> findPostsByPeriod(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        Set<PostDto> posts = postService.findPostsByTimePeriod(start, end);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/post/{{postId}}")
    public ResponseEntity<PostDto> updatePost(@PathVariable String id,
                                              @PathVariable String author,
                                              @RequestBody PostDto postDto) {
        if (!id.equals(postDto.getId())) {
            return ResponseEntity.badRequest().build();
        }

        PostDto updated = postService.updatePost(postDto, author);
        return ResponseEntity.ok(updated);
    }
}
