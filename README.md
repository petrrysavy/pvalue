# An Algorithm to Calculate the *p*-value of the Monge-Elkan Distance

This repository contains source codes used to generate experimental results for the following paper:

Ryšavý, Petr, and Filip Železný. "An Algorithm to Calculate the p-value of the Monge-Elkan Distance." *Journal of Computational Biology* (2025).
(https://www.liebertpub.com/doi/10.1089/cmb.2024.0854)

```bibtex
@article{doi:10.1089/cmb.2024.0854,
    author = {Ry\v{s}av\'{y}, Petr and \v{Z}elezn\'{y}, Filip},
    title = {An Algorithm to Calculate the p-Value of the Monge-Elkan Distance},
    journal = {Journal of Computational Biology},
    volume = {32},
    number = {8},
    pages = {797-812},
    year = {2025},
    doi = {10.1089/cmb.2024.0854},
    note ={PMID: 40488654},
    URL = {https://doi.org/10.1089/cmb.2024.0854},
    eprint = {https://doi.org/10.1089/cmb.2024.0854},
    abstract = { The Monge-Elkan distance is a straightforward yet popular distance measure used to estimate the mutual similarity of two sets of objects. It was initially proposed in the field of databases, and it found broad usage in other fields. Nowadays, it is especially relevant to the analysis of new-generation sequencing data as it represents a measure of dissimilarity between genomes of two distinct organisms, particularly when applied to unassembled reads. This article provides an algorithm to calculate the p-value associated with the Monge-Elkan distance. Given the object-level null distribution, that is, the distribution of distances between independently and identically sampled objects such as reads, the method yields the null distribution of the Monge-Elkan distance, which in turn allows for calculating the $p$-value. We also demonstrate an application on sequencing data, where individual reads are compared by the Levenshtein distance.}
}
```

The paper was presented at ISBRA 2022 for the first time (https://mangul-lab-usc.github.io/ISBRA),
without publication. Now, we are in the process of finding a journal to publish the results.

All source codes are implemented in Java programming language. To run the project,
use one of the main files. Alternatively, you can use the pre-compiled release jar.
The outputs are provided in the form of LaTeX files which can be compiled into figures.
To run the pre-compiled version, download the release JAR file, install Java, and run
```bash
java -cp isbra2022-2.0.jar cz.cvut.fel.ida.isbra2022.StringDistanceMonteCarlo
```
The main classes are `StringDistanceMonteCarlo`, `StringDistanceNullDistro`, `CircleIntegersMonteCarlo`, `CircleIntegersNullDistro`. The `StringDistance***` classes implement experiments using the Levenshtein/Hamming distances; the latter two classes use a setting where points are located uniformly on a circle, and the distance between them is calculated. The classes `***MonteCarlo` compare with an empirical distribution, Bernstein's inequality upper bound, and Central Limit Theorem approximation. The `***NullDistro` classes compare the distances in the case when the full enumeration of all possible combinations of input strings/numbers is possible. For a simple implementation of the approximation defined in the paper, see `AbstractNullDistro.approximatedDistro()`.

## Null distribution data
The algorithm uses no external data; the null distributions are calculated in runtime using subclasses of `AbstractNullDistro.` Please, see, for example, `StringDistanceNullDistro:mongeElkanNullDistro()`.
