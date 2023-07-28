/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.ida.isbra2022;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Petr Ryšavý <petr.rysavy@fel.cvut.cz>
 */
public class DistroCompareTeX {

    List<String> output;

    public DistroCompareTeX(String[] colHeaders) {
        output = new LinkedList<>();
        output.add("\\documentclass{standalone}\n"
                + "\\usepackage{pgfplots}\n"
                + "\\pgfplotsset{compat=1.16}\n"
                + "\\usepackage{filecontents}\n"
                + "\\def\\vc#1{$\\vcenter{\\hbox{#1}}$}\n"
                + "\\begin{document}");
        output.add("\\begin{tabular}{c|" + StringUtils.kCopies("c", colHeaders.length) + '}');
        output.add("& " + StringUtils.toString(colHeaders, " & "));
        output.add("\\\\ \\hline");
    }

    public void startRow(String header) {
        output.add("\\\\");
        output.add("\\vc{"+header+"}");
    }

    public void distro(double[] nullDistro, double[] approximatedDistro, String id) {
        final double divergence = StatsUtils.KLDivergence(nullDistro, approximatedDistro);
        final int widePlot = Math.max((15 - nullDistro.length), 3);
        final int narrowPlot = Math.max((int) (widePlot * 0.6),2);

        output.add("&");

        output.add("\\begin{filecontents*}{" + id + ".csv}");
        output.add("dist,null,approx");
        for (int i = 0; i < nullDistro.length; i++)
            output.add(Integer.toString(i) + ',' + Double.toString(nullDistro[i]) + ',' + Double.toString(approximatedDistro[i]));
        output.add("\\end{filecontents*}");

        output.add("\\vc{\\begin{tikzpicture}");
        output.add("\\begin{axis}[ymin=0, title={KL-divergence: " + String.format("%.3f", divergence) + "}, width=5cm]");
        output.add("\\addplot[ybar,bar width="+widePlot+"pt,fill=red!60,opacity=0.8] \n"
                + "  table[col sep=comma,x index=0,y index=1] {" + id + ".csv};");
        output.add("\\addplot[ybar,bar width="+narrowPlot+"pt,fill=blue!80,opacity=0.6] \n"
                + "  table[col sep=comma,x index=0,y index=2] {" + id + ".csv};");
        output.add("\\end{axis}");
        output.add("\\end{tikzpicture}}");
    }

    public void NAplot() {
        output.add("& \\vc{NA}");
    }

    public void close(Path file) throws IOException {
        output.add("\\end{tabular}");
        output.add("\\end{document}");
        output.add("");
        IOUtils.write(file, output);
    }

}
