# Multidimensional-Simpson-s-Paradox
Algorithms used to find Simpson's Paradox in multidimensional data sets.
First, enter the file path of the dataset, how many attributes there are, and whether or not to find strong paradoxes.
Second, use createIndex() to assign indexes to each value.
Next, use getMover() to get how many bits each attribute needs to move.
Then use aggregate() (Algorithm 2) to collect all statistics.
Finally, use findSP() (Algorithm 3) to find Simpson's Paradoxes.

The data sets are Adult (https://archive.ics.uci.edu/ml/datasets/adult), Loan(https://www.kaggle.com/datasets/ikpeleambrose/irish-loan-data), and Mushroom(https://archive.ics.uci.edu/ml/datasets/mushroom).
