package com.example.collab_code_editor.core.exception;

public class FolderNotFoundException extends RuntimeException{
    public FolderNotFoundException (String message){
        super(message);
    }
}
