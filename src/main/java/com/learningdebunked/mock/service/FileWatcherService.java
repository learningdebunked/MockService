package com.learningdebunked.mock.service;

import com.learningdebunked.mock.processor.FileProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Kapil
 * @project Mock Service
 */
@Component
public class FileWatcherService {

    // on the server this is the path to the directory where templates are added. any changes to the files on this directory are monitored and published
    @Value("${dir.url}")
    String dirUrl;

    @Autowired
    FileProcessor fileProcessor;

    /**
     * Method that monitors a given director and flush all valid json templates to the in memory database
     */
    public void monitor() {
        try {

            //monitor the files and  call the propogations service
            //validations can all be aspects
            System.out.println("***** Directory being monitored is:********" + dirUrl);
            //TODO checkifDirExists , this can be an aspect
            //TOO another aspect could be if the directory exists and if the mode is dev mode , we need to validate if the files are valid json , if not send an email to the the developer
            //sending an email can also be an after returning aspect , sending an event , either email or slack notification
            if (checkIfFilesExist(dirUrl)) {
                //TODO decide if u want to use Executors.newWorkStealingPool(10) instead of fixed thread pool
                ExecutorService pool = Executors.newFixedThreadPool(10);
                fileProcessor.processFolder(pool, dirUrl);
                //TODO also need to check if its first time setup , if yes and if the production mode is true we can push these templates to production db
                //TODO read the existing templates and publish them into the database.
                //need to lock the directory on the server such that the templates are not modified , otherwise updated templates are not saved or could be saved twice
                //tODO instantiate multiple threads to read all the files and update them into database
                //TODO this should happen only if the profile in which the server is running is a dev profile
                //TODO add profile prod.mode = false

            } else {
                //This gets executed in the profile "prod.mode = true"
                readStream(dirUrl);
            }

        } catch (IOException | InterruptedException e) {
            //TODO unable to read the directory , may be there are file access issues
            e.printStackTrace();
        }
    }

    /**
     * If there are existing templates , in dev mode we want all this to be flushed to the in memory database
     *
     * @param dirUrl
     * @return
     */
    private boolean checkIfFilesExist(String dirUrl) {
        return false;
    }

    /**
     * Method to read the files created, updated or modified in the given directory
     */
    private void readStream(String filePath) throws IOException, InterruptedException {
        WatchService watchService
                = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(filePath);
        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                if (event.context().toString().endsWith(".template")) {
                    fileProcessor.process(filePath,event.context().toString());
                }
            }
            key.reset();
        }
    }
}
