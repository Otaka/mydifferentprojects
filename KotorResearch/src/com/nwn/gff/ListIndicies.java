package com.nwn.gff;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author sad
 */
public class ListIndicies {
    private List<int[]>indicies=new ArrayList<>();;
    private HashMap<Integer,int[]>byteOffsetToIndiciesArray=new HashMap<>();;
    
    public ListIndicies() {
    }

    public void add(int byteOffset,int[]indicies){
        byteOffsetToIndiciesArray.put(byteOffset, indicies);
        this.indicies.add(indicies);
    }
    
    public int[] getIndicies(int byteOffset) {
        return byteOffsetToIndiciesArray.get(byteOffset);
    }
    
    
    
}
