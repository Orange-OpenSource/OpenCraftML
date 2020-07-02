# OpenCraftML & CraftML4Txt

This project contains a release of CraftML4Txt, a powerful multi-label classifier for raw-texts.
The project contains also the original CraftML, a multi-label classifier for tabular numeric/symbolic data.


You can directly use the 2 provided jar programs

- to use CraftML4Txt for text-mining purposes, use CraftML4Txt.jar; the "sandbox4txt" directory provides an example of text-data and an example of script that you can execute

- to use CraftML for data-mining purposes, or to run the benchmarks for research purposes, use CraftML01.jar; several data and config files are provided in the "CraftML\_Examples" directory.


Please see the documentation for more information.


While most automatic classification algorithms can only classify data in one category at a time, CraftML can classify data into multiple categories. 
CraftML is a Clustering-based Random Forest for Extreme multi-label Learning, and it is, in its category, a state of the art algorithm.


The CraftML project contains the original java implementation of CraftML, an efficient Clustering-based Random Forest for Extreme multi-label Learning.
If you use this code, please cite this paper:

SIBLINI, Wissam, MEYER, Frank, & KUNTZ, Pascale. 
CRAFTML, an Efficient Clustering-based Random Forest for Extreme Multi-label Learning. 
In : International Conference on Machine Learning. 2018. p. 4671-4680.


Note that (for the multi-label classifier only) there exists another implementation done in RUST by TomTung, also available on github (https://github.com/tomtung/craftml-rs). It is also a good one!


----------------------------------------
## Contents


CraftML\_Sources: directory with the sources of the original Algorithm (ICML 2018 paper); language Java 1.8 or +

CraftML\_Examples: directory with examples of configuration files and data files to run CraftML

CraftML\_Txt_Sources: directory with the sources of the CraftML4Txt program; language Java 1.8 or +

sanbox4Txt: directory with example of script files and text data file to run CraftML4Txt

CraftML01.jar: runnable jar built from the CraftML sources (using JDK 1.8 or +)

CraftML4txt.jar: runnable jar built from the CraftML4Txt sources (Using JDK 1.8 or +)

licence.txt: licence file (MIT Licence)

CraftMLTxt\_Manual\_vXXX: documentation 

README.md: this file


--------------------------------------
## Using CraftML4Txt for text classification with multi targets

### Have a look to the sandbox4Txt directory first
If you use Windows, you can directly use the sandbox4txt directory: copy this directory on C:/ to obtain a C:/sandbox4Txt/ directory. 
You will find in this sandbox the script and the dataset examples that you can use directly with.

If you use Linux, copy the sandbox4Txt directory in a personal folder. 
You will just need first to adapt the filepaths in the example of script (scriptLittle.txt). 

See documentation for more information.


To run CraftML4Txt, you will need to have Java installed on your PC (version 1.8 or later).

You will need to prepare a text file (see documentation); an example is given in the sandbox4Txt directory : littleTrain.txt

You will need to prepare a script file.

An example of script file is given in the sandbox4Txt directory of this project: ScriptLittle.txt


Please note that CraftML4Txt use UTF-8 without BOM text file format for both scripts and data files.
We recommand to use an editor such as Notepad++ to check, and if necessary to encode, the right text file format.

```
To run CraftML4Txt with the data and script examples, just type:
java -jar CraftML4Txt.jar scriptLittle.txt

(the script file should be in the same directry as the jar file)
```

### INSTALLATION OF SOURCE CODE

The craftML4txt.jar program provided as example is compiled with the CraftML\_API\_ScriptInterpretor main entry.

If you are using Eclipse, you will be able to import directly the project. The main class to run / interprete script files is the CraftML\_API\_ScriptInterpretor.java class (in the textModule package).

Note that craftML4Txt contains all the code to build or and run the orginal CraftML. 
But as some adaptations have been done specifically for text-mining, and to be sure that XML-Repository benchmarks will stay reproducible, it is better to use the previous sub-project for numerical / XML data-ming purposes.



--------------------------------------
## Using CraftML with the craftML01.jar program (data-mining & benchmark XML)


### with the config file
Using the config file should be the easiest way to use CraftML, we recommand this option.
The craftML01.jar program provided as example is compiled with the CratML_API main entry (package Algorithm), so it is to be used via a config file.

You will need to have Java installed on your PC (version 1.8 or later).

You will need to install the datasets (see the section "To obtain the data")

You will need to prepare a config file.

An example of config file is given in the main directory of this project: config_API_CRAFTML.txt

Comment / decomment the line of a given action configuration to run it.

```
To run CraftML just type:
java -jar craftML01.jar config_API_CRAFTML.txt

(the config file should be in the same directory as the jar file)
```

To run craftML on XML (Extreme Multi label) data, you should use the -Xmx -Xms option to ask for more RAM

```
For instance if you can ask 32Gb of RAM, you can use: 

java -Xms32g -XMX32g -jar craftML01.jar c:\OpenCraftML2019\config_API_CRAFTML.txt
```
(it is assumed in this example that the project has been installed on C:/openCraftML2019/... )

Please note that CraftML use UTF-8 without BOM text file format for both config files and data files.
We recommand to use an editor such as Notepad++ to check, and if necessary to encode, the right text file format.

### by the command-line 
On Eclipse you can generate a jar program, using the CraftML.java entry.
In this case, you can run CraftML in a command-line manner.


### Data for extreme multi-label data (libsvm-type data file)

A data file starts with a header line with three space-separated integers: total number of examples, number of features, and number of labels. 
Following the header line, there is one line per example, starting with comma-separated labels, followed by space-separated feature:value pairs:
```
label1,label2,...labelk ft1:ft1_val ft2:ft2_val ft3:ft3_val .. ftd:ftd_val
```
For each dataset you have a train file and a test file.

An example of extreme multi label dataset is given with eurlex4k (see in the subdirectory of data_XML_ShortExample)

Note that you will need 32+ Go of RAM to run CraftML on the biggest datasets.

To reproduce the result of the original paper, use the default setting of the confilg file given in example.
(use a branch factor of 10, 50 trees, max of 10 instances per leaf, etc...)

### Data for classical mono-target data (UCI-type data file)

Alternatively you can also use CraftML on UCI (tabular) data. In this file format earch file:
the first line is a header, with the name of the atribute (separator: tabulation); each label attribute (target class) must contains "class" in its name.
the other line are the data, each value must be separated by a tabulation. 

Note that any value not parsable as an number is transformed by a concatenation of its name, an underscore, its value, and its value is then 1.
Example: on IRIS_train1.txt, for the target variable "class", the value of the 1st record is "setosa" an then it is parsed by CraftML as class_setosa=1
With this trick, CraftML can directly handle symbolic values.

An example of UCI dataset is given with Iris (see in the subdirectory of data_UCI_ShortExample)


### To obtain the data for complete benchmarks

To reduce the size of this project, only one multi-label (libsvm format) and one mono-label (tabular format) are given (Eurlex and IRIS respectively).

You can find all the extreme multi-label data on the [Extreme Classification Repository](http://manikvarma.org/downloads/XC/XMLRepository.html).

You can find all the UCI (with the tabular format) on this link: https://drive.google.com/drive/folders/1oIy-pVcNu13SQmxRNRZy_7EVW0ZggxiA?usp=sharing

We also provide a copy of the extreme multi-label data (with the subdirectory hierarchy compliant with the benchmarks).

Unzip the files on your disk.

On Windows, unzip on c:/data\_UCI and c:/data\_XML respectively to be able to run the benchmarks directly from the config file given in example.


### INSTALLATION OF SOURCE CODE

For the version of craftML dedicated to XML benchmark and data-mining we recommand to use the CraftML sub-project.
Indeed, the later version (dedicated to text-mining) has some adaptations which will not be optimal for numeric data-mining only purposes.

The code is an export of an Eclipse project (version Photon).
You should use Eclipse to import the project, it should be easier to work on it.

If you use windows, and if you copy the project on c:\openCraftML2019, you will be able to run directly the examples of the config file.

Once you have copied / cloned the project

1) Run Eclipse

2) File -> Import -> Existing Project -> Browse to the project directory...
Then click on "Finish"

