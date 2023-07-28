# An Algorithm to Calculate the $p$-value of the Monge-Elkan Distance

This repository contains source codes used to generate experimental results for the following paper:

Petr Ryšavý, Filip Železný
An Algorithm to Calculate the $p$-value of the Monge-Elkan Distance
TODO: provide a reference when available

The paper was presented at ISBRA 2022 (https://mangul-lab-usc.github.io/ISBRA)
and is intended for the following special issue.

All source codes are implemented in Java programming language. To run the project,
use one of the main files. Alternatively, you can use the pre-compiled release jar.
The outputs are provided in the form of LaTeX files which can be compiled into figures.
To run the pre-compiled version, download the release JAR file, install Java, and run
```bash
java -cp isbra2022-2.0.jar cz.cvut.fel.ida.isbra2022.StringDistanceMonteCarlo
```
The main classes are `StringDistanceMonteCarlo`, `StringDistanceNullDistro`, `CircleIntegersMonteCarlo`, `CircleIntegersNullDistro`. The `StringDistance***` classes implement experiments using the Levenshtein/Hamming distances; the latter two classes use a setting where points are located uniformly on a circle, and the distance between them is calculated. The classes `***MonteCarlo` compare with an empirical distribution, Bernstein's inequality upper bound, and Central Limit Theorem approximation. The `***NullDistro` classes compare the distances in the case when the full enumeration of all possible combinations of input strings/numbers is possible. For a simple implementation of the approximation defined in the paper, see `AbstractNullDistro.approximatedDistro()`.
