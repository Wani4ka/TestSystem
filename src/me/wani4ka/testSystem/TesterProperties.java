package me.wani4ka.testSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class TesterProperties {

    static String testInputExtension = ".in";
    static String testOutputExtension = ".out";
    static String programName = "solution.exe";
    static String inputFile = "input.txt";
    static String outputFile = "output.txt";
    static long tl = 1000;

    private static Properties defaults() {
        Properties prop = new Properties();
        prop.put("test-input-extension", ".in");
        prop.put("test-output-extension", ".out");
        prop.put("tl", 1000);
        prop.put("program-name", "solution.exe");
        prop.put("input-file", "input.txt");
        prop.put("output-file", "output.txt");
        return prop;
    }

    static void init() throws IOException {
        File f = new File("tester.properties");
        if (!f.exists()) create();
        Properties prop = new Properties(defaults());
        prop.load(new FileInputStream(f));
        testInputExtension = prop.getProperty("test-input-extension");
        testOutputExtension = prop.getProperty("test-output-extension");
        tl = Long.parseLong(prop.getProperty("tl"));
        programName = prop.getProperty("program-name");
        inputFile = prop.getProperty("input-file");
        outputFile = prop.getProperty("output-file");
        create();
    }

    private static void create() throws IOException {
        File f = new File("tester.properties");
        if (f.exists()) f.delete();
        f.createNewFile();
        Properties prop = new Properties();
        prop.setProperty("test-input-extension", testInputExtension);
        prop.setProperty("test-output-extension", testOutputExtension);
        prop.setProperty("tl", String.valueOf(tl));
        prop.setProperty("program-name", programName);
        prop.setProperty("input-file", inputFile);
        prop.setProperty("output-file", outputFile);
        prop.store(new FileOutputStream(f), "TestSystem properties");
    }
}
