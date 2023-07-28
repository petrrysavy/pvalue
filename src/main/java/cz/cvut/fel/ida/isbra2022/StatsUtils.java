package cz.cvut.fel.ida.isbra2022;

/**
 * Utility class that is capable of calculating various statistics.
 *
 * @author Petr Ryšavý
 */
public final class StatsUtils {

    /** Do not let anybody to instantiate the class. */
    private StatsUtils() {
    }

    /**
     * The Kullback-Leibner divergence, sometimes called relative entropy.
     * @param groudTruthP The data, what we expect.
     * @param approximationQ A model approximation.
     * @return The relative entropy.
     * @see https://en.wikipedia.org/wiki/Kullback%E2%80%93Leibler_divergence
     */
    public static double KLDivergence(double[] groudTruthP, double[] approximationQ) {
        return KLDivergence(groudTruthP, approximationQ, Math.E, -1.0);
    }

    /**
     * The Kullback-Leibner divergence, sometimes called relative entropy.
     * @param groudTruthP The data, what we expect.
     * @param approximationQ A model approximation.
     * @param logBase
     * @param limit The KL divergence underflow sometimes. Therefore, small
     * values might be needed to exclude from the calculation.
     * @return The relative entropy.
     * @see https://en.wikipedia.org/wiki/Kullback%E2%80%93Leibler_divergence
     */
    public static double KLDivergence(double[] groudTruthP, double[] approximationQ, double logBase, double limit) {
        if (groudTruthP.length != approximationQ.length)
            throw new IllegalArgumentException("Distributions need to have the same universe, found : " + groudTruthP.length + ", " + approximationQ.length);

        double divergence = 0.0;
        for (int i = 0; i < groudTruthP.length; i++)
            if (groudTruthP[i] > limit && approximationQ[i] > limit)
                divergence += groudTruthP[i] * MathUtils.logBase(groudTruthP[i] / approximationQ[i], logBase);
        return divergence;
    }

}
