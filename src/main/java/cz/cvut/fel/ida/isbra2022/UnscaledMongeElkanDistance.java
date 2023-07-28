/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */package cz.cvut.fel.ida.isbra2022;

import cz.cvut.fel.ida.isbra2022.distance.DistanceCalculator;

/**
 *
 * @author Petr Ryšavý
 * @param <T>
 */
public class UnscaledMongeElkanDistance<T> implements DistanceCalculator<T[], Integer> {

    private final DistanceCalculator<T, Integer> innerDistance;

    public UnscaledMongeElkanDistance(DistanceCalculator<T, Integer> innerDistance) {
        this.innerDistance = innerDistance;
    }

    @Override
    public Integer getDistance(T[] a, T[] b) {
        int distance = 0;
        for (T aElem : a) {
            int bestMatch = Integer.MAX_VALUE;
            for (T bElem : b)
                bestMatch = Math.min(innerDistance.getDistance(aElem, bElem), bestMatch);
            distance += bestMatch;
        }
        return distance;
    }

}
