package cz.cvut.fel.ida.isbra2022;

import java.util.Arrays;

/**
 *
 * @author Petr Ryšavý <petr.rysavy@fel.cvut.cz>
 */
public class NullDistroOperations {

    private NullDistroOperations() {
    }

    public static long[] minDistro(long[] originalDistro, int rbsize) {
        final long[] suffixSumArray = new long[originalDistro.length + 1];
        suffixSumArray[originalDistro.length] = 0;
        suffixSumArray[originalDistro.length - 1] = originalDistro[originalDistro.length - 1];
        for (int i = originalDistro.length - 2; i >= 0; i--)
            suffixSumArray[i] = suffixSumArray[i + 1] + originalDistro[i];

        PascalTriangle binomials = new PascalTriangle(rbsize);

        final long[] minDistr = new long[originalDistro.length];
        for (int d = 0; d < originalDistro.length; d++) {
            long prob = 0;
            for (int i = 1; i <= rbsize; i++)
                prob += binomials.get(rbsize, i).longValueExact() * MathUtils.pow(originalDistro[d], i) * MathUtils.pow(suffixSumArray[d + 1], rbsize - i);
            minDistr[d] = prob;
            assert prob > 0; // double-check for overflow!
        }
        return minDistr;
    }

    public static double[] minDistro(double[] originalDistroNorm, int rbsize) {
        final double[] suffixSumArray = new double[originalDistroNorm.length + 1];
        suffixSumArray[originalDistroNorm.length] = 0;
        suffixSumArray[originalDistroNorm.length - 1] = originalDistroNorm[originalDistroNorm.length - 1];
        for (int i = originalDistroNorm.length - 2; i >= 0; i--)
            suffixSumArray[i] = suffixSumArray[i + 1] + originalDistroNorm[i];

        PascalTriangle binomials = new PascalTriangle(rbsize);

        final double[] minDistr = new double[originalDistroNorm.length];
        for (int d = 0; d < originalDistroNorm.length; d++) {
            double prob = 0;
            for (int i = 1; i <= rbsize; i++)
                prob += binomials.get(rbsize, i).doubleValue() * MathUtils.pow(originalDistroNorm[d], i) * MathUtils.pow(suffixSumArray[d + 1], rbsize - i);
            minDistr[d] = prob;
            assert prob > 0; // double-check for overflow!
        }
        return minDistr;
    }

    public static double[] power(double[] originalDistro, int power) {
        final double[] curr = new double[(originalDistro.length - 1) * power + 1];
        final double[] next = new double[(originalDistro.length - 1) * power + 1];
        System.arraycopy(originalDistro, 0, curr, 0, originalDistro.length);

        for (int p = 1; p < power; p++) {
            for (int i = 0; i < curr.length; i++)
                for (int j = 0; j < originalDistro.length; j++)
                    if (i + j < curr.length)
                        next[i + j] += curr[i] * originalDistro[j];

            System.arraycopy(next, 0, curr, 0, next.length);
            Arrays.fill(next, 0);
        }
        return curr;
    }

    public static double expectedValueNormDistro(double[] distroNorm) {
        double average = 0;
        for (int i = 0; i < distroNorm.length; i++)
            average += ((double) i) * distroNorm[i];
        return average;
    }

    public static double varianceNormDistro(double[] distroNorm) {
        // TODO can be done single pass, but OK, this is more failproof
        final double mean = expectedValueNormDistro(distroNorm);
        double variance = 0;
        for (int i = 0; i < distroNorm.length; i++)
            variance += distroNorm[i] * (((double) i) - mean) * (((double) i) - mean);
        return variance;
    }

    /**
     * Gives Bernstein's UB on the pvalue of sum of samples taken from the
     * normalized null distro.
     *
     * @param nullDistroNorm Normalized distro of the summand. In the
     * experiments, it is the min(dist()).
     * @param rasize Size of the read bag.
     * @return Bound as by Barnard's theorem.
     */
    public static double[] bernsteinUB(double[] nullDistroNorm, int rasize) {
        final double expectedValue = NullDistroOperations.expectedValueNormDistro(nullDistroNorm);
        final double variance = NullDistroOperations.varianceNormDistro(nullDistroNorm);

        final int M = nullDistroNorm.length - 1;

        final double[] bernsteinUB = new double[rasize * M + 1];
        for (int D = 0; D < bernsteinUB.length; D++) {
            final double d = ((double) D) / rasize; // D is sum, d is average, i.e., ME distwor 
            final double muMinusD = expectedValue - d;
            if (muMinusD > 0.0)
                bernsteinUB[D] = Math.exp(-(((double) rasize) * muMinusD * muMinusD) / (2.0 * variance + 2.0 / 3.0 * M * muMinusD));
            else bernsteinUB[D] = 1.0;

            assert (bernsteinUB[D] < 1.0000001);
        }
        return bernsteinUB;
    }

    /**
     * Gives Central Limit Theorem approximation of the sum of the samples taken
     * from the normalized null distro.
     *
     * @param nullDistroNorm Normalized distro of the summand. In the
     * experiments, it is the min(dist()).
     * @param rasize Size of the read bag.
     * @return Approximation by the CLT.
     */
    public static double[] cltApproximation(double[] nullDistroNorm, int rasize) {
        final double expectedValue = NullDistroOperations.expectedValueNormDistro(nullDistroNorm);
        final double variance = NullDistroOperations.varianceNormDistro(nullDistroNorm);
        final double stdev = Math.sqrt(variance);

        final int M = nullDistroNorm.length - 1;

        final double[] cltapproxpvalue = new double[rasize * M + 1];
        for (int D = 0; D < cltapproxpvalue.length; D++) {
            final double d = ((double) D) / rasize; // D is sum, d is average, i.e., ME distwor 
            final double zscore = Math.sqrt(rasize) * (d - expectedValue) / stdev;
            cltapproxpvalue[D] = gaussianCDF(zscore);
        }
        return cltapproxpvalue;
    }

    /**
     * Implementation of the gaussian cummulative distribution function.
     *
     * The implementation of this method is taken from (with minor
     * modifications) Sedgewick and Waynes class at Priceton. Please, see
     * {@link https://introcs.cs.princeton.edu/java/21function/} and
     * {@link https://introcs.cs.princeton.edu/java/21function/Gaussian.java.html}.
     *
     * @param z Z-score
     * @return The CDF.
     */
    public static double gaussianCDF(double z) {
        if (z < -8.0) return 0.0;
        if (z > 8.0) return 1.0;
        double sum = 0.0, term = z;
        for (int i = 3; Math.abs(term) > 1e-15; i += 2) {
            sum = sum + term;
            term = term * z * z / i;
        }
        return 0.5 + sum * Math.exp(-z * z / 2) / Math.sqrt(2 * Math.PI);
    }

    public static double[] nullDistroNormToCDF(double[] nullDistroNorm) {
        final double[] prefixSum = MathUtils.prefixSumArray(nullDistroNorm);
        if (Math.abs(prefixSum[prefixSum.length - 1] - 1.0) > 1e-6)
            throw new IllegalArgumentException("The normalized null distribution does not sum to one: " + prefixSum[prefixSum.length - 1]);
        prefixSum[prefixSum.length - 1] = 1.0;
        return prefixSum;
    }
    
    public static double[] cdfToNullDistroNorm(double[] cdf) {
        final double[] nullDistro = new double[cdf.length];
        nullDistro[0] = cdf[0];
        for(int i = 1; i < nullDistro.length; i++)
            nullDistro[i] = cdf[i] - cdf[i-1];
        return nullDistro;
    }

}
