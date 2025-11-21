package com.example.collab_code_editor.core.exception;

public class ProjectNotFoundException extends RuntimeException{
    public ProjectNotFoundException (String message){
        super(message);
    }
}
