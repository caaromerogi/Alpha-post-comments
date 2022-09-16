package com.posada.santiago.alphapostsandcomments.application.handlers;


import com.posada.santiago.alphapostsandcomments.application.generic.models.AuthenticationRequest;
import com.posada.santiago.alphapostsandcomments.application.generic.models.User;
import com.posada.santiago.alphapostsandcomments.application.generic.models.usecases.CreateUserUseCase;
import com.posada.santiago.alphapostsandcomments.application.generic.models.usecases.LoginUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class AuthHandler {

    private final static Logger logger= LoggerFactory.getLogger(AuthHandler.class);
    @Bean
    RouterFunction<ServerResponse> createUser(CreateUserUseCase createUserUseCase){
        return route(POST("/auth/save/{role}"),
                request -> request.bodyToMono(User.class)
                        .flatMap(user -> request.pathVariable("role").equals("admin")?
                                createUserUseCase.save(user,"ROLE_ADMIN"):
                                createUserUseCase.save(user,"ROLE_USER"))
                        .flatMap(user -> ServerResponse.status(HttpStatus.CREATED).bodyValue(user))
                        .onErrorResume(error-> {
                            logger.error(error.getMessage());
                            return ServerResponse.badRequest().build();
                        }));

    }

    @Bean
    RouterFunction<ServerResponse> loginRouter(LoginUseCase loginUseCase){
        return route(POST("/auth/login"),
                request -> loginUseCase.logIn(request.bodyToMono(AuthenticationRequest.class))
                        .onErrorResume(error -> {
                            logger.error(error.getMessage());
                            return ServerResponse.badRequest().build();
                        })


                /*.onErrorResume(throwable -> ServerResponse.status(HttpStatus.FORBIDDEN).build())*/);
    }


}
