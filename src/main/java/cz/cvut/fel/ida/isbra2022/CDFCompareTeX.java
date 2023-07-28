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
public class CDFCompareTeX {

    List<String> output;
    List<String> tableOutput;
    String[] colHeaders;
    int n;

    public CDFCompareTeX(String[] colHeaders) {
        this.colHeaders = colHeaders;
        n = 0;

        output = new LinkedList<>();
        output.add("\\documentclass{standalone}\n"
                + "\\usepackage{pgfplots}\n"
                + "\\pgfplotsset{compat=1.16}\n"
                + "\\usepackage{filecontents}\n"
                + "\\usepgfplotslibrary{external}\n"
                + "\\tikzexternalize\n"
                + "\\def\\vc#1{$\\vcenter{\\hbox{#1}}$}\n"
                + "\\makeatletter\n"
                + "\\long\\def\\ifnodedefined#1#2#3{%\n"
                + "    \\@ifundefined{pgf@sh@ns@#1}{#3}{#2}%\n"
                + "}\n"
                + "\\pgfplotsset{\n"
                + "    discontinuous/.style={\n"
                + "    scatter,\n"
                + "    scatter/@pre marker code/.code={\n"
                + "        \\ifnodedefined{marker}{\n"
                + "            \\pgfpointdiff{\\pgfpointanchor{marker}{center}}%\n"
                + "             {\\pgfpoint{0}{0}}%\n"
                + "             \\ifdim\\pgf@y>0pt\n"
                + "                \\tikzset{options/.style={mark=*, fill=white}}\n"
                + "                \\draw [densely dashed] (marker-|0,0) -- (0,0);\n"
                + "                \\draw plot [mark=*] coordinates {(marker-|0,0)};\n"
                + "             \\else\n"
                + "                \\tikzset{options/.style={mark=none}}\n"
                + "             \\fi\n"
                + "        }{\n"
                + "            \\tikzset{options/.style={mark=none}}        \n"
                + "        }\n"
                + "        \\coordinate (marker) at (0,0);\n"
                + "        \\begin{scope}[options]\n"
                + "    },\n"
                + "    scatter/@post marker code/.code={\\end{scope}}\n"
                + "    }\n"
                + "}\n"
                + "\\makeatother"
                + "\\begin{document}");
        output.add("\\begin{tabular}{c|" + StringUtils.kCopies("c", colHeaders.length) + '}');
        output.add("& " + StringUtils.toString(colHeaders, " & "));
        output.add("\\\\ \\hline");

        tableOutput = new LinkedList<>();
        tableOutput.add("\\begin{tabular}{cc|" + StringUtils.kCopies("c", colHeaders.length) + "}");
        tableOutput.add(" & & Gen. poly. approximation & CLT approximation & BI upper bound\\\\");
    }

    public void startRow(String header) {
        output.add("\\\\");
        output.add("\\vc{" + header + "}");

        n = 0;
        tableOutput.add("\\hline");
        tableOutput.add(header);
    }

    public void distro(double[] nullCDF, double[] approximatedCDF, double[] bernstein, double[] clt_cdf, String id) {
        //final double divergence = StatsUtils.KLDivergence(nullDistro, approximatedDistro);
        final int widePlot = Math.max((15 - nullCDF.length), 3);
        final int narrowPlot = Math.max((int) (widePlot * 0.6), 2);

        output.add("&");

        output.add("\\begin{filecontents*}{" + id + ".csv}");
        output.add("dist,null,approx,bernstein,clt");
        for (int i = 0; i < nullCDF.length; i++)
            output.add(Integer.toString(i) + ',' + Double.toString(nullCDF[i]) + ',' + Double.toString(approximatedCDF[i]) + ',' + Double.toString(bernstein[i]) + ',' + Double.toString(clt_cdf[i]));
        output.add("\\end{filecontents*}");

        output.add("\\vc{\\begin{tikzpicture}");
        output.add("\\begin{axis}[ymin=0, width=5cm,clip=false, jump mark left, discontinuous,]");
        output.add("\\addplot[red, dashed, mark options=solid] \n"
                + "  table[col sep=comma,x index=0,y index=1] {" + id + ".csv};");
        output.add("\\addplot[blue] \n"
                + "  table[col sep=comma,x index=0,y index=2] {" + id + ".csv};");
        output.add("\\addplot[green!50!black, dotted, mark options=solid] \n"
                + "  table[col sep=comma,x index=0,y index=3] {" + id + ".csv};");
        output.add("\\addplot[gray, dashdotted, mark options=solid] \n"
                + "  table[col sep=comma,x index=0,y index=4] {" + id + ".csv};");
        output.add("\\end{axis}");
        output.add("\\end{tikzpicture}}");

        final double[] nullDistro = NullDistroOperations.cdfToNullDistroNorm(nullCDF);
        final double divergenceApproxKL = StatsUtils.KLDivergence(nullDistro, NullDistroOperations.cdfToNullDistroNorm(approximatedCDF), Math.E, 1e-14);
        final double cltApproxKL = StatsUtils.KLDivergence(nullDistro, NullDistroOperations.cdfToNullDistroNorm(clt_cdf), Math.E, 1e-14);
        final double bernsteinKL = StatsUtils.KLDivergence(nullDistro, NullDistroOperations.cdfToNullDistroNorm(bernstein), Math.E, 1e-14);
        final double min = MathUtils.min(divergenceApproxKL, cltApproxKL, bernsteinKL);

        tableOutput.add(" & " + colHeaders[n++] + " & " + formatKLDivergence(divergenceApproxKL, min) + " & " + formatKLDivergence(cltApproxKL, min) + " & " + formatKLDivergence(bernsteinKL, min) + "\\\\");
    }

    private String formatKLDivergence(double divergence, double boldfaced) {
        if (divergence == boldfaced)
            return "\\textbf{" + String.format("%.3f", divergence) + '}';
        else
            return String.format("%.3f", divergence);
    }

    public void NAplot() {
        output.add("& \\vc{NA}");
    }

    public void close(Path file, Path klDivFile) throws IOException {
        output.add("\\end{tabular}");
        output.add("\\end{document}");
        output.add("");
        IOUtils.write(file, output);

        tableOutput.add("\\hline \\end{tabular}");
        IOUtils.write(klDivFile, tableOutput);
    }

}
