package cohort_65.java.forumservice.post.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private String id;
    private String title;
    private String content;
    private String author;

    @Singular
    private Set<String> tags;

    private int likes;
    private LocalDateTime dateCreated;

    @Singular
    private List<CommentDto> comments;
}
