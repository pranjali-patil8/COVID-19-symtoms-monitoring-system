package com.example.assignment_1;

import java.util.ArrayList;

public class InvokeZeroCrossing {

    public static int invoke_zeroCrossing(Float[] div){

        //calculate differential and then invoke zero crossing
        ArrayList<Float> diff = new ArrayList<>();
        for (int i = 0; i < div.length-1; i++) {
            diff.add((div[i] - div[i+1]));
        }

        int numberOfZeroCrossings = Zero_Crossing.zeroCrossing(diff);
        return numberOfZeroCrossings;
    }
}
