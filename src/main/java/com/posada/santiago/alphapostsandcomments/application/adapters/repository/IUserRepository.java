package com.posada.santiago.alphapostsandcomments.application.adapters.repository;

import com.posada.santiago.alphapostsandcomments.application.generic.models.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


public interface IUserRepository extends ReactiveMongoRepository<User,String> {
    Mono<User> findByUsername(String username);
}
