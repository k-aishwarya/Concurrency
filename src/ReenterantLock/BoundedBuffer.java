package ReenterantLock;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// Bounded Buffer class implementing a thread-safe queue
class BoundedBuffer<T> {
    private final Queue<T> queue = new LinkedList<>(); // Shared queue
    private final int capacity; // Maximum capacity of the queue

    // Lock for synchronizing access to the queue
    private final Lock lock = new ReentrantLock();

    // Condition variables for managing waiting threads
    private final Condition notFull = lock.newCondition();   // Condition for "Queue is Full"
    private final Condition notEmpty = lock.newCondition();  // Condition for "Queue is Empty"

    public BoundedBuffer(int capacity) {
        this.capacity = capacity;
    }

    // Producer method: Adds an item to the queue
    public void put(T item) throws InterruptedException {
        lock.lock(); // Acquire the lock before modifying the queue
        try {
            // Wait if the queue is full
            while (queue.size() == capacity) {
                System.out.println(Thread.currentThread().getName() + " waiting: Queue is full.");
                notFull.await(); // Releases the lock and waits until space is available
            }

            // Add the item to the queue
            queue.add(item);
            System.out.println(Thread.currentThread().getName() + " produced: " + item);

            // Signal one waiting consumer that an item is available
            notEmpty.signal(); // More efficient than notifyAll()
        } finally {
            lock.unlock(); // Ensure lock is always released
        }
    }

    // Consumer method: Removes an item from the queue
    public T take() throws InterruptedException {
        lock.lock(); // Acquire the lock before accessing the queue
        try {
            // Wait if the queue is empty
            while (queue.isEmpty()) {
                System.out.println(Thread.currentThread().getName() + " waiting: Queue is empty.");
                notEmpty.await(); // Releases the lock and waits until an item is available
            }

            // Remove and return the item from the queue
            T item = queue.poll();
            System.out.println(Thread.currentThread().getName() + " consumed: " + item);

            // Signal one waiting producer that space is available
            notFull.signal(); // More efficient than notifyAll()
            return item;
        } finally {
            lock.unlock(); // Ensure lock is always released
        }
    }
}


