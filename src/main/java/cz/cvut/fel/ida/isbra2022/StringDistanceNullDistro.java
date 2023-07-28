package cz.cvut.fel.ida.isbra2022;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Random;
import cz.cvut.fel.ida.isbra2022.distance.DistanceCalculator;

/**
 * The comparison with the empirical distribution using the string distances.
 * The approximated distribution is compared with the one obtained by
 * enumerating all possibilities.
 *
 * @author Petr Ryšavý <petr.rysavy@fel.cvut.cz>
 */
public class StringDistanceNullDistro extends AbstractNullDistro<StringDistanceNullDistro.EnumerableString> {

    private static final int MAX_STR_LEN = 20;

    private final int strlen; //5
    private final int alphabetSize; //4

    StringDistanceNullDistro(int strlen, int rasize, int rbsize, int alphabetSize, DistanceCalculator<EnumerableString, Integer> dist) {
        super(rasize, rbsize, dist);
        this.strlen = strlen;
        this.alphabetSize = alphabetSize;
    }

    @Override
    public long[] nullDistroDistUncached() {
        final long[] nullDistro = new long[strlen + 1];
        EnumerableString a = new EnumerableString();
        EnumerableString b = new EnumerableString();

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

        long[] nullDistro = new long[rasize * strlen + 1];
        EnumerableString[] ra = new EnumerableString[rasize];
        for (int i = 0; i < rasize; i++) ra[i] = new EnumerableString();

        do {
            EnumerableString[] rb = new EnumerableString[rbsize];
            for (int i = 0; i < rbsize; i++) rb[i] = new EnumerableString();
            do {
                nullDistro[meDist.getDistance(ra, rb)]++;
            } while (next(rb));
        } while (next(ra));
        return nullDistro;
    }

    public double[] monteCarloNullDistro(int trials, Random rnd) {

        int[] nullDistro = new int[rasize * strlen + 1];
        Arrays.fill(nullDistro, 1); //Bayesian prior

        EnumerableString[] ra = new EnumerableString[rasize];
        for (int i = 0; i < rasize; i++) ra[i] = new EnumerableString();
        EnumerableString[] rb = new EnumerableString[rbsize];
        for (int i = 0; i < rbsize; i++) rb[i] = new EnumerableString();

        for (int i = 0; i < trials; i++) {
            for (int j = 0; j < rasize; j++)
                ra[j].randomString(rnd);
            for (int j = 0; j < rbsize; j++)
                rb[j].randomString(rnd);

            nullDistro[meDist.getDistance(ra, rb)]++;
        }
        return MathUtils.normalize(nullDistro);
    }

    private static void evaluate2(DistanceCalculator<EnumerableString, Integer> dist, String id) throws IOException {
        int alphabetsize = 2;

        final String[] labels = new String[]{"$l=2$", "$l=3$", "$l=4$", "$l=5$"};
        DistroCompareTeX outputFile = new DistroCompareTeX(labels);
        CDFCompareTeX cdfFile = new CDFCompareTeX(labels);
        for (int bagsize : new int[]{2, 3, 4}) {
            outputFile.startRow("$|R_A| = " + bagsize + "$");
            cdfFile.startRow("$|R_A| = " + bagsize + "$");
            for (int strlen : new int[]{2, 3, 4, 5}) {
                System.err.println("strlen * 2 * bagsize : " + strlen * 2 * bagsize);
                if (strlen * 2 * bagsize > 27) {
                    outputFile.NAplot();
                    continue;
                }

                StringDistanceNullDistro model = new StringDistanceNullDistro(strlen, bagsize, bagsize, alphabetsize, dist);
                final double[] mongeElkanNull = model.mongeElkanNullDistroNorm();
                final double[] approximatedDistro = model.approximatedDistro();
                outputFile.distro(mongeElkanNull, approximatedDistro, id + "_2_" + bagsize + "_" + strlen);
                cdfFile.distro(NullDistroOperations.nullDistroNormToCDF(mongeElkanNull),
                        NullDistroOperations.nullDistroNormToCDF(approximatedDistro),
                        model.bernsteinUB(),
                        model.centralLimitTheoremApprox(), id + "_2_" + bagsize + "_" + strlen);
            }
        }

        outputFile.close(Paths.get(id + "2.tex"));
        cdfFile.close(Paths.get(id + "CDF2.tex"), Paths.get(id + "CDF2KL.tex"));
    }

