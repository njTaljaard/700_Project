set fontpath "../../../../fonts/"
set output "graph1.eps"
set terminal postscript eps enhanced mono fontfile "sfss1000.pfb"
set noxtics
set noytics
set xlabel "X axis label"
set ylabel "Y axis label"
set lmargin 0
set rmargin 0
set tmargin 0
set bmargin 0
set arrow 1 from 0,0 to 0,1.5 head
set arrow 2 from 0,0 to 10,0 head
set xrange [0:10]
set yrange [0:1.5]
unset border
set size 0.5, 0.5
plot 0.5*log(x) with lines linetype 1 title "A log graph"
