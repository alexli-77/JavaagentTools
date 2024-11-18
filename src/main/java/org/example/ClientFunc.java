package org.example;

import lombok.NoArgsConstructor;

import java.util.ArrayList;

@NoArgsConstructor
public class ClientFunc {
    public boolean sampleA (int i) {
        ArrayList<Integer> list = new ArrayList<>();
        return list.add(i);
    }

    public int sampleB (int i) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(i);
        return list.size();
    }

    public int sampleC (int i) {
        ArrayList<Integer> list = new ArrayList<>();
        list.add(i);
        list.remove(0);
        return list.size();
    }
}
