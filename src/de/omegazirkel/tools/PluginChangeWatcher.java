/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.omegazirkel.tools;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Maik *Devidian* Laschober
 */
public class PluginChangeWatcher {

    private final WatchService watcher;
    private final Map keyPaths = new ConcurrentHashMap();
    private volatile Thread processingThread;
    private final FileChangeListener fcl;

    /**
     * 
     * @param fcl
     * @throws IOException 
     */
    public PluginChangeWatcher(FileChangeListener fcl) throws IOException {
        this.fcl = fcl;
        this.watcher = FileSystems.getDefault().newWatchService();
        System.out.println("[OZT] Start WatchUpdates");
    }

    /**
     * 
     */
    public void startListening() {
        processingThread = new Thread() {
            @Override
            public void run() {
                try {
                    processFileNotifications();
                } catch (InterruptedException ex) {
                    System.out.println("[OZT] EX " + ex.getMessage());
                    processingThread = null;
                }
            }
        };

        processingThread.start();
    }

    /**
     * 
     */
    public void shutDownListener() {
        Thread thr = processingThread;
        if (thr != null) {
            thr.interrupt();
        }
    }

    /**
     * 
     * @param dir
     * @throws IOException 
     */
    public void watchDir(File dir) throws IOException {
        Path p = dir.toPath();
        System.out.println("[OZT] register " + p);
        WatchKey key = p.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keyPaths.put(key, p);
    }

    /**
     * 
     * @throws InterruptedException 
     */
    private void processFileNotifications() throws InterruptedException {
        while (true) {
            WatchKey key = watcher.take();
            Path dir = (Path) keyPaths.get(key);
            key.pollEvents().forEach((evt) -> {
                WatchEvent.Kind eventType = evt.kind();
                if (!(eventType == OVERFLOW)) {
                    Object o = evt.context();
                    if (o instanceof Path) {
                        Path path = (Path) o;
                        process(dir, path, eventType);
                    }
                }
            });
            key.reset();
        }
    }

    /**
     * 
     * @param dir
     * @param file
     * @param evtType 
     */
    private void process(Path dir, Path file, WatchEvent.Kind evtType) {
        if(evtType == ENTRY_MODIFY){
            fcl.onFileChangeEvent(file.toString());
        }
    }
}
