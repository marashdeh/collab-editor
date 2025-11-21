package com.example.collab_code_editor.core.util;


public class ColorUtil {

    private static final String[] COLORS = {
            "#FF4C4C", "#4C8CFF", "#4CFF72", "#FFB84C", "#B84CFF",
            "#FF4CE0", "#4CFFD9", "#FFD24C", "#FF6F91", "#6F4CFF"
    };

    public static String generateColor(Long userId) {
        int index = (int) (userId % COLORS.length);
        return COLORS[index];
    }
}