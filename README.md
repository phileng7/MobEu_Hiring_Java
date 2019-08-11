JUnit
# MobEu_Hiring_Java
Sum all elements from an array in subsets bellow a specific value

You want to send your friend a package with different things.
Each thing you put inside the package has such parameters as index number, weight and cost. The
package has a weight limit. Your goal is to determine which things to put into the package so that the
total weight is less than or equal to the package limit and the total cost is as large as possible.
You would prefer to send a package which weights less in case there is more than one package with the
same price.

Constraints:
1. Max weight that a package can take is ≤ 100
2. There might be up to 15 items you need to choose from
3. Max weight and cost of an item is ≤ 100

Put the "packages.txt" text file found in the root of this project and put in some place like
\tmp
If you change the place you must change the filePath inside <i>src/test/java/com.mobiquityinc.PackerTest</i> for JUnit testing and also
Java Application path argument for direct test.

To run the program there are two ways:
a) Java Application
Find <i>src/test/java/com.mobiquityinc.packer.run.PackerRun</i>
Run as Java Application with the file path argument like: "\tmp\packages.txt"

b) JUnit
Find <i>src/test/java/com.mobiquityinc.PackerTest</i>
Run As Junit Test
Observe that if you change the input "packages.txt" you will have to change the <u>expected</u> string.
