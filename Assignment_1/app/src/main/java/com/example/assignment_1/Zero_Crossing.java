package com.example.assignment_1;


import java.util.ArrayList;

public class Zero_Crossing {

    public static int zeroCrossing(ArrayList<Float> diff) {

        ArrayList<Integer> zero_cross = new ArrayList<>();
        for (int i = 0; i < diff.size() - 1; i++) {
            zero_cross.add(f(diff.get(i), diff.get(i + 1)));
        }

        int sum = 0;
        for (int i = 0; i < zero_cross.size(); i++){
            sum += zero_cross.get(i);
        }

        return sum;
    }

    public static int f(float x, float y) {
        if (x * y < 0) return 1;
        else return 0;
    }
}

