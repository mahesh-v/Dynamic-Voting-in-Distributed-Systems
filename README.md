# Dynamic-Voting-in-Distributed-Systems
Implementation of the Jajodia-Mutchler voting algorithm for dynamic votes in a distributed network where faults in the network are likely to happen, but consistency is important.


Authors: Mahesh Venkateswaran (mxv142030) and Ronak Shah (rxs144130)

The zip file contains:

1. The source code
2. The ANT build xml file to make the executable jar
3. The output files (expected report is present at the end of each output file)
4. This report file
5. The jar file (located in dist folder)

The jar is to be executed as:
prompt> java -jar AOS3.jar <node_label>
For example, if running on node A, prompt> java -jar AOS3.jar A

To redirect output to a file,
prompt> java -jar AOS.jar A -al | tee output/A.txt

USAGE:
The program is in CLI and offers to following functionalities:

1. Write
Syntax: write<tab><content_to_write>
The <tab> is the \t character and the content can be any content to write.
The node will send out requests for write access, and if it gains access, it will write to the file X.
Content of X is stored in <node_label>/X.txt

2. Joining the network
Syntax: connect_to ABCD
Above syntax will connect the network node to all 4 nodes, A, B, C and D.

4. Disconnecting from nodes
Syntax: disconnect_from ABCD
This will disconnect from all nodes, A, B, C and D.

5. List neighbors
Syntax: neighbors
This will list out all neighbors to this node

6. Quit
Syntax: quit/exit
To gracefully exit the system, ensuring no node is left disconnected.
