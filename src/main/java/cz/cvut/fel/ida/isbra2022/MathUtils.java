package cz.cvut.fel.ida.isbra2022;

import java.util.Collection;

/**
 *
 * @author Petr Ryšavý
 */
public final class MathUtils {

    private MathUtils() {

    }

    public static int min(int... arr) {
        int min = arr[0];
        for (int i = 1; i < arr.length; i++)
            if (arr[i] < min)
                min = arr[i];
        return min;
    }

    public static int min(int a, int b, int c) {
        if (a <= b && a <= c)
            return a;
        return b <= c ? b : c;
    }

    public static double min(double... arr) {
        double min = arr[0];
        for (int i = 1; i < arr.length; i++)
            if (arr[i] < min)
                min = arr[i];
        return min;
    }

    public static int min(int[] arr, int firstIndex, int toIndex) {
        assert (firstIndex < toIndex);

        int min = arr[firstIndex];
        for (int i = firstIndex + 1; i < toIndex; i++)
            if (arr[i] < min)
                min = arr[i];
        return min;
    }

    public static double min(double a, double b, double c) {
        if (a <= b && a <= c)
            return a;
        return b <= c ? b : c;
    }

    public static double min(double a, double b, double c, double d) {
        return Math.min(Math.min(a, b), Math.min(c, d));
    }

    public static int sum(int[] vec) {
        int sum = 0;
        for (int i : vec)
            sum += i;
        return sum;
    }

    public static int sum(int[] vec, int fromIndex, int toIndex) {
        int sum = 0;
        toIndex = Math.min(vec.length, toIndex);
        for (int i = fromIndex; i < toIndex; i++)
            sum += vec[i];
        return sum;
    }

    public static double sum(double[] vec, int fromIndex, int toIndex) {
        double sum = 0;
        toIndex = Math.min(vec.length, toIndex);
        for (int i = fromIndex; i < toIndex; i++)
            sum += vec[i];
        return sum;
    }

    public static double sum(double[] vec) {
        double sum = 0;
        for (double i : vec)
            sum += i;
        return sum;
    }

    public static long sum(long[] vec) {
        long sum = 0;
        for (long i : vec)
            sum += i;
        return sum;
    }

    public static double sum(Collection<? extends Double> col) {
        double sum = 0.0;
        for (Double d : col) sum += d.doubleValue();
        return sum;
    }

    public static double average(double a, double b) {
        return (a + b) / 2.0;
    }

    public static double average(double... array) {
        return sum(array) / array.length;
    }

    public static double average(long... array) {
        return ((double) sum(array)) / array.length;
    }

    public static double average(int... array) {
        return ((double) sum(array)) / array.length;
    }

    public static int pow(int a, int b) {
        assert (b >= 0);

        int result = 1;
        for (int i = 0; i < b; i++)
            result *= a;
        return result;
    }

    public static long pow(long a, int b) {
        assert (b >= 0);

        long result = 1;
        for (int i = 0; i < b; i++)
            result *= a;
        return result;
    }

    public static double pow(double a, int b) {
        assert (b >= 0);

        double result = 1;
        for (int i = 0; i < b; i++)
            result *= a;
        return result;
    }

    public static double[] normalize(int[] distro) {
        int sum = MathUtils.sum(distro);
        double[] result = new double[distro.length];
        for (int i = 0; i < distro.length; i++)
            result[i] = ((double) distro[i]) / sum;
        return result;
    }

    public static double[] normalize(long[] distro) {
        long sum = MathUtils.sum(distro);
        double[] result = new double[distro.length];
        for (int i = 0; i < distro.length; i++)
            result[i] = ((double) distro[i]) / sum;
        return result;
    }

    /**
     * The prefix-sum array function. Sometimes called integral image. At each
     * position, there is sum of all values earlier in the list, including the
     * current one. For [1,2,3,4] the prefix-sum is [1,3,6,10].
     * @param arr The original array.
     * @return The prefix-sum array.
     */
    public static double[] prefixSumArray(double[] arr) {
        final double[] prefixSum = new double[arr.length];
        prefixSum[0] = arr[0];
        for (int i = 1; i < arr.length; i++)
            prefixSum[i] = prefixSum[i - 1] + arr[i];
        return prefixSum;
    }

    public static double logBase(double value, double base) {
        return Math.log(value) / Math.log(base);
    }

    public static double log2(double value) {
        return logBase(value, 2.0);
    }

}
