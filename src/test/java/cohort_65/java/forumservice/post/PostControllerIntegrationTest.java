package cohort_65.java.forumservice.post;

import cohort_65.java.forumservice.post.dto.DatePeriodDto;
import cohort_65.java.forumservice.post.dto.NewCommentDto;
import cohort_65.java.forumservice.post.dto.NewPostDto;
import cohort_65.java.forumservice.post.dto.PostDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PostControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void testAddNewPost_ReturnsPostDto() {
        NewPostDto newPost = new NewPostDto("Test title", "Test content", Set.of("spring", "java"));

        ResponseEntity<PostDto> response = restTemplate.postForEntity(
                baseUrl() + "/forum/post/john_doe",
                newPost,
                PostDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTitle()).isEqualTo("Test title");
        assertThat(response.getBody().getAuthor()).isEqualTo("john_doe");
    }

    @Test
    void testLikePost() {
        NewPostDto post = new NewPostDto("Title", "Text", Set.of("tag"));

        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl() + "/forum/post/anna",
                post,
                String.class
        );

        String id = JsonPath.read(response.getBody(), "$.id");

        restTemplate.put(baseUrl() + "/forum/post/" + id + "/like", null);

        // You could also check likes count here if endpoint returns it
    }

    @Test
    void testGetPostsByAuthor() {
        NewPostDto post = new NewPostDto("Author test", "Content", Set.of("java"));

        restTemplate.postForEntity(baseUrl() + "/forum/post/test_user", post, String.class);

        ResponseEntity<PostDto[]> response = restTemplate.getForEntity(
                baseUrl() + "/forum/posts/author/test_user",
                PostDto[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()[0].getAuthor()).isEqualTo("test_user");
    }

    @Test
    void testAddCommentToPost() {
        NewPostDto post = new NewPostDto("Commented Post", "Text", Set.of());

        ResponseEntity<String> postResponse = restTemplate.postForEntity(
                baseUrl() + "/forum/post/user1",
                post,
                String.class
        );

        String id = JsonPath.read(postResponse.getBody(), "$.id");

        NewCommentDto comment = new NewCommentDto("Nice post!");

        HttpEntity<NewCommentDto> request = new HttpEntity<>(comment);
        ResponseEntity<PostDto> updatedPost = restTemplate.exchange(
                baseUrl() + "/forum/post/" + id + "/comment/user2",
                HttpMethod.PUT,
                request,
                PostDto.class
        );

        assertThat(updatedPost.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updatedPost.getBody()).isNotNull();
        assertThat(updatedPost.getBody().getComments()).isNotEmpty();
        assertThat(updatedPost.getBody().getComments().get(0).getMessage()).isEqualTo("Nice post!");
        assertThat(updatedPost.getBody().getComments().get(0).getUser()).isEqualTo("user2");
    }

    @Test
    void testUpdatePost() {
        NewPostDto createDto = new NewPostDto("Original Title", "Original Content", Set.of("tag1"));

        ResponseEntity<String> postResponse = restTemplate.postForEntity(
                baseUrl() + "/forum/post/author1",
                createDto,
                String.class
        );

        String id = JsonPath.read(postResponse.getBody(), "$.id");

        NewPostDto updateDto = new NewPostDto("Updated Title", "Updated Content", Set.of("tag2", "tag3"));

        HttpEntity<NewPostDto> request = new HttpEntity<>(updateDto);
        ResponseEntity<PostDto> updatedResponse = restTemplate.exchange(
                baseUrl() + "/forum/post/" + id,
                HttpMethod.PUT,
                request,
                PostDto.class
        );

        assertThat(updatedResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        PostDto updated = updatedResponse.getBody();
        assertThat(updated).isNotNull();
        assertThat(updated.getTitle()).isEqualTo("Updated Title");
        assertThat(updated.getContent()).isEqualTo("Updated Content");
        assertThat(updated.getTags()).contains("tag2", "tag3");
    }

    @Test
    void testGetPostsByTags() {
        Set<String> tags = Set.of("spring", "java");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Set<String>> request = new HttpEntity<>(tags, headers);

        ResponseEntity<PostDto[]> response = restTemplate.postForEntity(
                baseUrl() + "/forum/posts/tags",
                request,
                PostDto[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).allMatch(post ->
                post.getTags().stream().anyMatch(tags::contains)
        );
    }

    @Test
    void testGetPostsByPeriod() {
        DatePeriodDto period = new DatePeriodDto(
                LocalDate.of(2023, 1, 1),
                LocalDate.of(2023, 12, 31)
        );

        ResponseEntity<PostDto[]> response = restTemplate.postForEntity(
                baseUrl() + "/forum/posts/period",
                period,
                PostDto[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        // можно добавить проверку диапазона дат в каждом посте
    }
}
