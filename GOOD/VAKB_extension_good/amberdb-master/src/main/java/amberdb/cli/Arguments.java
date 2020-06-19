package amberdb.cli;

import java.util.Iterator;
import java.util.List;

class Arguments implements Iterable<String> {
    final List<String> list;

    Arguments(List<String> list) {
        this.list = list;
    }

    @Override
    public Iterator<String> iterator() {
        return list.iterator();
    }

    boolean isEmpty() {
        return list.isEmpty();
    }

    String first() {
        return list.get(0);
    }

    Arguments rest() {
        return new Arguments(list.subList(1, list.size()));
    }
}