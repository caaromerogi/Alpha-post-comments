package com.posada.santiago.alphapostsandcomments.application.handlers;


import co.com.sofka.domain.generic.DomainEvent;
import com.posada.santiago.alphapostsandcomments.business.usecases.AddCommentUseCase;
import com.posada.santiago.alphapostsandcomments.business.usecases.CreatePostUseCase;
import com.posada.santiago.alphapostsandcomments.business.usecases.DeleteCommentUseCase;
import com.posada.santiago.alphapostsandcomments.domain.commands.AddCommentCommand;
import com.posada.santiago.alphapostsandcomments.domain.commands.CreatePostCommand;
import com.posada.santiago.alphapostsandcomments.domain.commands.DeleteCommentCommand;
import com.posada.santiago.alphapostsandcomments.domain.events.CommentDeleted;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class CommandHandle {

    private final static Logger logger= LoggerFactory.getLogger(CommandHandle.class);

    @Bean
    public RouterFunction<ServerResponse> createPost(CreatePostUseCase useCase){

        return route(
                POST("/create/post").and(accept(MediaType.APPLICATION_JSON)),
                request -> useCase.apply(request.bodyToMono(CreatePostCommand.class))
                        .collectList()
                        .flatMap(domainEvents ->{
                            logger.info("Post successfully created");
                            return  ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(domainEvents);
                        })
                        .onErrorResume(error -> {
                            logger.error(error.getMessage());
                            return ServerResponse.badRequest().build();
                        })
        );
    }

    @Bean
    public RouterFunction<ServerResponse> addComment(AddCommentUseCase useCase){

        return route(
                POST("/add/comment").and(accept(MediaType.APPLICATION_JSON)),
                request -> useCase.apply(request.bodyToMono(AddCommentCommand.class))
                        .collectList()
                        .flatMap(domainEvents ->{
                            logger.info("Comment successfully added");
                            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                    .bodyValue(domainEvents);
                        } )
                        .onErrorResume(error ->{
                            logger.error(error.getMessage());
                            return ServerResponse.badRequest().build();
                        })
        );
    }


    @Bean
    public RouterFunction<ServerResponse> deleteComment(DeleteCommentUseCase useCase){
        return route(
                DELETE("/delete/comment").and(accept(MediaType.APPLICATION_JSON)),
                request -> useCase.apply(request.bodyToMono(DeleteCommentCommand.class))
                            .collectList()
                            .flatMap(e -> {
                                if (e.isEmpty()) {
                                    return ServerResponse.notFound().build();
                                }
                                return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(BodyInserters.fromValue(e.get(0)));
                            })
        );
    }
}
