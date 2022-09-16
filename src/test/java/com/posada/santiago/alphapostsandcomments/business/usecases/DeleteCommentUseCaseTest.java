package com.posada.santiago.alphapostsandcomments.business.usecases;

import com.posada.santiago.alphapostsandcomments.business.gateways.DomainEventRepository;
import com.posada.santiago.alphapostsandcomments.business.gateways.EventBus;
import com.posada.santiago.alphapostsandcomments.domain.commands.DeleteCommentCommand;
import com.posada.santiago.alphapostsandcomments.domain.events.CommentAdded;
import com.posada.santiago.alphapostsandcomments.domain.events.CommentDeleted;
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
class DeleteCommentUseCaseTest {
    @Mock
    DomainEventRepository domainEventRepository;

    @Mock
    EventBus eventBus;

    DeleteCommentUseCase useCase;

    @BeforeEach
    void init(){
        useCase = new DeleteCommentUseCase(domainEventRepository,eventBus);
    }

    @Test
    void deleteCommentUseCaseTest(){
        PostCreated post = new PostCreated("BlackMirror", "Netflix");

        CommentAdded comment = new CommentAdded("rsd", "Carlos", "Hi");

        DeleteCommentCommand command = new DeleteCommentCommand("1", "rsd");

        CommentDeleted event = new CommentDeleted("rsd");

        Mockito.when(domainEventRepository.findById(Mockito.any(String.class))).thenReturn(Flux.just(post,comment));
        Mockito.when(domainEventRepository.saveEvent(Mockito.any(CommentDeleted.class))).thenReturn(Mono.just(event));

        var use = useCase.apply(Mono.just(command));

        StepVerifier.create(use)
                .expectNext(event)
                .verifyComplete();

        Mockito.verify(domainEventRepository).findById(Mockito.any(String.class));
        Mockito.verify(domainEventRepository).saveEvent(Mockito.any(CommentDeleted.class));
        Mockito.verify(eventBus).publish(Mockito.any(CommentDeleted.class));

        

    }
}