3) Optionnal to run the benchmarks : Install the datasets

On Windows, install c:/data\_UCI and c:/data\_XML to be able to run the benchmarks directly from the config file given in example.

On Linux, install the datasets in a directory on your disk 
and then adapt the path directory in the sources Bench_UCI.java and Bench_XML.java if you want to use CraftML from Eclipse and run the benchmarks.

----------------------------------
## USING CraftML (data-mining and Extreme Multi-label Benchmarks) from Eclipse

You can run the two benchmarks in the benchmark package, just by selecting them and clicking on the run (green button) of Eclipse.

- Bench_UCI.java will run the benchmark on various UCI mono-label datasets from the UCI repository (re-formated in the "tabular file format)

- Bench_XML.java will run the benchmark on the extreme multi-label repository.

You must download and install the required datasets before.

If you use Windows, and if you install the datasets from https://drive.google.com/drive/folders/1oIy-pVcNu13SQmxRNRZy_7EVW0ZggxiA?usp=sharing to c:/data_UCI and c:/data_XML respectively you will be able to run all the benchmark
without modification. Otherwise, adapt the file paths on the code of Bench_UCI.java and Bench_XML.java.

You can also run CraftML via the java program CrafTML_API.
In this case, you have to give in argument the path of the config file (given in example: config_API_CRAFTML.txt).

Note that it is possible to generate an executable jar file 

- using CraftML.java as Main Class (for a command-line usage),

- or using CraftML_API.java as Main Class (for using it via a config file)



----------------------------------
## Trademarks / Citations

Java is a Trademark of Oracle Corporation. 

Eclipse is an open source project of the Eclipse Foundation.

The datasets of the UCI benchmark are adapted from the UCI repository: https://archive.ics.uci.edu/ml/index.php

The datasets of the XML benchmark are from the Extreme multi label dataset repository: http://manikvarma.org/downloads/XC/XMLRepository.html



