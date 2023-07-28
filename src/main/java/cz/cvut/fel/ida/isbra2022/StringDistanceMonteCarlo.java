/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.isbra2022;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Random;
import cz.cvut.fel.ida.isbra2022.distance.DistanceCalculator;
import cz.cvut.fel.ida.isbra2022.StringDistanceNullDistro.EnumerableString;
import static cz.cvut.fel.ida.isbra2022.StringDistanceNullDistro.HAMMING;
import static cz.cvut.fel.ida.isbra2022.StringDistanceNullDistro.LEVENSHTEIN;

/**
 * The comparison with the empirical distribution using the string distances.
 * The comparison also includes the Bernstein's inequality upper bound and
 * approximation stemming from the Central Limit Theorem.
 *
 * @author Petr Ryšavý <petr.rysavy@fel.cvut.cz>
 */
public class StringDistanceMonteCarlo {

    private static final int TRIALS = 1000000;
    private static final Random RANDOM = new Random(42);

    private static void evaluate2(DistanceCalculator<EnumerableString, Integer> dist, String id) throws IOException {
        int alphabetsize = 2;

        final String[] labels = new String[]{"$l=6$", "$l=8$", "$l=10$", "$l=12$"};
        DistroCompareTeX outputFile = new DistroCompareTeX(labels);
        CDFCompareTeX cdfFile = new CDFCompareTeX(labels);
        for (int bagsize : new int[]{2, 5, 10, 15, 20}) {
            outputFile.startRow("$|R_A| = " + bagsize + "$");
            cdfFile.startRow("$|R_A| = " + bagsize + "$");
            for (int strlen : new int[]{6, 8, 10, 12}) {
                System.err.println("strlen * 2 : " + strlen * 2);
                //if (strlen * 2 > 27) {
                //    outputFile.NAplot();
                //continue;
                //}

                StringDistanceNullDistro model = new StringDistanceNullDistro(strlen, bagsize, bagsize, alphabetsize, dist);
                final double[] monteCarloNullDistro = model.monteCarloNullDistro(TRIALS, RANDOM);
                final double[] approximatedDistro = model.approximatedDistroDouble();

                outputFile.distro(monteCarloNullDistro, approximatedDistro, id + "_2_" + bagsize + "_" + strlen);
                cdfFile.distro(NullDistroOperations.nullDistroNormToCDF(monteCarloNullDistro),
                        NullDistroOperations.nullDistroNormToCDF(approximatedDistro),
                        model.bernsteinUB(),
                        model.centralLimitTheoremApprox(), id + "_2_" + bagsize + "_" + strlen);
            }
        }

        outputFile.close(Paths.get(id + "2MC.tex"));
        cdfFile.close(Paths.get(id + "CDF2MC.tex"), Paths.get(id + "CDF2MCKL.tex"));
    }

    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.US);

        evaluate2(LEVENSHTEIN, "Leven");
        evaluate2(HAMMING, "Hamm");
//        evaluate4(LEVENSHTEIN, "Leven");
//        evaluate4(HAMMING, "Hamm");
    }

}
