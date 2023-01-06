package net.cassite.hottapcassistant.discharge;

import java.util.LinkedList;

public class Stabilizer {
    private static final int SAME_COUNT = 6;
    private final LinkedList<Integer> counts = new LinkedList<>();

    public int add(int count) {
        counts.add(count);
        if (counts.size() < SAME_COUNT) {
            return -1;
        }
        while (counts.size() > SAME_COUNT) {
            counts.removeFirst();
        }
        var ite = counts.iterator();
        var first = ite.next();
        while (ite.hasNext()) {
            var n = ite.next();
            if (n.intValue() != first.intValue()) {
                return -1;
            }
        }
        return first;
    }

    public void reset() {
        counts.clear();
    }
}
