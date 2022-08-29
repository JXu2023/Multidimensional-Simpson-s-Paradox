import java.util.*;
import java.io.*;

/**
 * The algorithms used to find Simpson's Paradoxes in multi-dimensional data sets.
 *
 * @Jay Xu
 * @August 28, 2022
 */
public class MDSP
{
    public static final int size = 8; // number of attributes
    public static final String filename = "loan.csv";
    
    /**
     * Creates the indexes for each value in each attribute.
     * 
     */
     public static void createIndex( HashMap<String, Integer> indexHolder, int[] lengths) throws FileNotFoundException{ 
        File file = new File(filename);
        Scanner scan = new Scanner(file);
        boolean firstLine = true;
        while(scan.hasNextLine()){
            String line = scan.nextLine();
            String vals[] = line.split(",", 0);
            if(firstLine){ 
                firstLine = false;
                continue;
            }
            int count = 0;
            for(String s: vals){
                if(count == size){
                    break;
                }
                // create a distinct key that corresponds with an index
                String key = s + " "+ count; 
                if(!indexHolder.containsKey(key)){
                    // update the amount of elements for each attribute.
                   lengths[count]++; 
                   int i = lengths[count];
                    indexHolder.put(key, i); 
                     
                }
                count++;
            }
        }
    }
    /**
     * Assigns how many bits each attribute needs to move 
     */
    public static int[] getMover(int[] lengths){ 
        int[] bitSize = new int[size];
        bitSize[0] = 0;
        for(int i = 1; i < size; i++){
            bitSize[i] = bitSize[i-1] + (int)(Math.log(lengths[i-1])/Math.log(2)) + 1; // how many bits to move each attribute over
            
        }
        
        return bitSize;
        
    }
    /**
     * Collects statistics (Algorithm 2)
     */
    public static void aggregate(int[] move, HashMap<String, Integer> indexHolder, HashMap<Long, Long> map) throws FileNotFoundException{
        File file = new File(filename); // a multidimensional data set T
        Scanner scan = new Scanner(file);
        boolean firstLine = true;
         HashMap<String, Integer> agg = new HashMap<String, Integer>(); 
         //for each unique record t = (x1,..., xn, y) in T
       while(scan.hasNextLine()){ 
           // count the number of its duplicates in T
           
            String line = scan.nextLine();
            if(firstLine){
                firstLine = false;
                continue;
            }
            if(!agg.containsKey(line)){
                agg.put(line, 0);
            }
            agg.put(line, agg.get(line) + 1);
            
        }
        // for each unique record t = (x1,..., xn, y) in T
        for(String line: agg.keySet()){ 
            String vals[] = line.split(",", 0);
            long[] indexes = new long[size]; 
            int Y = Integer.parseInt(vals[size]) * agg.get(line);
            for(int i = 0; i < size; i++){
                String key = vals[i] + " " + i;
                indexes[i] = indexHolder.get(key); 
            }
            // for each ancestor (x1',...,xn') of (x1,...,xn) in T   
            for(int i = 0; i < Math.pow(2,size); i++){ 
                long l = 0; // the ancestor
                for(int j = 0; j < size; j++){
                    long a = (long)1 << j;
                    if( (a & i) == a){ 
                        l += indexes[j] << move[j];
                    }  
                }
                Long l1 = new Long(l);
                if(!map.containsKey(l1)){
                    map.put(l1, new Long(0));
                }
                // Update counters of the statistics 
                Long l2 = map.get(l1);
                long l3 = l2.longValue(); // the total and count of the ancestor, first 32 bits are total, second 32 are count
                l3 += ( (long)agg.get(line) << 32);
                l3 += Y;
                map.put(l1, l3); 
                }
        }   
    }
    /**
     * Finds all Simpson's Paradoxes using statistics (Algorithm 3)
     */
    public static void findSP(HashMap<Long,Long> map, int[] move, int[] lengths ) {
        // for each non-empty subpopulation c = (x1,..., xn)
        for(Long agg: map.keySet()){ 
            //pruning
            if((map.get(agg).longValue() >> 32 ) < 9){ 
                continue;
            }
             int[] usedIndexes = new int[size];
             long l1 = agg.longValue();
             //for each attribute
             for(int i  = 0; i < size - 1; i++){
                 for(int j = move[i]; j < move[i+1]; j++){
                     long l2 = (long)1 << j;
                     //see if the attribute is present
                     if((l2 & l1) == l2){
                         usedIndexes[i] = 1;
                          break;   
                     }
                 }
             }
             if(l1 >= ((long)1<<move[size-1])){
                 usedIndexes[size-1] = 1;
             }
             // for each dimension Di such that xi = *
             for(int i = 0; i < size; i++){ 
                 if(usedIndexes[i] == 1){
                     continue;
                 }
                 // for each dimension Dj such that xj = * and i != j
                 for(int j = 0; j < size; j++){ 
                     if(usedIndexes[j] == 1 || i == j){
                         continue;
                     }
                     // for each pair of values y, y' ∈ Dom(Di) such that c1 = (x1,...xi-1, y, xi+1, ...xn) and c2 = (x1,...xi-1, y', xi+1, ...xn) are non-empty subpopulations
                     for(long c1 = 1 ; c1 <= lengths[i]; c1++){ 
                         //Check whether (c1, c2,Dj) is a Simpson's Paradox according to Definition 1 (or Definition 2 if Strong paradox is found)
                         long c1L = l1 + (c1 <<move[i]);
                         Long c1LW = new Long(c1L);
                         
                         if(!map.containsKey(c1LW)){
                             continue;
                         }
                         long c1pop = (map.get(c1LW).longValue() >> 32);
                         double c1TotRate = getRate(map, c1LW);
                         for(long c2 = 1; c2 <= lengths[i]; c2++){
                             long c2L = l1 + (c2 <<move[i]);
                             Long c2LW = new Long(c2L);
                             if(!map.containsKey(c2LW)){
                                 continue;
                             }
                             //prune
                             if((map.get(c2LW).longValue() >> 32) + c1pop <= 4 * lengths[j]){
                                 continue;
                             }
                             double c2TotRate = getRate(map, c2LW);
                             boolean stillTrue = true;
                             //use statistics to find paradox
                             if(c1TotRate > c2TotRate){
                                 for(long s = 1; s <= lengths[j] && stillTrue; s++){
                                     
                                     long c1S = c1L + (s <<move[j]);
                                     Long c1SW = new Long(c1S);
                                     long c2S = c2L + (s << move[j]);
                                     Long c2SW = new Long(c2S);
                                     
                                     if(!map.containsKey(c1SW) || !map.containsKey(c2SW)){
                                         stillTrue = false;
                                         break;
                                     }
                                     double c1STotRate = getRate(map, c1SW);
                                     double c2STotRate = getRate(map, c2SW);
                                     if(c1STotRate >= c2STotRate){ // >= for STRONG, > for Neutral
                                         stillTrue = false;
                                         break;
                                     }                                      
                                 }
                                 if(stillTrue){ // there is a paradox 
                                     // do whatever
                                 }
                             }
                         }
                         
                     }
                 }
             }
            
        }
        
    }
    /**
     * Calculates the rate of a subpopulation using its statistics
     */
    public static double getRate(HashMap<Long, Long> map, Long key){ 
        double rate = 0;
        if(map.containsKey(key)){
            Long l1 = map.get(key);
            long l2 =  l1.longValue();
            double inc = l2 % ((long)1 << 32);
            double tot = l2 / ((long)1 << 32);
            rate = inc / tot;  
        }
        return rate;
    }
    /**
     * Example of Algorithm 1
     */
    public static void main(String[] args) throws FileNotFoundException{
        HashMap<String, Integer> indexHolder = new HashMap<>(); // Map to hold indexes
        int[] lengths = new int[size];// the 
        createIndex(indexHolder, lengths);
        int[] move = getMover(lengths);
        HashMap<Long,Long> aggregations = new HashMap<>();
        aggregate(move, indexHolder, aggregations);
        findSP(aggregations, move, lengths);

    }
  
}
