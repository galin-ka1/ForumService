package cohort_65.java.forumservice.post.service;

import cohort_65.java.forumservice.post.dao.PostRepository;
import cohort_65.java.forumservice.post.dto.DatePeriodDto;
import cohort_65.java.forumservice.post.dto.NewCommentDto;
import cohort_65.java.forumservice.post.dto.NewPostDto;
import cohort_65.java.forumservice.post.dto.PostDto;
import cohort_65.java.forumservice.post.dto.exception.PostNotFoundException;
import cohort_65.java.forumservice.post.model.Comment;
import cohort_65.java.forumservice.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    final PostRepository postRepository;
    final ModelMapper modelMapper;

    @Override
    public PostDto addNewPost(NewPostDto newPostDto, String author) {
        Post post = new Post(newPostDto.getTitle(),
                newPostDto.getContent(), author, newPostDto.getTags());
        post = postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto getPostById(String id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public void likePost(String id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        post.addLike();
        postRepository.save(post);
    }

    @Override
    public PostDto deletePostById(String id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        postRepository.delete(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto updatePostById(NewPostDto newPostDto, String id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        String content = newPostDto.getContent();
        if (content != null) {
            post.setContent(content);
        }
        String title = newPostDto.getTitle();
        if (title != null) {
            post.setTitle(title);
        }
        Set<String> tags = newPostDto.getTags();
        if (tags != null) {
            tags.forEach(post::addTag);
        }
        post = postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto addComment(String id, String user, NewCommentDto newCommentDto) {
        Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        Comment comment = new Comment(user, newCommentDto.getMessage());
        post.addComment(comment);
        post = postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public Iterable<PostDto> getPostsByAuthor(String author) {
        return StreamSupport
                .stream(postRepository.findAllByAuthorIgnoreCase(author).spliterator(),
                        false)
                .map(post -> modelMapper.map(post, PostDto.class)).toList();
    }

    @Override
    public Iterable<PostDto> getPostsByTags(Set<String> tags) {
        return StreamSupport
                .stream(postRepository.findAllByTagsIgnoreCaseIn(tags).spliterator(),
                        false)
                .map(post -> modelMapper.map(post, PostDto.class)).toList();
    }

    @Override
    public Iterable<PostDto> getPostsByPeriod(DatePeriodDto datePeriodDto) {
        return StreamSupport
                .stream(postRepository
                                .findAllByDateCreatedBetween(
                                        datePeriodDto.getDateFrom(),
                                        datePeriodDto.getDateTo()).spliterator(),
                        false)
                .map(post -> modelMapper.map(post, PostDto.class)).toList();
    }
}
