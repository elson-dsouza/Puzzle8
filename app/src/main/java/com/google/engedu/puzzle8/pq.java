package com.google.engedu.puzzle8;

/**
 * Reference: http://algs4.cs.princeton.edu/24pq/ (As per Preperation for Unit 5)
 */


import java.util.NoSuchElementException;

public class MinPQ {
    private PuzzleBoard[] pq;
    private int N;

    public MinPQ (int initCapacity)
    {
        pq =  new PuzzleBoard[initCapacity +1];
        N=0;
    }

    public MinPQ(){
        this(1);
    }

    /**
     * Adds a new key to this priority queue.
     *
     * @param  x the key to add to this priority queue
     */
    public void add(PuzzleBoard x) {
        // double size of array if necessary
        if (N == pq.length - 1) resize(2 * pq.length);

        // add x, and percolate it up to maintain heap invariant
        pq[++N] = x;
        swim(N);
        assert isMinHeap();
    }

    /**
     * Removes and returns a smallest key on this priority queue.
     *
     * @return a smallest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public PuzzleBoard poll() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        swap(1, N);
        PuzzleBoard min = pq[N--];
        sink(1);
        pq[N+1] = null;         // avoid loitering and help with garbage collection
        if ((N > 0) && (N == (pq.length - 1) / 4)) resize(pq.length  / 2);
        assert isMinHeap();
        return min;
    }

    // helper function to double the size of the heap array
    private void resize(int capacity) {
        assert capacity > N;
        PuzzleBoard[] temp = new PuzzleBoard[capacity];
        for (int i = 1; i <= N; i++) {
            temp[i] = pq[i];
        }
        pq = temp;
    }

    //helper function to restore heap invariants
    private void swim(int k) {
        while (k > 1 && greater(k/2, k)) {
            swap(k, k / 2);
            k = k/2;
        }
    }

    private void sink(int k) {
        while (2*k <= N) {
            int j = 2*k;
            if (j < N && greater(j, j+1)) j++;
            if (!greater(k, j)) break;
            swap(k, j);
            k = j;
        }
    }


    //Helper function to compare puzzle states, swap and check heap condition
    private boolean greater(int i, int j) {

        return compare(pq[i], pq[j]) > 0;

    }

    private void swap(int i, int j) {
        PuzzleBoard swap = pq[i];
        pq[i] = pq[j];
        pq[j] = swap;
    }

    // is pq[1..N] a min heap?
    private boolean isMinHeap() {
        return isMinHeap(1);
    }

    // is subtree of pq[1..N] rooted at k a min heap?
    private boolean isMinHeap(int k) {
        if (k > N) return true;
        int left = 2*k, right = 2*k + 1;
        if (left  <= N && greater(k, left))  return false;
        if (right <= N && greater(k, right)) return false;
        return isMinHeap(left) && isMinHeap(right);
    }


    /**
     * returns true if queue is empty
     * @return
     */
    public boolean isEmpty() {
        return N == 0;
    }

    public int size(){
        return N;
    }

    /**
     * Returns a smallest key on this priority queue.
     *
     * @return a smallest key on this priority queue
     * @throws NoSuchElementException if this priority queue is empty
     */
    public PuzzleBoard peek() {
        if (isEmpty()) throw new NoSuchElementException("Priority queue underflow");
        return pq[1];
    }

    /**
     *  Compares two puzzle states and determines which is of higher priority
     * @param lhs
     * @param rhs
     * @return
     */
    public int compare(PuzzleBoard lhs, PuzzleBoard rhs) {
        if(lhs.dist() == rhs.dist())
        {
            return lhs.compareStates(rhs);
        }
        else if(lhs.priority()<rhs.priority())
            return -1;
        else
            return  1;

    }
}