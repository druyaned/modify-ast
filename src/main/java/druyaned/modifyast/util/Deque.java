package druyaned.modifyast.util;

/**
 * Implementation of the {@link java.util.Deque Deque}.
 * 
 * <P><i>Important Note</i><br>
 * The structure implementation is based on the array with an immutable {@code capacity}.
 * 
 * <P><i>Example#1</i><pre>
 *  # 5 2 7 1 6 # #
 *    ^       ^
 *  head     tail
 * </pre>
 * <i>Example#2</i><pre>
 *  7 1 6 # # # 5 2
 *      ^       ^
 *    tail     head
 * </pre>
 * 
 * @author druyaned
 * @param <T> the type of value maintained by the list
 */
public class Deque<T> {
    
    /** Default capacity for constructor with no arguments. */
    public static final int DEFAULT_CAPACITY = (1 << 15);
    
    private final int capacity;
    private final Object[] values;
    private int size;
    private int head, tail;
    
    /**
     * Creates a new empty deque with a {@link DEFAULT_CAPACITY default capacity}.
     */
    public Deque() {
        this(DEFAULT_CAPACITY);
    }
    
    /**
     * Creates a new empty deque.
     * @param capacity immutable capacity of the data array
     */
    public Deque(int capacity) {
        this.capacity = capacity;
        values = new Object[capacity];
        size = 0;
        head = tail = 0;
    }
    
    /**
     * Returns capacity of the deque.
     * @return capacity of the deque
     */
    public int capacity() {
        return capacity;
    }
    
    /**
     * Returns size of the deque.
     * @return size of the deque
     */
    public int size() {
        return size;
    }
    
    /**
     * Returns {@code true} if the deque is empty, otherwise - {@code false}.
     * @return {@code true} if the deque is empty, otherwise - {@code false}
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Returns value which is placed after the head by {@code i} positions
     * (head's position is {@code 0}). So {@code deque.get(0)} returns head
     * of the deque.
     * 
     * @param i to move forward from the head
     * @return value which is placed after the head by {@code i} positions
     *      (head's position is {@code 0})
     */
    public T get(int i) {
        checkIndex(i);
        return (T)values[relativeIndex(i)];
    }
    
    /**
     * Returns head of the deque.
     * @return head of the deque
     */
    public T getFirst() {
        checkIndex(0);
        return (T)values[head];
    }
    
    /**
     * Returns tail of the deque.
     * @return tail of the deque
     */
    public T getLast() {
        checkIndex(size - 1);
        return (T)values[tail];
    }
    
    /**
     * Sets value which is placed after the head by {@code i} positions
     * (head's position is {@code 0}). So deque.set(0, value) sets the head.
     * 
     * @param i to shift forward from the head
     * @param value to be set
     */
    public void set(int i, T value) {
        checkIndex(i);
        values[relativeIndex(i)] = value;
    }
    
    /**
     * Clears the whole deque. The complexity of the method is O(n).
     */
    public void clear() {
        for (int i = 0; i < size; i++) {
            values[i] = null;
        }
        size = head = tail = 0;
    }
    
    /**
     * Sets size, head and tail to zero.
     * The complexity of the method is O(1).
     */
    public void clearSimple() {
        size = head = tail = 0;
    }
    
    /**
     * Attaches the value to the head of the deque.
     * @param value to be attached
     * @return {@code true} if the attachments was succeeded, otherwise - {@code false}
     */
    public boolean addFirst(T value) {
        if (size == capacity) {
            return false;
        }
        if (size == 0) {
            values[head = tail = 0] = value;
        } else if (head == 0) {
            values[head = capacity - 1] = value;
        } else {
            values[--head] = value;
        }
        size++;
        return true;
    }
    
    /**
     * Attaches the value to the tail of the deque.
     * @param value to be attached
     * @return {@code true} if the attachments was succeeded, otherwise - {@code false}
     */
    public boolean addLast(T value) {
        if (size == capacity) {
            return false;
        }
        if (size == 0) {
            values[head = tail = 0] = value;
        } else if (tail == capacity - 1) {
            values[tail = 0] = value;
        } else {
            values[++tail] = value;
        }
        size++;
        return true;
    }
    
    /**
     * Attaches the value to the head of the deque.
     * @param value to be attached
     */
    public void pushFirst(T value) {
        throwIfOversize();
        if (size == 0) {
            values[head = tail = 0] = value;
        } else if (head == 0) {
            values[head = capacity - 1] = value;
        } else {
            values[--head] = value;
        }
        size++;
    }
    
    /**
     * Attaches the value to the tail of the deque.
     * @param value to be attached
     */
    public void pushLast(T value) {
        throwIfOversize();
        if (size == 0) {
            values[head = tail = 0] = value;
        } else if (tail == capacity - 1) {
            values[tail = 0] = value;
        } else {
            values[++tail] = value;
        }
        size++;
    }
    
    /**
     * Detaches the head of the deque.
     * @return {@code true} if the detaching was succeeded, otherwise - {@code false}
     */
    public boolean removeFirst() {
        if (size == 0) {
            return false;
        }
        values[head] = null;
        if (size == 1) {
            head = tail = 0;
        } else if (head == capacity - 1) {
            head = 0;
        } else {
            head++;
        }
        size--;
        return true;
    }
    
    /**
     * Detaches the tail of the deque.
     * @return {@code true} if the detaching was succeeded, otherwise - {@code false}
     */
    public boolean removeLast() {
        if (size == 0) {
            return false;
        }
        values[tail] = null;
        if (size == 1) {
            head = tail = 0;
        } else if (tail == 0) {
            tail = capacity - 1;
        } else {
            tail--;
        }
        size--;
        return true;
    }
    
    /**
     * Detaches and returns the head of the deque.
     * @return detached head of the deque
     */
    public T popFirst() {
        throwIfEmpty();
        T first = (T)values[head];
        values[head] = null;
        if (size == 1) {
            head = tail = 0;
        } else if (head == capacity - 1) {
            head = 0;
        } else {
            head++;
        }
        size--;
        return first;
    }
    
    /**
     * Detaches and returns the tail of the deque.
     * @return detached tail of the deque
     */
    public T popLast() {
        throwIfEmpty();
        T last = (T)values[tail];
        values[tail] = null;
        if (size == 1) {
            head = tail = 0;
        } else if (tail == 0) {
            tail = capacity - 1;
        } else {
            tail--;
        }
        size--;
        return last;
    }
    
    private int relativeIndex(int i) {
        return head <= tail || i < capacity - head ? head + i : i - (capacity - head);
    }
    
    private void checkIndex(int i) {
        if (i < 0 || i >= size) {
            throw new IndexOutOfBoundsException(String.format("index=%d size=%d", i, size));
        }
    }
    
    private void throwIfOversize() {
        if (size == capacity) {
            throw new IllegalStateException("deque is full");
        }
    }
    
    private void throwIfEmpty() {
        if (size == 0) {
            throw new IllegalStateException("deque is empty");
        }
    }
    
}
