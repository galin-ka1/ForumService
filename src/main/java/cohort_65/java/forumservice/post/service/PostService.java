package cohort_65.java.forumservice.post.service;

import cohort_65.java.forumservice.post.dto.CommentDto;
import cohort_65.java.forumservice.post.dto.NewCommentDto;
import cohort_65.java.forumservice.post.dto.NewPostDto;
import cohort_65.java.forumservice.post.dto.PostDto;

import java.time.LocalDateTime;
import java.util.Set;

public interface PostService {

    PostDto addNewPost(NewPostDto newPostDto, String author);

    PostDto findPostById(String id);

    boolean addLike(PostDto postDto, String id);

    Set<PostDto> findPostsByAuthor(String author);

    CommentDto addNewComment(NewCommentDto newCommentDto, String author);

    PostDto deletePostById(String id);

    Set<PostDto> findPostsByTag(String tag);

    Set<PostDto> findPostsByTimePeriod(LocalDateTime start, LocalDateTime end);

    PostDto updatePost(PostDto postDto, String author);


}
