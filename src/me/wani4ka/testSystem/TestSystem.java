package me.wani4ka.testSystem;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

public class TestSystem {
    public static void main(String[] args) throws IOException, InterruptedException {

        File out = new File("results.txt");
        if (out.exists()) out.delete();
        out.createNewFile();
        System.setOut(new PrintStream(out));

        System.err.println("Loading tester properties");
        TesterProperties.init();

        System.err.println("Looking for solution program");
        File f = new File(TesterProperties.programName);
        if (!f.exists()) {
            System.err.println("Could not find solution program");
            return;
        }

        System.err.println("Looking for test files");
        Set<String> tests = new TreeSet<>();
        try (Stream<Path> paths = Files.walk(Paths.get("tests"))) {
            paths.filter(i -> Files.isRegularFile(i) && Files.isReadable(i) && (i.getFileName().toString().endsWith(TesterProperties.testInputExtension) || i.getFileName().toString().endsWith(TesterProperties.testOutputExtension)))
                    .forEach(i -> tests.add(getName(i.getFileName().toString())));
        }
        int passed = 0;
        int all = 0;
        File input = new File("input.txt");
        File output = new File("output.txt");
        for (String s : tests) {
            System.out.println();
            System.out.println("###### " + s + " ######");
            System.err.println("Running test " + s);
            Tester t = new Tester(s);
            t.run();
            t.join();
            System.out.println("Verdict: " + t.getVerdict());
            System.out.println("Message: " + t.getMessage());
            if (t.getVerdict() != Verdict.INTERNAL) ++all;
            if (t.getVerdict() == Verdict.OK) ++passed;
            if (input.exists()) input.delete();
            if (output.exists()) output.delete();
        }
        System.out.println();
        System.out.println(passed + " / " + all + " tests passed.");
        System.err.println(passed + " / " + all + " tests passed.");
    }

    private static String getName(String fileName) {
        return fileName.replaceFirst("[.][^.]+$", "");
    }
}
