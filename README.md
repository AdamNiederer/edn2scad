# edn2scad

Convert EDN to OpenSCAD

# Usage

```shell
clojure -m edn2scad.core -f input.edn > output.scad
```

OpenSCAD's embedded language is pretty poorly-featured, slow, and has trouble
with recursion. `edn2scad` lets you define your OpenSCAD models with a real
programming language, output EDN, and then convert that output to something
OpenSCAD can understand.
