# snailj-sciview
Mathematical seashell generator based on the [paper by Jorge Picado](http://www.mat.uc.pt/~picado/conchas/eng/article.pdf)

# Seashell Generation
In SciView under Demo -> Seashell, simply select a preset and click okay.
- __Spiral Turns__: Amount of rotations to generate
- __Spiral Turn Resolution__: Resolution for underlying helical shape
- __Generating Curve Resolution__: Resolution for the "tube" that becomes the shell surface
- __Generating Curve Randomness Multiplier__: Uses Random.nextDouble() to add bumpiness to the shell surface

Options __D__ to __N__ are best explained in Picado's paper, but will eventually be described here too.

# Fractal Dimensionality
The Fractal Dimension of a generated shell is outputted to the log after creation

# LICENSING

snailj-sciview is distributed under a
[Simplified BSD License](http://en.wikipedia.org/wiki/BSD_licenses);
for the full text of the license, see
[LICENSE.txt](https://github.com/imagej/imagej/blob/master/LICENSE.txt).

For the list of ImageJ developers and contributors, see
[the parent POM](https://github.com/imagej/pom-imagej/blob/master/pom.xml).
