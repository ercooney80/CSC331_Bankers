
/**
 * Author: Edward Cooney
 * Class: BankImpl.java
 * Section: CSC331-01
 * Date: 05/01/2014
 * Notes: This class has been modified in five sections to implement the Bankers 
 * Algorithm.  Each modified section is denoted by custom code folds.
 */
import java.io.*;
import java.util.*;

public class BankImpl implements Bank {

    private int n;			// the number of threads in the system
    private int m;			// the number of resources

    private int[] available; 	// the amount available of eah resource 
    private int[][] maximum; 	// the maximum demand of each thread 
    private int[][] allocation;	// the amount currently allocated to each thread	
    private int[][] need;		// the remaining needs of each thread		

    /**
     * Create a new bank with resources.
     */
    public BankImpl(int[] resources) {
        // m is the number of resources
        m = resources.length;
        n = Customer.COUNT;

        // initialize the resources array
        available = new int[m];
        System.arraycopy(resources, 0, available, 0, m);

        // create the array for storing the maximum demand by  each thread
        maximum = new int[Customer.COUNT][];
        allocation = new int[Customer.COUNT][];
        need = new int[Customer.COUNT][];
    }

    /**
     * This method is invoked by a thread when it enters the system. It records
     * its maximum demand with the bank.
     */
    public void addCustomer(int threadNum, int[] maxDemand) {
        maximum[threadNum] = new int[m];
        allocation[threadNum] = new int[m];
        need[threadNum] = new int[m];

        System.arraycopy(maxDemand, 0, maximum[threadNum], 0, maxDemand.length);
        System.arraycopy(maxDemand, 0, need[threadNum], 0, maxDemand.length);
    }

    /**
     * Outputs the state for each thread
     */
    public void getState() {
        System.out.print("Available = [");
        for (int i = 0; i < m - 1; i++) {
            System.out.print(available[i] + " ");
        }
        System.out.println(available[m - 1] + "]");
        System.out.print("\nAllocation = ");
        for (int i = 0; i < n; i++) {
            System.out.print("[");
            for (int j = 0; j < m - 1; j++) {
                System.out.print(allocation[i][j] + " ");
            }
            System.out.print(allocation[i][m - 1] + "]");
        }
        System.out.print("\nMax = ");
        for (int i = 0; i < n; i++) {
            System.out.print("[");
            for (int j = 0; j < m - 1; j++) {
                System.out.print(maximum[i][j] + " ");
            }
            System.out.print(maximum[i][m - 1] + "]");
        }
        System.out.print("\nNeed = ");
        for (int i = 0; i < n; i++) {
            System.out.print("[");
            for (int j = 0; j < m - 1; j++) {
                System.out.print(need[i][j] + " ");
            }
            System.out.print(need[i][m - 1] + "]");
        }

        System.out.println();
    }

