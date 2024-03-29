package ru.otus.l62.b1.strategy;

import java.util.List;

/**
 * Created by tully.
 *
 * Abstract algorithm in the Strategy pattern
 *
 */
public interface Algorithm<T> {
    void sort(List<T> list);
}
