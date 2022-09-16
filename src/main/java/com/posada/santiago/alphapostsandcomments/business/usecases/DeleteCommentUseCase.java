package com.posada.santiago.alphapostsandcomments.business.usecases;

import co.com.sofka.domain.generic.DomainEvent;
import com.posada.santiago.alphapostsandcomments.business.gateways.DomainEventRepository;
import com.posada.santiago.alphapostsandcomments.business.gateways.EventBus;
import com.posada.santiago.alphapostsandcomments.business.generic.UseCaseForCommand;
import com.posada.santiago.alphapostsandcomments.domain.Post;
import com.posada.santiago.alphapostsandcomments.domain.commands.DeleteCommentCommand;
import com.posada.santiago.alphapostsandcomments.domain.values.Author;
import com.posada.santiago.alphapostsandcomments.domain.values.CommentId;
import com.posada.santiago.alphapostsandcomments.domain.values.Content;
import com.posada.santiago.alphapostsandcomments.domain.values.PostId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class DeleteCommentUseCase extends UseCaseForCommand<DeleteCommentCommand> {
    private final DomainEventRepository eventRepository;
    private final EventBus bus;

    public DeleteCommentUseCase(DomainEventRepository eventRepository, EventBus bus) {
        this.eventRepository = eventRepository;
        this.bus = bus;
    }

    @Override
    public Flux<DomainEvent> apply(Mono<DeleteCommentCommand> deleteCommentCommandMono) {
        return deleteCommentCommandMono.flatMapMany(command -> eventRepository.findById(command.getPostId())
                .collectList()
                .flatMapIterable(events -> {
                    Post post = Post.from(PostId.of(command.getPostId()), events);
                    if(post.getCommentByID(CommentId.of(command.getCommentId())).isPresent()){
                        post.deleteComment(post.getCommentByID(CommentId.of(command.getCommentId())).get().identity());
                    }
                    return post.getUncommittedChanges();
                }).flatMap(eventRepository::saveEvent)).doOnNext(bus::publish);
    }
}
