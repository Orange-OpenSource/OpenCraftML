# OpenCraftML

While most automatic classification algorithms can only classify data in one category at a time, CraftML can classify data into multiple categories. CraftML is a Clustering-based Random Forest for Extreme multi-label Learning, and it is, in its category, a state of the art algorithm.


This project is the original java implementation of CraftML, an efficient Clustering-based Random Forest for Extreme multi-label Learning.
If you use this code, please cite this paper:

SIBLINI, Wissam, MEYER, Frank, & KUNTZ, Pascale. 
CRAFTML, an Efficient Clustering-based Random Forest for Extreme Multi-label Learning. 
In : International Conference on Machine Learning. 2018. p. 4671-4680.


Note that there exists another implementation done in RUST by TomTung, also available on github (https://github.com/tomtung/craftml-rs). It is also a good one!


--------------------------------------
## Using CraftML with the jar program 

### with the config file
Using the config file should be the easiest way to use CraftML, we recommand this option.
The jar program provided as example is compiled with the CratML_API main entry, so it is to be used via a config file.

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
(it is assumed that the project has been installed on C:/openCraftML2019/... )

### by the command-line 
On Eclipse you can generate a jar program, using the CraftML.java entry.
In this case, you can run CraftML in a command-line manner.


## Data 


### for extreme multi-label data (libsvm-type data file)

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

### for classical mono-target data (UCI-type data file)

Alternatively you can also use CraftML on UCI (tabular) data. In this file format earch file:
the first line is a header, with the name of the atribute (separator: tabulation); each label attribute (target class) must contains "class" in its name.
the other line are the data, each value must be separated by a tabulation. 

Note that any value not parsable as an number is transformed by a concatenation of its name, an underscore, its value, and its value is then 1.
Example: on IRIS_train1.txt, for the target variable "class", the value of the 1st record is "setosa" an then it is parsed by CraftML as class_setosa=1
With this trick, CraftML can handle symbolic values.

An example of UCI dataset is given with Iris (see in the subdirectory of data_UCI_ShortExample)


## To obtain the data

To reduce the size of this project, only one multi-label (libsvm format) and one mono-label (tabular format) are given (Eurlex and IRIS respectively).

You can find all the extreme multi-label data on the [Extreme Classification Repository](http://manikvarma.org/downloads/XC/XMLRepository.html).

You can find all the UCI (with the tabular format) on this link: https://drive.google.com/drive/folders/1oIy-pVcNu13SQmxRNRZy_7EVW0ZggxiA?usp=sharing

We also provide a copy of the extreme multi-label data (with the subdirectory hierarchy compliant with the benchmarks).

Unzip the files on your disk.

On Windows, unzip on c:/data_UCI and c:/data_XML respectively to be able to run the benchmarks directly from the config file given in example.


## INSTALLATION OF SOURCE CODE

The code is an export of an Eclipse project (version Photon).
You should use Eclipse to import the project, it should be easier to work on it.

If you use windows, and if you copy the project on c:\openCraftML2019, you will be able to run directly the examples of the config file.

Once you have copied / cloned the project

1) Run Eclipse

2) File -> Import -> Existing Project -> Browse to the project directory (TODO)
Then click on "Finish"

3) Optionnal to run the benchmarks : Install the datasets

On Windows, install c:/data_UCI and c:/data_XML to be able to run the benchmarks directly from the config file given in example.

On Linux, install the datasets in a directory on your disk 
and then adapt the path directory in the sources Bench_UCI.java and Bench_XML.java if you want to use CraftML from Eclipse and run the benchmarks.

----------------------------------
## USING CraftML From Eclipse

You can run the two benchmarks in the benchmark package, just by selecting them and clicking on the run (green button) of Eclipse.

- Bench_UCI.java will run the benchmark on various UCI mono-label datasets from the UCI repository (re-formated in the "tabular file format)

- Bench_XML.java will run the benchmark on the extreme multi-label repository.

You must download and install the required datasets before.

If you use Windows, and if you install the datasets from https://drive.google.com/drive/folders/1oIy-pVcNu13SQmxRNRZy_7EVW0ZggxiA?usp=sharing to c:/data_UCI and c:/data_XML respectively you will be able to run all the benchmark
without modification. Otherwise, adapt the file paths on the code of Bench_UCI.java and Bench_XML.java.

You can also run CraftML via the java program CrafTML_API.
In this case, you have to give in argument the path of the config file (given in example: config_API_CRAFTML.txt).

It is possible to generate an executable jar file 

- using CraftML.java (for a command-line usage),

- or using CraftML_API.java (for a using it via a config file)



----------------------------------
## Trademarks / Citations

Java is a Trademark of Oracle Corporation. 

Eclipse is an open source project of the Eclipse Foundation.

The datasets of the UCI benchmark are adapted from the UCI repository: https://archive.ics.uci.edu/ml/index.php

The datasets of the XML benchmark are from the Extreme multi label dataset repository: http://manikvarma.org/downloads/XC/XMLRepository.html



