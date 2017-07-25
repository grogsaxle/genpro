#labels Featured,Phase-Deploy
=Quick start =
#summary quick start

===Running the examples===
  # Make sure you have downloaded and installed java SDK 1.5 or higher.
  # Download and unzip the main distribution (genpro-x.x-bin.zip)
  # Start one of the batchfiles (or rewrite them to linux ; ):
    * runCelciusFahrenheitExample.bat
    * runEggWeightExample.bat


===What is in the distribution:===
- folder *examples-src* : The sources of the examples, this is a good starting point to explore how to use GenPro.

- *genpro-x.x.jar* : This is the GenPro core library (junit test are not included). The core is chosen in such a way that it can run on Android as well. A separate Android distribution will be made available.
  * minimum java-version :  java 1.5 (excluding swing)
  * needed jars    :  none
		
- *genpro-swingview-xx.jar* : This contains the view classes, VisualTrainer etc..(junit test are not included) 		 
  * minimum java-version :  java 1.5 (including swing)
  * needed jars     :  genpro-x.x.jar, collections-generic-4.01.jar, jung-algorithms-2.0.jar, jung-visualization-2.0.jar, jung-api-2.0.jar, jung-graph-impl-2.0.jar
								
- *genpro-examples-x.x.jar* : This contains the examples		
  * minimum java-version :  java 1.5 
  * needed jars     :  genpro-x.x.jar, genpro-swingview-x.x.jar ( + its needed libraries)

- folder *doc/genpro* : The Javadoc of both genpro-core and genpro-swingview

- *eggData.txt* : Data file for eggweight example	


===known issues===
  * multi-threading does not work yet
  * example RomanFiguresProblem does not result in a working solution yet