package cohort_65.java.forumservice.post.service;

import cohort_65.java.forumservice.post.dao.PostRepository;
import cohort_65.java.forumservice.post.dto.CommentDto;
import cohort_65.java.forumservice.post.dto.NewCommentDto;
import cohort_65.java.forumservice.post.dto.NewPostDto;
import cohort_65.java.forumservice.post.dto.PostDto;
import cohort_65.java.forumservice.post.dto.exception.PostNotFoundException;
import cohort_65.java.forumservice.post.model.Comment;
import cohort_65.java.forumservice.post.model.Post;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public PostDto findPostById(String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public boolean addLike(PostDto postDto, String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);

        post.addLike();
        postRepository.save(post);

        return true;
    }

    @Override
    public Set<PostDto> findPostsByAuthor(String author) {
        List<Post> posts = postRepository.findAllByAuthor(author);
        return posts.stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public CommentDto addNewComment(NewCommentDto newCommentDto, String author) {
        Post post = postRepository.findById(newCommentDto.getPostId())
                .orElseThrow(PostNotFoundException::new);

        Comment comment = new Comment(author, newCommentDto.getMessage());
        post.getComments().add(comment);
        postRepository.save(post);

        return modelMapper.map(comment, CommentDto.class);
    }


    @Override
    public Set<PostDto> findPostsByTag(String tag) {
        List<Post> posts = postRepository.findAllByTagsContaining(tag);
        return posts.stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .collect(Collectors.toSet());
    }


    @Override
    public PostDto deletePostById(String id) {
        Post post = postRepository.findById(id)
                .orElseThrow(PostNotFoundException::new);

        postRepository.delete(post);

        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public Set<PostDto> findPostsByTimePeriod(LocalDateTime start, LocalDateTime end) {
        List<Post> posts = postRepository.findAllByDateCreatedBetween(start, end);
        return posts.stream()
                .map(post -> modelMapper.map(post, PostDto.class))
                .collect(Collectors.toSet());
    }

    @Override
    public PostDto updatePost(PostDto postDto, String author) {
        Post post = postRepository.findById(postDto.getId())
                .orElseThrow(PostNotFoundException::new);

        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setTags(postDto.getTags());

        postRepository.save(post);

        return modelMapper.map(post, PostDto.class);
    }

}
