package com.n;

import gnu.trove.TIntIntHashMap;
import java.util.ArrayList;
import java.util.List;

public class NProcessor {
    private final List<DetectionN> active = new ArrayList<>(200);

    private void calculateActive(List<DetectionN> ns) {
        active.clear();
        for (int i = 0; i < ns.size(); i++) {
            DetectionN n = ns.get(i);
            if (n.isActive()) {
                active.add(n);
            }
        }
    }

    private void processActiveNs() {
        TIntIntHashMap tempHashSet = new TIntIntHashMap();
        for (DetectionN n : active) {

        }
    }

    public void process(List<DetectionN> ns) {
        calculateActive(ns);
        processActiveNs();
    }
}
