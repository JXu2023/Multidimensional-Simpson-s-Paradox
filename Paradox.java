
/**
 * Write a description of class Paradox here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Paradox
{
    private long sibling1;
    private long sibling2;

    private int separator;
    
    public Paradox(long s1, long s2, int s)
    {
        this.sibling1 = s1;
        this.sibling2 = s2;
        this.separator = s;
    }

    public long getS1(){
        return sibling1;
    }
    public long getS2(){
        return sibling2;
    }
    public int getSeparator(){
        return separator;
    }
    public boolean equals(Object o){
        Paradox p = (Paradox)o;
        
        return p.sibling1 == this.sibling1 && p.sibling2 == this.sibling2 && this.separator == p.separator;
    }
}
