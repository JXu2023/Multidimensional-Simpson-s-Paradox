# Multidimensional-Simpson-s-Paradox
Algorithms used to find Simpson's Paradox in multidimensional data sets.

In MDSP.java,
First, enter the file path of the dataset, how many attributes there are, and whether or not to find strong paradoxes.
Second, use createIndex() to assign indexes to each value.
Next, use getMover() to get how many bits each attribute needs to move.
Then use aggregate() (Algorithm 2) to collect all statistics.
Finally, use findSP() (Algorithm 3) to find Simpson's Paradoxes.

The data sets are Adult (https://archive.ics.uci.edu/ml/datasets/adult), Loan(https://www.kaggle.com/datasets/ikpeleambrose/irish-loan-data), and Mushroom(https://archive.ics.uci.edu/ml/datasets/mushroom).

The experiments are performed using the ParadoxList Class.
First, pass all of the statistics of the dataset. 
Use getContributions() to get a list of how many paradoxes each record contributed to.
Use getAllSeparators() to get a map of cardinality and the corresponding number of paradoxes.
Use getAllDimensions() to get an array of how many paradoxes per dimension.
Use populationDistribution() and pass the size of the bucket and how many buckets to get a hashmap of the population distribution.
