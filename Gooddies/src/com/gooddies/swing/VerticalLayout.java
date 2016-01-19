package com.gooddies.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;

/**
 * @author Dmitry
 */
public class VerticalLayout implements LayoutManager2, LayoutManager {

    private boolean fillByWidth = false;
    private int startGap=0;
    private int betweenGap=0;
    private int endGap=0;
    private int sideGap;
    
    public int getStartGap() {
        return startGap;
    }

    public int getSideGap() {
        return sideGap;
    }

    /**
     * @param sideGap 
     */
    public VerticalLayout setSideGap(int sideGap) {
        this.sideGap = sideGap;
        return this;
    }
    
    /**
     * Gap from top of the container to top of the toppest component
     */
    public VerticalLayout setStartGap(int startGap) {
        this.startGap = startGap;
        return this;
    }

    public int getBetweenGap() {
        return betweenGap;
    }

    /**
     * Gap between components
     */
    public VerticalLayout setBetweenGap(int betweenGap) {
        this.betweenGap = betweenGap;
        return this;
    }

    public int getEndGap() {
        return endGap;
    }

    /**
     * Gap from bottom edge of the bottomest component and container bottom edge
     */
    public VerticalLayout setEndGap(int endGap) {
        this.endGap = endGap;
        return this;
    }
    
    
    
    public boolean isFillByWidth() {
        return fillByWidth;
    }

    public VerticalLayout setFillByWidth(boolean feelByWidth) {
        this.fillByWidth = feelByWidth;
        return this;
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0;
    }

    @Override
    public void invalidateLayout(Container target) {
        layoutContainer(target);
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void removeLayoutComponent(Component comp) {
    }

    private Dimension recalculateSize(Container target) {
        int maxWidth = 0;
        int maxHeight = 0;

        int componentCount=target.getComponentCount();
        maxHeight+=startGap;
        maxHeight+=endGap;
        for (int i = 0; i < componentCount; i++) {
            Component c = target.getComponent(i);
            int height = c.getPreferredSize().height;
            int width = c.getPreferredSize().width;
            if (maxWidth < width) {
                maxWidth = width;
            }

            maxHeight += height;
            if(i<componentCount-1){
                maxHeight+=betweenGap;
            }
        }

        if (fillByWidth) {
            maxWidth = target.getWidth();
            maxWidth-=(sideGap*2);
        }else{
            maxWidth+=(sideGap*2);
        }

        
        return new Dimension(maxWidth, maxHeight);
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        return recalculateSize(target);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return recalculateSize(parent);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return recalculateSize(parent);
    }

    @Override
    public void layoutContainer(Container parent) {
        Dimension maxSizes = recalculateSize(parent);
        int currentTop=startGap;
        int doubleSideGap=sideGap*2;
        for (int i = 0; i < parent.getComponentCount(); i++) {
            Component c = parent.getComponent(i);
            int height = c.getPreferredSize().height;
            int width;
            
            if (fillByWidth) {
                width = maxSizes.width-doubleSideGap;
            }else{
                width = c.getPreferredSize().width;
            }

            int left=parent.getWidth()/2-width/2;
            c.setBounds(left, currentTop, width, height);
            currentTop+=height+betweenGap;
        }
    }
}
