# ==============================================================================

SHELL = /bin/sh

# File parameters
FILE_ROOT = dissertation

SRC_FILES = $(shell find $(CURDIR) -name "*.tex") \
	$(shell find $(CURDIR) -name "*.bib") \
	$(shell find $(CURDIR) -name "*.eps")

BUILD_FILES = $(shell find $(CURDIR) -name "*.aux") \
	$(shell find $(CURDIR) -name "*.log") \
	$(shell find $(CURDIR) -name "*.pdf") \
	$(CURDIR)/$(FILE_ROOT).bbl \
	$(CURDIR)/$(FILE_ROOT).blg \
	$(CURDIR)/$(FILE_ROOT).dvi \
	$(CURDIR)/$(FILE_ROOT).glg \
	$(CURDIR)/$(FILE_ROOT).glo \
	$(CURDIR)/$(FILE_ROOT).gls \
	$(CURDIR)/$(FILE_ROOT).idx \
	$(CURDIR)/$(FILE_ROOT).ilg \
	$(CURDIR)/$(FILE_ROOT).ind \
	$(CURDIR)/$(FILE_ROOT).ist \
	$(CURDIR)/$(FILE_ROOT).lgf \
	$(CURDIR)/$(FILE_ROOT).loa \
	$(CURDIR)/$(FILE_ROOT).lof \
	$(CURDIR)/$(FILE_ROOT).lot \
	$(CURDIR)/$(FILE_ROOT).toc \
	$(CURDIR)/$(FILE_ROOT).tpt \
	$(CURDIR)/$(FILE_ROOT).ps \
	$(CURDIR)/$(FILE_ROOT).out

TMP_FILES = $(shell find $(CURDIR) -name "*.*~") \
	$(shell find $(CURDIR) -name "*~")

# PostScript viewer executable and options
PS_VIEWER = gv
PS_VIEWER_OPS =

# PDF viewer executable and options
PDF_VIEWER = kpdf
PDF_VIEWER_OPS =

# ==============================================================================

.PHONY: default pdf clean

# ==============================================================================

# Default phony rule to make and view a PostScript file
default: $(FILE_ROOT).ps
	@echo
	@echo "*** Viewing PostScript file ***"
	@echo
	@rm -f hyperref.cfg
	$(PS_VIEWER) $(PS_VIEWER_OPS) $(FILE_ROOT).ps

# Phony rule to make and view a PDF file
pdf: $(FILE_ROOT).pdf
	@echo
	@echo "*** Viewing PDF file ***"
	@echo
	@rm -f hyperref.cfg
	$(PDF_VIEWER) $(PDF_VIEWER_OPS) $(FILE_ROOT).pdf

$(FILE_ROOT).ps: $(FILE_ROOT).dvi
	@echo
	@echo "*** Building PostScript file ***"
	@echo
	dvips -f < $(FILE_ROOT).dvi > $(FILE_ROOT).ps

$(FILE_ROOT).pdf: $(SRC_FILES)
	@echo
	@echo "*** Building PDF file ***"
	@echo
	pdflatex -shell-escape $(FILE_ROOT)
	bibtex $(FILE_ROOT)
	makeindex -s $(FILE_ROOT).ist -t $(FILE_ROOT).glg -o $(FILE_ROOT).gls \
		$(FILE_ROOT).glo
	makeindex $(FILE_ROOT) -s index.ist
	pdflatex -shell-escape $(FILE_ROOT)
	pdflatex -shell-escape $(FILE_ROOT)
	#thumbpdf $(FILE_ROOT)
	pdflatex -shell-escape $(FILE_ROOT)

# makeindex $(FILE_ROOT).glo -s glossary.ist -o $(FILE_ROOT).gls

$(FILE_ROOT).dvi: $(SRC_FILES)
	@echo
	@echo "*** Building DVI FILE ***"
	@echo
	latex $(FILE_ROOT)
	bibtex $(FILE_ROOT)
	makeindex -s $(FILE_ROOT).ist -t $(FILE_ROOT).glg -o $(FILE_ROOT).gls \
		$(FILE_ROOT).glo
	makeindex $(FILE_ROOT) -s index.ist
	latex $(FILE_ROOT)
	latex $(FILE_ROOT)

# Phony rule for cleaning up all generated files
clean:
	@rm -f $(BUILD_FILES) $(TMP_FILES)










#pre $(FILE_ROOT).gls $(FILE_ROOT).ind $(FILE_ROOT).bbl final
#echo
#echo "[Building PDF file]"
#dvips -Ppdf -G0 $(FILE_ROOT) -o $(FILE_ROOT).ps
#ps2pdf -dMaxSubsetPct=100 -dCompatibilityLevel=1.2 -dSubsetFonts=true \
#   -dEmbedAllFonts=true $(FILE_ROOT).ps
#echo "[Viewing PDF file]"
#$(PDF_VIEWER) $(PDF_VIEWER_OPS) $(FILE_ROOT).pdf





#echo "[Building DVI file]"
#echo
#latex $(FILE_ROOT)
#bash -c ' \
#f [ -f $(FILE_ROOT).glo ]; then \
#        echo "[Generating glossary]"; \
#        echo; \
#        makeindex $(FILE_ROOT).glo -s glossary.ist -o $(FILE_ROOT).gls; \
#fi '






#pre: $(SECTION_SOURCE)
#rm -f $(FILE_ROOT).gls $(FILE_ROOT).ind

# Rule for building index list
$(FILE_ROOT).ind: $(SECTION_SOURCE)
	bash -c ' \
	if [ -f $(FILE_ROOT).idx ]; then \
		echo; \
		echo "[Generating index]"; \
		echo; \
		makeindex $(FILE_ROOT); \
	fi '

# Rule for building BIBTeX bibliography
$(FILE_ROOT).bbl: $(SECTION_SOURCE) $(FILE_ROOT).bib
	echo "[Generating bibliography]"
	bibtex $(FILE_ROOT)

# ==============================================================================
