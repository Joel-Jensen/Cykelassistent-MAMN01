package se.lth.MAMN01.cykel_assistent;

import android.util.Log;

import java.util.LinkedList;
import java.util.List;

public class Speedometer {

    public static final int ABOVE_THRESHOLD = 1;
    public static final int BELOW_THRESHOLD = -1;
    public static final int WITHIN_THRESHOLD = 0;
    public static final int STANDING_STILL_THRESHOLD = 2;
    private List<Double> samples;
    private int NUMBER_OF_SAMPLES = 10;
    private double lowestLimit = 0;
    private double highestLimit = 0;

    public Speedometer(double lowestLimit, double highestLimit) {
        samples = new LinkedList<>();
    }

    public int onSpeedUpdate(double currentSpeed){
        samples.add(currentSpeed);
        removeOldSample();
        return currentSpeedStatus();
    }

    public void setLowestLimit(double lowestLimit) {
        this.lowestLimit = lowestLimit;
    }

    public void setHighestLimit(double highestLimit) {
        this.highestLimit = highestLimit;
    }

    private int currentSpeedStatus() {
        double averageSpeed = samples.stream().mapToDouble(x -> x).sum() / NUMBER_OF_SAMPLES;

        if(averageSpeed < STANDING_STILL_THRESHOLD || samples.size() < NUMBER_OF_SAMPLES) {
            return WITHIN_THRESHOLD;
        }

        samples = new LinkedList<>();
        if (averageSpeed > highestLimit) {
            return ABOVE_THRESHOLD;
        }
        return BELOW_THRESHOLD;
    }

    private void removeOldSample() {
        if(samples.size() > NUMBER_OF_SAMPLES) {
            samples.remove(0);
        }
    }
}
