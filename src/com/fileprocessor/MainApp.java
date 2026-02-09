package com.fileprocessor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainApp {

    // Shared resource: A counter to track total files processed
    private static int totalProcessed = 0;

    // Synchronized method to ensure thread-safety when updating the counter
    private static synchronized void incrementCounter() {
        totalProcessed++;
        System.out.println(">>> [LOG] Total files processed so far: " + totalProcessed);
    }

    public static void main(String[] args) {
        // 1. The Buffer: Thread-safe queue to hold file names
        BlockingQueue<String> fileQueue = new LinkedBlockingQueue<>();

        // 2. The Producer: A separate thread that "discovers" files and adds them to the queue
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    String fileName = "Data_File_" + i + ".xml";
                    System.out.println("PRODUCER: Found and queued " + fileName);
                    fileQueue.put(fileName); // Adds to queue (waits if full)
                    Thread.sleep(300); // Simulate discovery delay
                }

                // Poison Pill: Tell the 3 consumer threads to stop when they see this
                for (int i = 0; i < 3; i++) {
                    fileQueue.put("SHUTDOWN");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 3. The Consumer Pool: A Thread Pool with 3 worker threads
        ExecutorService executor = Executors.newFixedThreadPool(3);

        System.out.println("--- System Starting ---");
        producer.start();

        // 4. Submit Consumer Tasks to the Pool
        for (int i = 0; i < 3; i++) {
            executor.execute(() -> {
                while (true) {
                    try {
                        String file = fileQueue.take(); // Retrieve from queue (waits if empty)

                        if (file.equals("SHUTDOWN")) break; // Exit loop if poison pill received

                        System.out.println(Thread.currentThread().getName() + " is processing: " + file);

                        // Simulate the "work" of processing a file
                        Thread.sleep(1500);

                        // Securely update the shared counter
                        incrementCounter();

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }

        // 5. Shutdown sequence
        executor.shutdown();
        try {
            // Wait for threads to finish their final tasks
            if (executor.awaitTermination(1, TimeUnit.MINUTES)) {
                System.out.println("--- All Threads Finished. Final Count: " + totalProcessed + " ---");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}