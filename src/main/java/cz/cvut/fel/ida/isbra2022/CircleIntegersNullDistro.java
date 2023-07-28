package cz.cvut.fel.ida.isbra2022;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * The comparison with the empirical distribution using points placed on a
 * circle. The approximated distribution is compared with the one obtained by
 * enumerating all possibilities.
 *
 * @author Petr Ryšavý <petr.rysavy@fel.cvut.cz>
 */
public class CircleIntegersNullDistro extends AbstractNullDistro<CircleIntegersNullDistro.EnumerableInteger> {

    private final int n; //5

    public CircleIntegersNullDistro(int n, int rasize, int rbsize) {
        super(rasize, rbsize,
                (EnumerableInteger a, EnumerableInteger b) -> {
                    final int diff = Math.abs(a.value - b.value);
                    return Math.min(diff, n - diff);
                });
        this.n = n;
    }

    @Override
    public long[] nullDistroDistUncached() {
        final long[] nullDistro = new long[n / 2 + 1];
        EnumerableInteger a = new EnumerableInteger();
        EnumerableInteger b = new EnumerableInteger();

        do {
            b.reset();
            do {
                nullDistro[dist.getDistance(a, b)]++;
            } while (b.nextSafe());
        } while (a.nextSafe());

        return nullDistro;
    }

    @Override
    public long[] mongeElkanNullDistro() {

        long[] nullDistro = new long[rasize * (n / 2) + 1];
        EnumerableInteger[] ra = new EnumerableInteger[rasize];
        for (int i = 0; i < rasize; i++) ra[i] = new EnumerableInteger();
        EnumerableInteger[] rb = new EnumerableInteger[rbsize];
        for (int i = 0; i < rbsize; i++) rb[i] = new EnumerableInteger();

        do {
            for (int i = 0; i < rbsize; i++) rb[i].reset();
            do {
                nullDistro[meDist.getDistance(ra, rb)]++;
            } while (next(rb));
        } while (next(ra));
        return nullDistro;
    }

    public double[] monteCarloNullDistro(int trials, Random rnd) {

        long[] nullDistro = new long[rasize * (n / 2) + 1];
        Arrays.fill(nullDistro, 1); //Bayesian prior

        EnumerableInteger[] ra = new EnumerableInteger[rasize];
        for (int i = 0; i < rasize; i++) ra[i] = new EnumerableInteger();
        EnumerableInteger[] rb = new EnumerableInteger[rbsize];
        for (int i = 0; i < rbsize; i++) rb[i] = new EnumerableInteger();

        for (int i = 0; i < trials; i++) {
            for (int j = 0; j < rasize; j++)
                ra[j].random(rnd);
            for (int j = 0; j < rbsize; j++)
                rb[j].random(rnd);

            nullDistro[meDist.getDistance(ra, rb)]++;
        }
        return MathUtils.normalize(nullDistro);
    }

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

//        final int[] ns = new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10};
        final int[] ns = new int[]{2, 4, 6, 8, 10};
        final int[] rbs = new int[]{2, 3, 4, 5};
        final String[] labels = new String[ns.length];
        for (int i = 0; i < ns.length; i++) labels[i] = "$n=" + ns[i] + "$";
        DistroCompareTeX outputFile = new DistroCompareTeX(labels);
        CDFCompareTeX cdfFile = new CDFCompareTeX(labels);
        for (int bagsize : rbs) {
            outputFile.startRow("$|R_A| = " + bagsize + "$");
            cdfFile.startRow("$|R_A| = " + bagsize + "$");
            for (int n : ns) {
                System.err.println("n ^ (2*bagsize) : " + Math.pow(n, 2 * bagsize));
                if (Math.pow(n, 2 * bagsize) > 1e10) {
                    outputFile.NAplot();
                    continue;
                }

                CircleIntegersNullDistro model = new CircleIntegersNullDistro(n, bagsize, bagsize);
                final double[] mongeElkanNull = model.mongeElkanNullDistroNorm();
                final double[] approximatedDistro = model.approximatedDistro();
                outputFile.distro(mongeElkanNull, approximatedDistro, "Circle" + bagsize + "_" + n);
                cdfFile.distro(NullDistroOperations.nullDistroNormToCDF(mongeElkanNull),
                        NullDistroOperations.nullDistroNormToCDF(approximatedDistro),
                        model.bernsteinUB(),
                        model.centralLimitTheoremApprox(), "Circle" + bagsize + "_" + n);
            }
        }
        outputFile.close(Paths.get("CircleIntegers.tex"));
        cdfFile.close(Paths.get("CircleIntegersCDF.tex"), Paths.get("CircleIntegersCDFKL.tex"));
    }

    private boolean next(EnumerableInteger[] reads) {
        for (int i = 0; i < reads.length; i++)
            if (!reads[i].hasNext())
                reads[i].reset();
            else {
                reads[i].next();
                return true;
            }
        return false;
    }

    class EnumerableInteger {

        int value;

        public EnumerableInteger() {
            this.value = 0;
        }

        public void next() {
            value++;
            if (value >= n)
                throw new NoSuchElementException("Iterated over all strings.");
        }

        public boolean nextSafe() {
            if (hasNext()) {
                next();
                return true;
            }
            return false;
        }

        public void random(Random rnd) {
            value = rnd.nextInt(n);
        }

        public boolean hasNext() {
            return value < n - 1;
        }

        public void reset() {
            this.value = 0;
        }

    }
}
