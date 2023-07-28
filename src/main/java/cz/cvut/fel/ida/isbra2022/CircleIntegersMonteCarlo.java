package cz.cvut.fel.ida.isbra2022;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Random;

/**
 * The comparison with the empirical distribution using the integer on a circle.
 * The comparison also includes the Bernstein's inequality upper bound and
 * approximation stemming from the Central Limit Theorem.
 *
 * @author Petr Ryšavý <petr.rysavy@fel.cvut.cz>
 */
public class CircleIntegersMonteCarlo {

    private static final int TRIALS = 1000000;
    private static final Random RANDOM = new Random(42);

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

//        final int[] ns = new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10};
        final int[] ns = new int[]{12, 14, 16, 18, 20};
        final int[] rbs = new int[]{6, 7, 8, 9, 10};
        final String[] labels = new String[ns.length];
        for (int i = 0; i < ns.length; i++) labels[i] = "$n=" + ns[i] + "$";
        CDFCompareTeX cdfFile = new CDFCompareTeX(labels);
        DistroCompareTeX outputFile = new DistroCompareTeX(labels);
        for (int bagsize : rbs) {
            outputFile.startRow("$|R_A| = " + bagsize + "$");
            cdfFile.startRow("$|R_A| = " + bagsize + "$");
            for (int n : ns) {
                System.err.println("n: " + n + ", bagsize: " + bagsize);
//                if (Math.pow(n, 2 * bagsize) > 1e10) {
//                    outputFile.NAplot();
//                    continue;
//                }

                CircleIntegersNullDistro model = new CircleIntegersNullDistro(n, bagsize, bagsize);
                final double[] monteCarloNullDistro = model.monteCarloNullDistro(TRIALS, RANDOM);
                final double[] approximatedDistro = model.approximatedDistroDouble();

                outputFile.distro(monteCarloNullDistro, approximatedDistro, "Circle" + bagsize + "_" + n);
                cdfFile.distro(NullDistroOperations.nullDistroNormToCDF(monteCarloNullDistro),
                        NullDistroOperations.nullDistroNormToCDF(approximatedDistro),
                        model.bernsteinUB(),
                        model.centralLimitTheoremApprox(), "Circle" + bagsize + "_" + n);
            }
        }
        outputFile.close(Paths.get("CircleIntegersMC.tex"));
        cdfFile.close(Paths.get("CircleIntegersCDFMC.tex"), Paths.get("CircleIntegersCDFMCKL.tex"));
    }

}
