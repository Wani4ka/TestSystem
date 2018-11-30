package me.wani4ka.testSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Tester extends Thread {

    private final String name;

    private long time;

    private Verdict verdict = Verdict.TESTING;
    private String message = "Program is starting...";

    public Tester(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        File in = new File("tests/" + name + TesterProperties.testInputExtension);
        if (!in.exists()) {
            verdict = Verdict.INTERNAL;
            message = "Could not find input file.";
            return;
        }
        List<String> input;
        try {
            input = Files.readAllLines(in.toPath());
        } catch (IOException e) {
            verdict = Verdict.INTERNAL;
            message = e.getMessage();
            return;
        }
        File out = new File("tests/" + name + TesterProperties.testOutputExtension);
        if (!out.exists()) {
            verdict = Verdict.INTERNAL;
            message = "Could not find output file.";
            return;
        }
        List<String> output;
        try {
            output = Files.readAllLines(out.toPath());
        } catch (IOException e) {
            verdict = Verdict.INTERNAL;
            message = e.getMessage();
            return;
        }

        File f = new File("input.txt");
        if (f.exists()) f.delete();
        try {
            f.createNewFile();
            Files.write(f.toPath(), input);
        } catch (IOException e) {
            verdict = Verdict.INTERNAL;
            message = e.getMessage();
        }

        long l = System.currentTimeMillis();
        try {
            Process p = Runtime.getRuntime().exec(TesterProperties.programName);
            p.waitFor(TesterProperties.tl, TimeUnit.MILLISECONDS);
            l = System.currentTimeMillis() - l;
            if (p.isAlive()) {
                p.destroyForcibly();
                verdict = Verdict.TL;
                message = "Program tried to work longer that 1 second.";
                return;
            } else if (p.exitValue() != 0){
                verdict = Verdict.RE;
                message = "Program returned exit code " + p.exitValue();
                return;
            }
        } catch (IOException | InterruptedException e) {
            verdict = Verdict.INTERNAL;
            message = e.getMessage();
            return;
        }

        File pout = new File("output.txt");
        if (!pout.exists()) {
            verdict = Verdict.WA;
            message = "File output.txt not found.";
            return;
        }
        List<String> programOutput;
        try {
            programOutput = Files.readAllLines(pout.toPath());
        } catch (IOException e) {
            verdict = Verdict.INTERNAL;
            message = e.getMessage();
            return;
        }
        if (output.size() != programOutput.size()) {
            verdict = Verdict.WA;
            message = "Output size differ: " + output.size() + " expected, " + programOutput.size() + " found.";
            return;
        }
        for (int i = 0; i < output.size(); ++i) {
            if (!output.get(i).equals(programOutput.get(i))) {
                verdict = Verdict.WA;
                message = (i+1) + "st line differ by " + Math.abs(output.get(i).compareTo(programOutput.get(i))) + ".";
                return;
            }
        }
        verdict = Verdict.OK;
        message = "Test passed in " + l + "ms";
        f.delete();
        f = new File("output.txt");
        if (f.exists()) f.delete();
    }

    public Verdict getVerdict() {
        return verdict;
    }

    public String getMessage() {
        return message;
    }
}