    /**
     * Determines whether granting a request results in leaving the system in a
     * safe state or not.
     *
     * @return true - the system is in a safe state.
     * @return false - the system is NOT in a safe state.
     */
    private boolean isSafeState(int threadNum, int[] request) {
        System.out.print("\n Customer # " + threadNum + " requesting ");
        for (int i = 0; i < m; i++) {
            System.out.print(request[i] + " ");
        }

        System.out.print("Available = ");
        for (int i = 0; i < m; i++) {
            System.out.print(available[i] + "  ");
        }

        // first check if there are sufficient resources available
        for (int i = 0; i < m; i++) {
            if (request[i] > available[i]) {
                System.err.println("INSUFFICIENT RESOURCES");
                return false;
            }
        }

        // ok, there are. Now let's see if we can find an ordering of threads to finish
        //initialize the Finish (canFinish) matrix
        // SAFETY ALGORITHM - > FINISH
        boolean[] canFinish = new boolean[n];
        for (int i = 0; i < n; i++) {
            canFinish[i] = false;           
        }
        
        // SAFETY ALGORITHM -> WORK
        // copy the available matrix to avail
        int[] avail = new int[m];
        System.arraycopy(available, 0, avail, 0, available.length);

        // Now decrement avail by the request.
        // Temporarily adjust the value of need for this thread.
        // Temporarily adjust the value of allocation for this thread.
        for (int i = 0; i < m; i++) {
            avail[i] -= request[i];
            need[threadNum][i] -= request[i];
            allocation[threadNum][i] += request[i];
        }

        /**
         * Now try to find an ordering of threads so that each thread can
         * finish.
         */
        for (int i = 0; i < n; i++) {
            // first find a thread that can finish
            for (int j = 0; j < n; j++) {
// <editor-fold> 	
                //FILL IN THE BLANKS HERE!!!!
                //if this thread has not finished (determined from canFinish[j]
                if (!canFinish[j]) {
                //then do the following:
                    //check if there is need of any resource that cannot be accomodated
                    for (int k = 0; k < m; k++) {                    
                        if (need[j][k] <= avail[k]) {        //if no such a need (which means the thread can finish):
                            canFinish[j] = true;                // set its Finish to true, and 
                            for (int l = 0; l < m; l++) {
                                avail[l] += allocation[j][l];   // add its allocation to available 
                            }                                   // (make the allocated resources to it available to others)
                        }       
                    }
                }
// </editor-fold> 				
            } //end for j
        } //end for i
        
// <editor-fold>        
        //Because earlier we temporarily adjusted the value of need and allocation, 
        // we need to restore the value of need and allocation for this thread
        //FILL IN THE BLANKS HERE!!!!
        int t = threadNum;
        for (int j = 0; j < m; j++) {
            need[t][j] +=request[j];        // add back what we requested to our needs
            allocation[t][j] -= request[j]; // subtract what we allocated from request 
        }
// </editor-fold>       
        boolean returnValue = true; //initialize the returned boolean value
        
// <editor-fold> 		             
        // now go through the boolean array and see if all threads could complete
        //if not, set returnValue to false
        //FILL IN THE BLANKS HERE!!!!
        for (int i = 0; i < n; i++) {
            if (!canFinish[i]) { // canFinish[i] == FALSE
               returnValue = false;
               break;           // At least one thread cannot finish, sequence was not safe
            }
        }     
// </editor-fold> 
        return returnValue;
    }

    /**
     * Make a request for resources. This is a blocking method that returns only
     * when the request can safely be satisfied.
     *
     * @return true - the request is granted.
     * @return false - the request is not granted.
     */
    public synchronized boolean requestResources(int threadNum, int[] request) {
        if (!isSafeState(threadNum, request)) {
            //System.out.println("Customer # " + threadNum + " is denied.");
            return false;
        }
// <editor-fold> 
        // if it is safe, allocate the resources to thread threadNum 
        //FILL IN THE BLANKS HERE!!!!
        int t = threadNum;
        for (int i = 0; i < m; i++) {
            available[i] -= request[i];			// Subtract what the thread requested from what is abvailable to other threads
            allocation[t][i] += request[i];		// add the request to what the thread has been allocated
            need[t][i] = maximum[t][i] - allocation[t][i];	// calculate the threads need for resources i
		}
// </editor-fold> 		

         //For debugging:
         System.out.println("Customer # " + threadNum + " using resources.");
         System.out.print("Available = ");
         for (int i = 0; i < m; i++)
         System.out.print(available[i] + "  ");
         System.out.print("Allocated = [");
         for (int i = 0; i < m; i++)
         System.out.print(allocation[threadNum][i] + "  "); 
         System.out.print("]");  
         System.out.print(" Need = [");
         for (int i = 0; i < m; i++)
         System.out.print(need[threadNum][i] + "  "); 
         System.out.print("]");  
         
        return true;
    }

    /**
     * Release resources
     *
     * @param int[] release - the resources to be released.
     */
    public synchronized void releaseResources(int threadNum, int[] release) {
        System.out.print("\n Customer # " + threadNum + " releasing ");
        for (int i = 0; i < m; i++) {
            System.out.print(release[i] + " ");
        }

// <editor-fold>         
        //modify available, allocation, and need matrices
        //FILL IN THE BLANKS HERE!!!!
        int t = threadNum;
        for (int i = 0; i < m; i++) {
            available[i] += release[i];  // add the released resources to what is available
            allocation[t][i] -= release[i];  // subtract the released resources from what was allocated
            need[t][i] += release[i];	// add what was released back to what we need
        }
// </editor-fold> 
        //output available and allocation matrices               
        System.out.print("Available = ");
        for (int i = 0; i < m; i++) {
            System.out.print(available[i] + "  ");
        }

        System.out.print("Allocated = [");
        for (int i = 0; i < m; i++) {
            System.out.print(allocation[threadNum][i] + "  ");
        }
        System.out.print("]");

    }

}