    private static void evaluate4(DistanceCalculator<EnumerableString, Integer> dist, String id) throws IOException {
        int alphabetsize = 4;

        final String[] labels = new String[]{"$l=2$", "$l=3$"};
        DistroCompareTeX outputFile = new DistroCompareTeX(labels);
        CDFCompareTeX cdfFile = new CDFCompareTeX(labels);
        for (int bagsize : new int[]{2, 3}) {
            outputFile.startRow("$|R_A| = " + bagsize + "$");
            cdfFile.startRow("$|R_A| = " + bagsize + "$");
            for (int strlen : new int[]{2, 3}) {
                System.err.println("strlen * 4 * bagsize : " + strlen * 4 * bagsize);
                if (strlen * 4 * bagsize > 27) {
                    outputFile.NAplot();
                    continue;
                }

                StringDistanceNullDistro model = new StringDistanceNullDistro(strlen, bagsize, bagsize, alphabetsize, dist);
                final double[] mongeElkanNull = model.mongeElkanNullDistroNorm();
                final double[] approximatedDistro = model.approximatedDistro();
                outputFile.distro(mongeElkanNull, approximatedDistro, id + "_4_" + bagsize + "_" + strlen);
                cdfFile.distro(NullDistroOperations.nullDistroNormToCDF(mongeElkanNull),
                        NullDistroOperations.nullDistroNormToCDF(approximatedDistro),
                        model.bernsteinUB(),
                        model.centralLimitTheoremApprox(), id + "_4_" + bagsize + "_" + strlen);
            }
        }

        outputFile.close(Paths.get(id + "4.tex"));
        cdfFile.close(Paths.get(id + "CDF4.tex"), Paths.get(id + "CDF4KL.tex"));
    }

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

        evaluate2(LEVENSHTEIN, "Leven");
        evaluate2(HAMMING, "Hamm");
        evaluate4(LEVENSHTEIN, "Leven");
        evaluate4(HAMMING, "Hamm");
    }

    private boolean next(EnumerableString[] reads) {
        for (int i = 0; i < reads.length; i++)
            if (!reads[i].hasNext())
                reads[i] = new EnumerableString();
            else {
                reads[i].next();
                return true;
            }
        return false;
    }

    class EnumerableString {

        final int[] string;

        public EnumerableString() {
            this.string = new int[strlen];
        }

        public void reset() {
            Arrays.fill(string, 0);
        }

        public void next() {
            for (int i = 0; i <= string.length; i++) {
                if (string[i] == alphabetSize - 1)
                    string[i] = 0;
                else {
                    string[i]++;
                    return;
                }
            }
            throw new NoSuchElementException("Iterated over all strings.");
        }

        public void randomString(Random rnd) {
            for (int i = 0; i < string.length; i++) {
                string[i] = rnd.nextInt(alphabetSize);
            }
        }

        public boolean nextSafe() {
            if (hasNext()) {
                next();
                return true;
            }
            return false;
        }

        public boolean hasNext() {
            for (int i : string)
                if (i < alphabetSize - 1)
                    return true;
            return false;
        }

    }

    public static final DistanceCalculator<EnumerableString, Integer> HAMMING = (EnumerableString a, EnumerableString b) -> {
        int dist1 = 0;
        for (int i = 0; i < a.string.length; i++)
            dist1 += a.string[i] == b.string[i] ? 0 : 1;
        return dist1;
    };

    private static final int[] LEVENSHTEIN_ARR_1 = new int[MAX_STR_LEN];
    private static final int[] LEVENSHTEIN_ARR_2 = new int[MAX_STR_LEN];
    public static final DistanceCalculator<EnumerableString, Integer> LEVENSHTEIN = (EnumerableString a, EnumerableString b) -> {
        final int[] aSeq = a.string;
        final int[] bSeq = b.string;
        // create emty table, first string in rows, second to the columns
        int[] scoreMatrixCurrent = LEVENSHTEIN_ARR_1;
        int[] scoreMatrixLast = LEVENSHTEIN_ARR_2;
        int[] swap;

        scoreMatrixLast[0] = 0;
        for (int j = 0; j < bSeq.length; j++)
            scoreMatrixLast[j + 1] = scoreMatrixLast[j] + 1;
        for (int i = 0; i < aSeq.length; i++) { // i goes over rows, i.e. the first word
            scoreMatrixCurrent[0] = scoreMatrixLast[0] + 1;
            for (int j = 0; j < bSeq.length; j++)
                scoreMatrixCurrent[j + 1] = MathUtils.min(
                        scoreMatrixCurrent[j] + 1,
                        scoreMatrixLast[j + 1] + 1,
                        scoreMatrixLast[j] + (aSeq[i] == bSeq[j] ? 0 : 1));

            swap = scoreMatrixCurrent;
            scoreMatrixCurrent = scoreMatrixLast;
            scoreMatrixLast = swap;
        }
        return scoreMatrixLast[bSeq.length];
    };
}
