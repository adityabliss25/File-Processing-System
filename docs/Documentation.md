# Project Documentation: Multi-threaded File Processing System

## 1. Executive Summary
This project demonstrates the implementation of a concurrent file processing system using Java’s Multithreading and Concurrency libraries. The application utilizes a Producer-Consumer architecture to efficiently manage tasks and ensure data integrity through synchronization.

## 2. Technical Requirements Addressed
The following core concepts were successfully implemented:
| Requirement | Implementation Detail |
| Multithreading | Used separate threads for producing tasks and consuming them. |
| Thread Pool | Implemented ExecutorService with a fixed pool of 3 worker threads. |
| Producer-Consumer | Used a BlockingQueue to decouple task discovery from processing. |
| Synchronization | Used the synchronized keyword to protect a shared resource (counter). |

## 3. System Architecture & Pattern
The system is divided into three distinct phases:

**A. The Producer (Task Discovery)**
A dedicated thread simulates "finding" files. It populates a LinkedBlockingQueue with file names. This ensures that the system doesn't have to wait for one file to finish before finding the next.

**B. The Buffer (BlockingQueue)**
The LinkedBlockingQueue acts as the intermediary. It is inherently thread-safe, meaning we don't need manual locks to add or remove items from the queue. It handles the "waiting" logic automatically if the consumers are faster than the producer.

**C. The Consumers (Worker Pool)**
Instead of creating 10 separate threads, we use an ExecutorService to manage 3 reusable worker threads. This optimizes CPU usage and prevents system exhaustion.

## 4. Ensuring Thread Safety
To track the total number of files processed, a shared variable totalProcessed was used. To prevent a Race Condition (where two threads update the count at the same time and lose data), we implemented a synchronized method:
private static synchronized void incrementCounter() {
    totalProcessed++;
}
This ensures that only one thread can increment the counter at any given millisecond, guaranteeing a final count of 10/10.

## 5. Execution Results
Success Rate: 100% (10 files processed).

Graceful Shutdown: Implemented the "Poison Pill" (SHUTDOWN) strategy to ensure all threads closed correctly after the queue was empty.

Concurrency: Observation of console logs confirmed that 3 different threads (pool-1-thread-1, pool-1-thread-2, pool-1-thread-3) worked in parallel.



