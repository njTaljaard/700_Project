#!/bin/bash

function printHelp {
  echo
  echo "graphicsMake"
  echo "------------"
  echo
  echo "A conversion script for image files intended for a LaTeX document."
  echo
  echo "The script receives a graphics directory and a file root. It is assumed that the"
  echo "graphics directory contains a source directory (named "source"), within which"
  echo "one of two file types resides: either a LaTeX source file (for building an EPS"
  echo "graphics file), or an existing EPS graphics file."
  echo
  echo "In the former case, an EPS graphics file is built, and a converted PDF file is"
  echo "generated. In the latter case, only a converted PDF file is generated. In both"
  echo "cases, both the (generated or existing) EPS file, and the converted PDF file are"
  echo "copied to the supplied graphics directory."
  printUsage
}

function printUsage {
  echo
  echo "Usage: ./graphicsMake.sh [-help|-convertonly] <directory> <file>"
  echo
  echo "Switches:"
  echo "  -help        - print help and usage information"
  echo "  -convertonly - don't compile from LaTeX file, only convert existing EPS file"
  echo "                 to PDF and copy both to [directory]"
  echo
  echo "Parameters:"
  echo "  <directory>  - directory location that compiled files will be placed"
  echo "  <file>       - the root file name for the LaTeX file to be compiled, or the"
  echo "                 existing EPS file to convert (excluding file extension)"
  echo
  exit
}

# get directory and file parameters
if [ ! -z $1 ]; then
  if [ $1 == "-convertonly" ]; then
    if [ ! -z $2 ] && [ ! -z $3 ] && [ -z $4 ]; then
      directory=$2
      file=$3
    else
      printUsage
    fi
  elif [ $1 == "-help" ]; then
    printHelp
  else
    if [ ! -z $1 ] && [ ! -z $2 ] && [ -z $3 ]; then
      directory=$1
      file=$2
    else
      printUsage
    fi
  fi
else
  printUsage
fi

# check directory and file parameters
if [ ! -d $directory ]; then
  echo
  echo "Error: Specified directory "$directory" invalid"
  echo
  exit
fi
if [ ! -d $directory/source ]; then
  echo
  echo "Error: Specified directory "$directory" contains no source directory"
  echo
  exit
fi
if [ $1 == "-convertonly" ]; then
    if [ ! -f $directory/source/$file.eps ]; then
      echo
      echo "Error: Source file "$file".eps invalid"
      echo
      exit
    fi
  else
    if [ ! -f $directory/source/$file.tex ]; then
      echo
      echo "Error: Source file "$file".tex invalid"
      echo
      exit
    fi
fi

# convert files
cd $directory/source
if [ $1 != "-convertonly" ]; then
  latex $file.tex
  dvips -E $file.dvi -o $file.eps
fi
mv $file.eps ../
cd ..
epstopdf --nocompress $file.eps
gv $file.eps