package com.posada.santiago.alphapostsandcomments.business.usecases;

import com.posada.santiago.alphapostsandcomments.business.gateways.DomainEventRepository;
import com.posada.santiago.alphapostsandcomments.business.gateways.EventBus;
import com.posada.santiago.alphapostsandcomments.domain.commands.AddCommentCommand;
import com.posada.santiago.alphapostsandcomments.domain.commands.CreatePostCommand;
import com.posada.santiago.alphapostsandcomments.domain.events.CommentAdded;
import com.posada.santiago.alphapostsandcomments.domain.events.PostCreated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AddCommentUseCaseTest {
    @Mock
    DomainEventRepository domainEventRepository;

    @Mock
    EventBus eventBus;

    AddCommentUseCase useCase;

    @BeforeEach
    void init(){
        useCase = new AddCommentUseCase(domainEventRepository,eventBus);
    }

    @Test
    void addCommentUseCaseTest() {
        PostCreated post = new PostCreated("Black mirror", "Netflix");
        post.setAggregateRootId("1");

        AddCommentCommand command = new AddCommentCommand("1", "edws","Carlos", "Hola");

        CommentAdded event = new CommentAdded("edws", "Carlos", "Hola");

        Mockito.when(domainEventRepository.findById(Mockito.any(String.class))).thenReturn(Flux.just(post));
        Mockito.when(domainEventRepository.saveEvent(Mockito.any(CommentAdded.class))).thenReturn(Mono.just(event));

        var use= useCase.apply(Mono.just(command));

        StepVerifier.create(use)
                .expectNext(event)
                .verifyComplete();

        Mockito.verify(domainEventRepository).findById(Mockito.any(String.class));
        Mockito.verify(domainEventRepository).saveEvent(Mockito.any(CommentAdded.class));
        Mockito.verify(eventBus).publish(Mockito.any(CommentAdded.class));
    }



}