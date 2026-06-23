@echo off
cd /d %~dp0
xelatex -interaction=nonstopmode main.tex
xelatex -interaction=nonstopmode main.tex
