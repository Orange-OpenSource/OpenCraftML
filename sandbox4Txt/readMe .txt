Contents of sandbox(V3, november 2020)
======================================

This sandbox for CraftML4Txt V3 is self-contained.

Documentation
--------------
CraftML4Txt_Manual_v01G.pdf
This document will explain how to use CraftML for Text (and CraftML for numeric data).
The main subject is multi-label text classification.

Data
-----
littleTrain.txt		train file example
littleTest.txt		test file example

These data are just to show the syntax and the multi-label possibilies
These data are in text UTF8 format.
Do not save them in an other text format than UTF8.
Use Notepad++ to check/correct encoding if necessary

jar Files (for java version 1.8 or +)
---------------------------------------------------
CraftML4Txt.jar : alias for CraftMLTxtV3.jar    (the batch excutable)
EasyCraft.jar : alias for EasyCraftML4TxtV3.jar  (a simple user interface for craftml4txt)

Syntax for batch mode (only to execute a script)
java -jar CraftML4Txt.jar myFileScriptToExecute.txt

Syntax for the user interface
java -jar EasyCraftML4txt.jar

All programs are open-sources (licence MIT).
Note : on GitHub, a version for "Data Mining" of CraftML (craftML01.jar) is also available :
https://github.com/Orange-OpenSource/OpenCraftML
See the documentation


Script
-------
script_Little.txt		script to run CraftML4Txt on the "littleTrain/test" data





