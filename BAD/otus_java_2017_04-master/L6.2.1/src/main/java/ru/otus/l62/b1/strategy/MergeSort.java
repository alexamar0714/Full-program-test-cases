package ru.otus.l62.b1.strategy;

import java.util.List;

/**
 * Created by tully.
 */
public class MergeSort<T> implements Algorithm<T> {
    @Override
    public void sort(List<T> list) {
        System.out.println("Merge sort");
    }
}