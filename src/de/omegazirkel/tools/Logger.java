package de.omegazirkel.tools;

public class Logger {
    private static final int LEVEL_DEBUG = 0;

    private String prefix = "[OZ.Tools]";
    private int level = LEVEL_DEBUG;

    /**
     * 
     * @param prefix
     * @param level
     */
    public Logger(String prefix, int level) {
        this.prefix = prefix;
        this.level = level;
    }

    /**
     * 
     * @param prefix
     */
    public Logger(String prefix) {
        this.prefix = prefix;
    }

    public Logger() {
    }

    /**
     * 
     * @param message
     */
    public void out(String message) {
        this.out(message, LEVEL_DEBUG);
    }

    /**
     * 
     * @param message
     * @param level
     */
    public void out(String message, int level) {
        if (level >= this.level) {
            System.out.println(prefix + " " + message);
        }
    }
}