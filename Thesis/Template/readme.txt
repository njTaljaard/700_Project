CIRG MSc Dissertation Template (version 1.0)
by Willem S. van Heerden (April 2008)
=====================================

The distributed archive includes all the source you will need to produce a LaTeX dissertation compliant with the CIRG standard.

This template was designed under Linux, and consequently is op[timised for compiling documents under that operating system. It should work under Windows with a bit of effort on your part (but that's your baby). Included is a Makefile that will compile the document in either Postscript of PDF format. To remove all build files (for either Postscript or PDF files), issue the command:

make clean

from a console. To compile a Postscript file, simply execute the command:

make

To build a PDF file, issue the command:

make pdf

PLEASE NOTE: If you build a Postscript file directly after building a PDF (or vice versa), there will be compilation errors (seeing as the different builds generate files that conflict with one another). To solve this, simply remove the build files (using "make clean"), and re-build using whatever format you prefer.

Any queries may be forwarded to wvheerden@cs.up.ac.za (but please try to solve the problem yourself before asking me - I also have a dissertation to finish)