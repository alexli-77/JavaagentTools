package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Main {
    static final Logger logger = LoggerFactory.getLogger(Main.class);

    static {
        AgentApplication.initialize();
    }

    /**
     * Main method.
     *
     * @param args
     */
    public static void main(String[] args) {
        logger.info("main method invoked with args: {}", Arrays.asList(args));
    }}