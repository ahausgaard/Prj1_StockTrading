package business.stockmarket.simulation;

import domain.StockState;
import shared.logging.Logger;
import shared.logging.LoggerLevel;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class TransitionManager {
    private final Logger logger = Logger.getInstance();
    private final Random random = new Random();
    //Using EnumMap
    private final Map<StockState, Map<StockState, Double>> transitionTable = new EnumMap<>(StockState.class);


    public TransitionManager() {
        // Initialize transition probabilities
        Map<StockState, Double> steadyTransitions = new EnumMap<>(StockState.class);
        steadyTransitions.put(StockState.STEADY, 0.80);
        steadyTransitions.put(StockState.GROWING, 0.10);
        steadyTransitions.put(StockState.DECLINING, 0.10);
        steadyTransitions.put(StockState.BANKRUPT, 0.0);
        transitionTable.put(StockState.STEADY, steadyTransitions);

        Map<StockState, Double> growingTransitions = new EnumMap<>(StockState.class);
        growingTransitions.put(StockState.STEADY, 0.20);
        growingTransitions.put(StockState.GROWING, 0.75);
        growingTransitions.put(StockState.DECLINING, 0.05);
        growingTransitions.put(StockState.BANKRUPT, 0.0);
        transitionTable.put(StockState.GROWING, growingTransitions);

        Map<StockState, Double> decliningTransitions = new EnumMap<>(StockState.class);
        decliningTransitions.put(StockState.STEADY, 0.25);
        decliningTransitions.put(StockState.GROWING, 0.10);
        decliningTransitions.put(StockState.DECLINING, 0.65);
        decliningTransitions.put(StockState.BANKRUPT, 0.01);
        transitionTable.put(StockState.DECLINING, decliningTransitions);
        // Remove Bankrupt and Reset transitions from transitionTable
    }

    public StockState getNextState(StockState currentState) {
        if (currentState == StockState.BANKRUPT) {
            logger.log(LoggerLevel.INFO, "Bankrupt: Penalty timeout.");
            return StockState.STEADY;
        }

        Map<StockState, Double> probabilities = transitionTable.get(currentState);
        if (probabilities == null) {
            logger.log(LoggerLevel.WARNING, "No transition table for state: " + currentState);
            return currentState;
        }
        double rand = random.nextDouble();
        double cumulative = 0.0;
        StockState chosenState = currentState;
        for (Map.Entry<StockState, Double> entry : probabilities.entrySet()) {
            cumulative += entry.getValue();
            if (rand < cumulative) {
                chosenState = entry.getKey();
                break;
            }
        }
        logger.log(LoggerLevel.INFO, "Transition attempt: " + currentState + " -> " + chosenState + " (rand=" + rand + ")");
        return chosenState;
    }
}
