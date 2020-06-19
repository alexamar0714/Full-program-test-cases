package ru.otus.l71.behavioral.observer;

/**
 * Created by tully.
 *
 * Abstract observer in the Observer interface.
 */
@FunctionalInterface
public interface Observer {
    void notify(Event event);
}
