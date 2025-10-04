package cohort_65.java.forumservice.post.service;

import cohort_65.java.forumservice.post.dao.PostRepository;
import cohort_65.java.forumservice.post.dto.*;
import cohort_65.java.forumservice.post.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    @MockitoBean
    PostRepository postRepository;

    Post post;

    @BeforeEach
    void setUp() {
        post = new Post("title1", "content1", "author1", Set.of("tag1", "tag2"));
        post.setId("1");
        post.setDateCreated(LocalDateTime.now());
    }

    // ---------- Positive tests ----------
    @Test
    void addNewPost() {
        NewPostDto newPostDto = new NewPostDto("title1", "content1", Set.of("tag1", "tag2"));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        PostDto result = postService.addNewPost(newPostDto, "author1");
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getAuthor()).isEqualTo("author1");
        assertThat(result.getTitle()).isEqualTo("title1");
        Mockito.verify(postRepository).save(any(Post.class));
    }

    @Test
    void getPostById_Found() {
        when(postRepository.findById("1")).thenReturn(Optional.of(post));
        PostDto result = postService.getPostById("1");
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getAuthor()).isEqualTo("author1");
        verify(postRepository).findById("1");
    }

    @Test
    void likePost() {
        post.setLikes(0);
        when(postRepository.findById("1")).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        postService.likePost("1");
        verify(postRepository).findById("1");
        verify(postRepository).save(any(Post.class));
        assertThat(post.getLikes()).isEqualTo(1);
    }

    @Test
    void deletePostById() {
        when(postRepository.findById("1")).thenReturn(Optional.of(post));
        PostDto deleted = postService.deletePostById("1");
        verify(postRepository).findById("1");
        verify(postRepository).delete(post);
        assertThat(deleted.getId()).isEqualTo("1");
    }

    @Test
    void updatePostById() {
        NewPostDto updateDto = new NewPostDto("updated title", "updated content", Set.of("tag3"));
        when(postRepository.findById("1")).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        PostDto result = postService.updatePostById(updateDto, "1");
        assertThat(result).isNotNull();
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void addComment() {
        NewCommentDto newCommentDto = new NewCommentDto("Nice post!");
        when(postRepository.findById("1")).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        PostDto result = postService.addComment("1", "user1", newCommentDto);
        assertThat(result).isNotNull();
        assertThat(result.getComments().size()).isEqualTo(1);
        verify(postRepository).findById("1");
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void getPostsByAuthor() {
        when(postRepository.findByAuthor("author1")).thenReturn(List.of(post));
        Iterable<PostDto> result = postService.getPostsByAuthor("author1");
        assertThat(result.iterator().hasNext()).isTrue();
        verify(postRepository).findByAuthor("author1");
    }

    @Test
    void getPostsByTags() {
        Set<String> tags = Set.of("tag1");
        when(postRepository.findByTagsIn(tags)).thenReturn(List.of(post));
        Iterable<PostDto> result = postService.getPostsByTags(tags);
        assertThat(result.iterator().hasNext()).isTrue();
        verify(postRepository).findByTagsIn(tags);
    }

    @Test
    void getPostsByPeriod() {
        DatePeriodDto period = new DatePeriodDto(LocalDate.now().minusDays(5), LocalDate.now());
        when(postRepository.findByDateCreatedBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(post));
        Iterable<PostDto> result = postService.getPostsByPeriod(period);
        assertThat(result.iterator().hasNext()).isTrue();
        verify(postRepository).findByDateCreatedBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    // ---------- Negative tests ----------
    @Test
    void getPostById_NotFound() {
        when(postRepository.findById("999")).thenReturn(Optional.empty());
        Exception ex = assertThrows(RuntimeException.class, () -> postService.getPostById("999"));
        assertThat(ex.getMessage()).contains("not found");
        verify(postRepository).findById("999");
    }

    @Test
    void deletePostById_NotFound() {
        when(postRepository.findById("999")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.deletePostById("999"));
        verify(postRepository).findById("999");
    }

    @Test
    void updatePostById_NotFound() {
        when(postRepository.findById("999")).thenReturn(Optional.empty());
        NewPostDto dto = new NewPostDto("t", "c", Set.of("tag"));
        assertThrows(RuntimeException.class, () -> postService.updatePostById(dto, "999"));
        verify(postRepository).findById("999");
    }

    @Test
    void likePost_NotFound() {
        when(postRepository.findById("999")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> postService.likePost("999"));
        verify(postRepository).findById("999");
    }

    @Test
    void addComment_NotFound() {
        when(postRepository.findById("999")).thenReturn(Optional.empty());
        NewCommentDto comment = new NewCommentDto("Some text");
        assertThrows(RuntimeException.class, () -> postService.addComment("999", "user1", comment));
        verify(postRepository).findById("999");
    }

    @Test
    void getPostsByAuthor_Empty() {
        when(postRepository.findByAuthor("unknown")).thenReturn(Collections.emptyList());
        Iterable<PostDto> result = postService.getPostsByAuthor("unknown");
        assertThat(result.iterator().hasNext()).isFalse();
        verify(postRepository).findByAuthor("unknown");
    }

    @Test
    void getPostsByTags_Empty() {
        Set<String> tags = Set.of("unknown");
        when(postRepository.findByTagsIn(tags)).thenReturn(Collections.emptyList());
        Iterable<PostDto> result = postService.getPostsByTags(tags);
        assertThat(result.iterator().hasNext()).isFalse();
        verify(postRepository).findByTagsIn(tags);
    }

    @Test
    void getPostsByPeriod_Empty() {
        DatePeriodDto period = new DatePeriodDto(LocalDate.now().minusDays(10), LocalDate.now().minusDays(5));
        when(postRepository.findByDateCreatedBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        Iterable<PostDto> result = postService.getPostsByPeriod(period);
        assertThat(result.iterator().hasNext()).isFalse();
        verify(postRepository).findByDateCreatedBetween(any(LocalDateTime.class), any(LocalDateTime.class));
    }

    @Test
    void getPostsByTitle() {
        Post p1 = new Post("t1", "c1", "authorA", Set.of("x"));
        p1.setId("1");
        Post p2 = new Post("t2", "c2", "authorB", Set.of("y", "s"));
        p2.setId("2");
        Post p3 = new Post("t3", "c3", "authorC", Set.of("w"));
        p3.setId("3");
        when(postRepository.findAllByTitleIgnoreCase("t2")).thenReturn(List.of(p1, p2, p3));
        Iterable<PostDto> result = postService.getPostsByTitle("t2");

        assertThat(result).hasSize(2);
        assertThat(result.iterator().next().getTitle()).isEqualTo("t2");
        assertThat(result.iterator().next().getAuthor()).isEqualTo("authorA");
       // assertThat(result.iterator().next().getAuthor()).isEqualTo("authorB");
        assertThat(result.iterator().next().getTags()).contains("y","s");

    }
}
