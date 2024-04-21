package com.example.myapplication;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CGC {
    private Map<String, List<String>> rules;
    public Set<String> facts;

    public CGC() {
        rules = new HashMap<>();
        facts = new HashSet<>();
    }
    public void addRule(String conclusion, List<String> conditions) {
        rules.put(conclusion, conditions);
    }

    public void addFact(String fact) {
        facts.add(fact);
    }

    public void executeForwardChaining() {
        boolean newFactAdded;

        do {
            newFactAdded = false;

            for (Map.Entry<String, List<String>> entry : rules.entrySet()) {
                String conclusion = entry.getKey();
                List<String> conditions = entry.getValue();

                if (conditions.stream().allMatch(facts::contains) && !facts.contains(conclusion)) {
                    facts.add(conclusion);
                    newFactAdded = true;
                }
            }
        } while (newFactAdded);
    }
}
