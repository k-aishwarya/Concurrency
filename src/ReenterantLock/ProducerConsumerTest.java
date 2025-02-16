package ReenterantLock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProducerConsumerTest {
    public static void main(String[] args) {
        BoundedBuffer<Integer> buffer = new BoundedBuffer<>(5); // Create a buffer with capacity 5
        ExecutorService executor = Executors.newFixedThreadPool(4); // Thread pool for concurrent execution

        // Producer Thread: Adds numbers to the queue
        executor.execute(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    buffer.put(i);
                    Thread.sleep(500); // Simulating production time
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Consumer Thread: Removes numbers from the queue
        executor.execute(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    buffer.take();
                    Thread.sleep(1000); // Simulating consumption time
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executor.shutdown(); // Gracefully shutdown the executor
    }
}

