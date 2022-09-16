package com.posada.santiago.alphapostsandcomments.domain.commands;

import co.com.sofka.domain.generic.Command;

public class DeleteCommentCommand extends Command {

    private String postId;

    private String commentId;

    public DeleteCommentCommand(){}

    public DeleteCommentCommand(String postId, String commentId) {
        this.postId = postId;
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public String getCommentId() {
        return commentId;
    }

}
