import java.util.*;
/**
 * ParadoxList class to run experiments on paradoxes.
 *
 * @Jay Xu
 * @September 9, 2022
 */
public class ParadoxList
{
    // instance variables - replace the example below with your own
    private List<Paradox> list;
    private int[] lengths;
    private int size;
    private int[] bitMove;
    private HashMap<Long, Long> aggregations;
    
    /**
     * Constructor, takes (cardinality of attributes, number of bits to move, number of attributes, map of aggregations)
     */
    public ParadoxList(int[] l, int[] b, int s, HashMap<Long, Long> a)
    {
        this.list = new ArrayList<>();
        this.lengths = l;
        this.bitMove = b;
        this.aggregations = a;
    }
    /**
     * adds a paradox to the list
     */
    public void add(Paradox p){
        list.add(p);
    }
    /**
     * gets the dimension of the paradox at the given index
     */
    public int getDimension(int a){
        int count = -1;
        long l1 = list.get(a).getS1();
        for(int i  = 0; i < size - 1; i++){
                 for(int j = bitMove[i]; j < bitMove[i+1]; j++){
                     long l2 = (long)1 << j;
                     if((l2 & l1) == l2){
                         count++;
                          break;   
                     }
                 }
             }
             if(l1 >= ((long)1<<bitMove[size-1])){
            
                 count++;
             }
             return count;
    }
    /**
     * gets the cardinality of the separator at the given index
     */
    public int getSeparator(int a){
        
        return lengths[list.get(a).getSeparator()];
    }
    /**
     * gets a hashmap of cardinalities of separator and the corresponding number of paradoxes
     * 
     */
    public HashMap<Integer, Integer> getAllSeparators(){
        int[] numSep = new int[size];
        for(Paradox p: list){
            numSep[p.getSeparator()]++;
        }
        HashMap<Integer, Integer> hm = new HashMap<>();
        for(int i = 0; i < size; i++){
            int len = lengths[i];
            if(!hm.containsKey(len)){
                hm.put(len, numSep[i]);
                
            }else{
                hm.put(len, hm.get(len) + numSep[i]);
            }
            
            
        }
        return hm;
    }
    /**
     * gets the total population of the two siblings
     */
    public long getPopulation(int a){
        
        Long s1 = new Long(list.get(a).getS1());
         Long s2 = new Long(list.get(a).getS2());
         
         return (aggregations.get(s1).longValue() >> 32) + (aggregations.get(s2).longValue() >> 32);
         
    }
    /**
     * gets the population distrbution of the paradoxes based on buckets
     * 
     */
    public HashMap<Integer, Integer> populationDistribution(int bucket, int bucketNum){
        HashMap<Integer, Integer> hm = new HashMap<>();
        for(int i = 0; i < list.size(); i++){
            int a = (int)this.getPopulation(i);
            a = a / bucket * bucket;
            if(a > bucket * bucketNum){
                a = bucket * (bucketNum + 1);
            }
            if(!hm.containsKey(a)){
                hm.put(a, 1);
            }else{
                hm.put(a, hm.get(a)+1);
            }
        }
        return hm;
    }
    /**
     * returns a list of the total number of paradoxes a record contributes to
     */
    public List<Integer> getContributions(){
        HashMap<Long, Integer> contributions = new HashMap<>();
        for(Paradox p: list){
            Long s1 = new Long(p.getS1());
            Long s2 = new Long(p.getS2());
            if(!contributions.containsKey(s1)){
                contributions.put(s1,  0);
                
            }
            if(!contributions.containsKey(s2)){
                contributions.put(s2, 0);
                
            }
           contributions.put(s1, contributions.get(s1) + 1);
           contributions.put(s2, contributions.get(s2) + 1);
            
        }
        List<Integer> list1 = new ArrayList<>();
        for(Long agg: aggregations.keySet()){
            int[] values = new int[size];
            int counter = 0;
            long l1 = agg.longValue();
            for(int i  = 0; i < size - 1; i++){
                 for(int j = bitMove[i]; j < bitMove[i+1]; j++){
                     long l2 = (long)1 << j;
                     if((l2 & l1) == l2){
                         
                         values[i] = (int)(l1/((long) 1 << bitMove[i])) % lengths[i];
                          break;   
                     }
                 }
             }
            for(int i = 0; i < Math.pow(2, size); i++){
                long l = 0;
                for(int j = 0; j < size; j++){
                    long a = (long)1 << j;
                    if( (a & i) == a){
                        l += values[j] << bitMove[j];
                    
                    }
                }
                Long aa = new Long(l);
                if(contributions.containsKey(aa)){
                    counter += contributions.get(aa);
                }
                
            }
            
            list1.add(counter);
            
        }
        
        
        
        
        return list1;
    }
}
