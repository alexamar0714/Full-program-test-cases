package ru.otus.l142;

/**
 * @author v.chibrikov
 */
public class StateObject {
    private int i;

    public synchronized void increment() {
            i++;
    }

    public int getI() {
        return i;
    }
}
