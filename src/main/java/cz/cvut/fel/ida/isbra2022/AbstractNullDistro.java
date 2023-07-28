package cz.cvut.fel.ida.isbra2022;

import cz.cvut.fel.ida.isbra2022.distance.DistanceCalculator;

/**
 *
 * @author Petr Ryšavý <petr.rysavy@fel.cvut.cz>
 * @param <T>
 */
public abstract class AbstractNullDistro<T> {

    final int rasize; //3
    final int rbsize;
    final DistanceCalculator<T, Integer> dist;
    final DistanceCalculator<T[], Integer> meDist;
    /** Cache for the nullDistroDist() function. */
    private long[] nullDistroDist;

    public AbstractNullDistro(int rasize, int rbsize, DistanceCalculator<T, Integer> dist) {
        this.rasize = rasize;
        this.rbsize = rbsize;
        this.dist = dist;
        this.meDist = new UnscaledMongeElkanDistance<>(dist);
        this.nullDistroDist = null;
    }

    public abstract long[] nullDistroDistUncached();

    public abstract long[] mongeElkanNullDistro();

    public long[] nullDistroDist() {
        if (nullDistroDist != null) return nullDistroDist;
        nullDistroDist = nullDistroDistUncached();
        return nullDistroDist;
    }

    public double[] mongeElkanNullDistroNorm() {
        return MathUtils.normalize(mongeElkanNullDistro());
    }

    private double[] minDistroNorm() {
        long[] nullDistro = nullDistroDist();
        long[] minDistro = NullDistroOperations.minDistro(nullDistro, rbsize);
        double[] minDistroNorm = MathUtils.normalize(minDistro);
        return minDistroNorm;
    }

    private double[] minDistroNormDouble() {
        long[] nullDistro = nullDistroDist();
        double[] nullDistroNorm = MathUtils.normalize(nullDistro);
        double[] minDistroNorm = NullDistroOperations.minDistro(nullDistroNorm, rbsize);
        return minDistroNorm;
    }

    public double[] approximatedDistro() {
        return NullDistroOperations.power(minDistroNorm(), rasize);
    }

    public double[] approximatedDistroDouble() {
        return NullDistroOperations.power(minDistroNormDouble(), rasize);
    }

    public double[] bernsteinUB() {
        return NullDistroOperations.bernsteinUB(minDistroNormDouble(), rasize);
    }

    public double[] centralLimitTheoremApprox() {
        return NullDistroOperations.cltApproximation(minDistroNormDouble(), rasize);
    }

}
