package com.example.assignment_1;



import java.util.ArrayList;

public class Rolling_Mean {

    public static ArrayList rollingMean(ArrayList<Float> bitmapArray) {
        int size = 3;
        ArrayList<Float> rolling_mean = new ArrayList<>();

        for (int i = 0; i + size <= bitmapArray.size(); i++) {
            float sum = 0;
            for (int j = i; j < i + size; j++) {
                float temp = (float) bitmapArray.get(j);
                sum += temp;
            }

            float average = sum / size;
            rolling_mean.add(average);
        }
        return rolling_mean;
    }
}
