package net.cassite.hottapcassistant.discharge;

import java.util.LinkedList;

public class Stabilizer {
    private final LinkedList<DischargeCheckAlgorithm.DischargeCheckResult> results = new LinkedList<>();
    private boolean fullCharge;
    private double lastMax;
    private int sleepTime = 100;
    private int discardCount = 0;

    public synchronized void add(DischargeCheckAlgorithm.DischargeCheckResult result) {
        if (discardCount > 0) {
            --discardCount;
            return;
        }
        if (result.p() > lastMax) {
            lastMax = result.p();
        }
        results.add(result);
        while (results.size() > 5) {
            results.removeFirst();
        }
        if (fullCharge) {
            return;
        }
        if (result.p() > 0.75) {
            sleepTime = 20;
        }
        var fcCount = 0;
        for (var r : results) {
            if (isFullCharge(r)) {
                ++fcCount;
            }
        }
        if (fcCount == results.size()) {
            fullCharge = true;
            lastMax = 0;
            sleepTime = 100;
        }
    }

    @SuppressWarnings("RedundantIfStatement")
    private boolean isFullCharge(DischargeCheckAlgorithm.DischargeCheckResult r) {
        if (r.isFullCharge() != null && r.isFullCharge()) {
            return true;
        }
        if (lastMax > 0.6 && r.p() < 0.25) {
            return true;
        }
        return false;
    }

    public boolean isFullCharge() {
        return fullCharge;
    }

    public synchronized void discharge() {
        fullCharge = false;
        sleepTime = 100;
        discardCount = 5;
        lastMax = 0;
        results.clear();
    }

    public void reset() {
        results.clear();
        fullCharge = false;
        lastMax = 0;
        sleepTime = 100;
        discardCount = 0;
    }

    public double getLastMax() {
        return lastMax;
    }

    public int getSleepTime() {
        return sleepTime;
    }
}
