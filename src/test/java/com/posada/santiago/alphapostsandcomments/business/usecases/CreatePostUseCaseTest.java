package com.posada.santiago.alphapostsandcomments.business.usecases;

import com.posada.santiago.alphapostsandcomments.business.gateways.DomainEventRepository;
import com.posada.santiago.alphapostsandcomments.business.gateways.EventBus;
import com.posada.santiago.alphapostsandcomments.domain.commands.CreatePostCommand;
import com.posada.santiago.alphapostsandcomments.domain.events.PostCreated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class CreatePostUseCaseTest {

    @Mock
    DomainEventRepository domainEventRepository;

    @Mock
    EventBus eventBus;

    CreatePostUseCase useCase;

    @BeforeEach
    void init(){
        useCase = new CreatePostUseCase(domainEventRepository,eventBus);
    }

    @Test
    void createPostTest(){
        CreatePostCommand postCommand = new CreatePostCommand("1", "Carlos","BlackMirror");

        PostCreated postCreated = new PostCreated("BlackMirror", "Carlos");

        Mockito.when(domainEventRepository.saveEvent(Mockito.any(PostCreated.class))).thenReturn(Mono.just(postCreated));

        var use = useCase.apply(Mono.just(postCommand));

        StepVerifier.create(use)
                //.expectNext(postCreated)
                .expectNextMatches(event -> event instanceof PostCreated)
                .verifyComplete();
    }
}