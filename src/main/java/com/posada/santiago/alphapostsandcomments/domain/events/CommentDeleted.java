package com.posada.santiago.alphapostsandcomments.domain.events;

import co.com.sofka.domain.generic.DomainEvent;

public class CommentDeleted extends DomainEvent {

    private String commentId;


    public CommentDeleted(String commentId){
        super("posada.santiago.postdeleted");
        this.commentId = commentId;
    }

    public String getCommentId() {
        return commentId;
    }
}
