package cohort_65.java.forumservice.security;

import cohort_65.java.forumservice.post.dao.PostRepository;
import cohort_65.java.forumservice.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostSecurity {

    private final PostRepository postRepository;

    // Возвращает логин автора поста по id
    public String getPostAuthor(String postId) {
        return postRepository.findById(postId)
                .map(Post::getAuthor)
                .orElse("");
    }
}
