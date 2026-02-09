package com.fileprocessor;

public class FileProcessor implements Runnable {
    private String fileName;

    public FileProcessor(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + " [START] processing " + fileName);
        try {
            // Simulate processing time (1 second)
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + " [FINISH] " + fileName);
    }
}