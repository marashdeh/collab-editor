package com.example.collab_code_editor.infrastructure.serviceImpl;

import com.example.collab_code_editor.core.dto.ExecuteRequest;
import com.example.collab_code_editor.core.dto.ExecuteResponse;
import com.example.collab_code_editor.core.service.CompilerService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets; // ✅ Import this
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CompilerServiceImpl implements CompilerService {

    @Override
    public ExecuteResponse execute(ExecuteRequest request) {
        try {
            switch (request.getLanguage().toLowerCase()) {
                case "java": return runJava(request.getCode());
                case "python": return runPython(request.getCode());
                case "cpp": return runCpp(request.getCode());
                default: return new ExecuteResponse("Unsupported language: " + request.getLanguage(), true);
            }
        } catch (Exception e) {
            return new ExecuteResponse("Server Error: " + e.getMessage(), true);
        }
    }

    // --- PYTHON STRATEGY ---
    private ExecuteResponse runPython(String code) throws Exception {
        Path tempFile = Files.createTempFile("script", ".py");
        Files.writeString(tempFile, code);

        ProcessBuilder pb = new ProcessBuilder("python", tempFile.toString());

        pb.environment().put("PYTHONIOENCODING", "utf-8");

        return runProcess(pb);
    }

    // --- JAVA STRATEGY ---
    private ExecuteResponse runJava(String code) throws Exception {
        Path tempDir = Files.createTempDirectory("javacode");
        Path sourceFile = tempDir.resolve("Main.java");
        Files.writeString(sourceFile, code);

        // 1. Compile
        ProcessBuilder compilePb = new ProcessBuilder("javac", "-encoding", "UTF-8", sourceFile.toString());
        ExecuteResponse compileResult = runProcess(compilePb);

        if (compileResult.isError() && !compileResult.getOutput().isEmpty()) {
            return compileResult;
        }

        // 2. Run
        ProcessBuilder runPb = new ProcessBuilder("java", "-Dfile.encoding=UTF-8", "-cp", tempDir.toString(), "Main");
        return runProcess(runPb);
    }

    // --- C++ STRATEGY ---
    private ExecuteResponse runCpp(String code) throws Exception {
        Path tempDir = Files.createTempDirectory("cppcode");
        Path sourceFile = tempDir.resolve("main.cpp");

        String exeName = System.getProperty("os.name").toLowerCase().contains("win") ? "main.exe" : "./main";
        Path exeFile = tempDir.resolve(exeName);

        Files.writeString(sourceFile, code);

        ProcessBuilder compilePb = new ProcessBuilder("g++", sourceFile.toString(), "-o", exeFile.toString());
        ExecuteResponse compileResult = runProcess(compilePb);

        if (compileResult.isError() && !compileResult.getOutput().isEmpty()) {
            return compileResult;
        }

        ProcessBuilder runPb = new ProcessBuilder(exeFile.toString());
        runPb.directory(tempDir.toFile());
        return runProcess(runPb);
    }

    private ExecuteResponse runProcess(ProcessBuilder pb) {
        try {
            pb.redirectErrorStream(true);
            Process process = pb.start();

            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                process.destroy();
                return new ExecuteResponse("Error: Time Limit Exceeded (5s)", true);
            }

            String output = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))
                    .lines().collect(Collectors.joining("\n"));

            return new ExecuteResponse(output, process.exitValue() != 0);

        } catch (Exception e) {
            return new ExecuteResponse("Execution Failed: " + e.getMessage(), true);
        }
    }
}