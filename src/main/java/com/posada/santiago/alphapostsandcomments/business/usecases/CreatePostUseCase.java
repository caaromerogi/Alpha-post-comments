package com.posada.santiago.alphapostsandcomments.business.usecases;

import co.com.sofka.domain.generic.DomainEvent;
import com.posada.santiago.alphapostsandcomments.business.gateways.DomainEventRepository;
import com.posada.santiago.alphapostsandcomments.business.gateways.EventBus;
import com.posada.santiago.alphapostsandcomments.business.generic.UseCaseForCommand;
import com.posada.santiago.alphapostsandcomments.domain.Post;
import com.posada.santiago.alphapostsandcomments.domain.commands.CreatePostCommand;
import com.posada.santiago.alphapostsandcomments.domain.values.Author;
import com.posada.santiago.alphapostsandcomments.domain.values.PostId;
import com.posada.santiago.alphapostsandcomments.domain.values.Title;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class CreatePostUseCase extends UseCaseForCommand<CreatePostCommand> {

    private final static Logger logger= LoggerFactory.getLogger(CreatePostUseCase.class);
    private final DomainEventRepository repository;
    private final EventBus bus;

    public CreatePostUseCase(DomainEventRepository repository, EventBus bus) {
        this.repository = repository;
        this.bus = bus;
    }

    @Override
    public Flux<DomainEvent> apply(Mono<CreatePostCommand> createPostCommandMono) {
        return createPostCommandMono.flatMapIterable(command -> {
            Post post = new Post(PostId.of(command.getPostId()), new Title(command.getTitle()), new Author(command.getAuthor()));
            return post.getUncommittedChanges();
        }).flatMap(event ->{
            logger.info("Create Post use case working in alpha");
            return  repository.saveEvent(event).thenReturn(event);
        })
               .doOnNext(bus::publish)
                .onErrorResume(error ->{
                    logger.error("Create post use case failed in alpha");
                    return Flux.just();
                });
    }
}
