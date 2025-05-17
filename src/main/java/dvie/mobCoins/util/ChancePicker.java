package dvie.mobCoins.util;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ChancePicker<T> {

    @Getter
    private Map<T, Double> options = new HashMap<>();
    private double max = 0;
    private Random random;

    public void addOption(T option, double weightedChance) {
        random = new Random();
        options.put(option, weightedChance);
        max += weightedChance;
    }

    public void removeOption(T option){
        max -= options.get(option);
        options.remove(option);
    }

    public T pick() {
        double rng = getRandomNumber(0, max);
        double current = 0;
        for (T option : options.keySet()){
            current += options.get(option);
            if (rng < current) return option;
        }
        return null;
    }

    public double getWeightedChance(T option){
        Double d = options.get(option);
        if (d == null)
            return 0;
        return d;
    }

    public double getChance(T option){
        double d = getWeightedChance(option);
        return d/max;
    }

    private double getRandomNumber(double min, double max) {
        return min + (random.nextDouble() * (max - min));
    }
}
