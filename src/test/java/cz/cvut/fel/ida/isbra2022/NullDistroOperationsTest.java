package cz.cvut.fel.ida.isbra2022;

import java.util.Arrays;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.hamcrest.number.IsCloseTo;
import org.junit.Test;

/**
 *
 * @author Petr Ryšavý <petr.rysavy@fel.cvut.cz>
 */
public class NullDistroOperationsTest {

    public NullDistroOperationsTest() {
    }

    @Test
    public void testMinDistro() {
        final long[] nullDistro = new long[]{1, 2, 2};
        assertThat(
                NullDistroOperations.minDistro(nullDistro, 2),
                is(new long[]{9, 12, 4})
        );
    }

    @Test
    public void comapreMinDistroDouble() {
        final long[] distDistro = new long[]{10, 20, 30, 1, 76, 321, 452, 4321, 21, 32};
        final long[] nullDistro = NullDistroOperations.minDistro(distDistro, 5);
        final double[] nullDistroNormalized = MathUtils.normalize(nullDistro);
        final double[] nullDistroDouble = NullDistroOperations.minDistro(MathUtils.normalize(distDistro), 5);
        for (int i = 0; i < nullDistroNormalized.length; i++)
            assertThat(nullDistroNormalized[i], IsCloseTo.closeTo(nullDistroDouble[i], 1e-10));
    }

    @Test
    public void testPower() {
        double[] actual = NullDistroOperations.power(new double[]{9.0 / 25, 12.0 / 25, 4.0 / 25}, 3);
        System.err.println(Arrays.toString(actual));
        double[] expected = MathUtils.normalize(new int[]{729, 2916, 4860, 4320, 2160, 576, 64});
        for (int i = 0; i < actual.length; i++)
            assertThat(actual[i], IsCloseTo.closeTo(expected[i], 1e-10));
    }

    @Test
    public void testMean() {
        assertThat(NullDistroOperations.expectedValueNormDistro(MathUtils.normalize(new int[]{3, 2, 2})),
                is(equalTo(6.0 / 7.0)));
    }

    @Test
    public void testVariance() {
        assertThat(NullDistroOperations.varianceNormDistro(MathUtils.normalize(new int[]{3, 2, 2})),
                IsCloseTo.closeTo(34.0 / 49.0, 1e-10));
    }

    @Test
    public void testBernsteinUB() {
        final double[] actual = NullDistroOperations.bernsteinUB(MathUtils.normalize(new int[]{3, 2, 2}), 2);
        final double[] expected = new double[]{0.55953725831183803385, 0.87209116469551312951, 1.0, 1.0, 1.0};
        for (int i = 0; i < actual.length; i++)
            assertThat(actual[i], IsCloseTo.closeTo(expected[i], 1e-10));
    }

    @Test
    public void testGaussiandCDF() {
        assertThat(NullDistroOperations.gaussianCDF(-9), IsCloseTo.closeTo(0, 1e-3));
        assertThat(NullDistroOperations.gaussianCDF(-5), IsCloseTo.closeTo(0, 1e-3));
        assertThat(NullDistroOperations.gaussianCDF(-3), IsCloseTo.closeTo(0.0013, 1e-3));
        assertThat(NullDistroOperations.gaussianCDF(-2), IsCloseTo.closeTo(0.0228, 1e-3));
        assertThat(NullDistroOperations.gaussianCDF(-1), IsCloseTo.closeTo(0.1587, 1e-3));
        assertThat(NullDistroOperations.gaussianCDF(0), IsCloseTo.closeTo(0.5, 1e-3));

    }

    @Test
    public void testCDFAndBack() {
        final double[] nullDistro = new double[]{0.7, 0.2, 0.1};
        final double[] cdf = NullDistroOperations.nullDistroNormToCDF(nullDistro);
        final double[] expectedCDF = new double[]{0.7, 0.9, 1.0};
        for (int i = 0; i < cdf.length; i++)
            assertThat(cdf[i], IsCloseTo.closeTo(expectedCDF[i], 1e-10));
        final double[] nullBack = NullDistroOperations.cdfToNullDistroNorm(cdf);
        for (int i = 0; i < cdf.length; i++)
            assertThat(nullBack[i], IsCloseTo.closeTo(nullDistro[i], 1e-10));
    }
}
