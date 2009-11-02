/*
    Copyright 2006 Manmohan Reddy

    This file is part of logQL.

    logQL is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    logQL is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with logQL.  If not, see <http://www.gnu.org/licenses/>.

    $Id: QLStackedAreaRenderer.java,v 1.2 2009/10/29 05:11:18 mreddy Exp $
*/
package com.logql.ui;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.CategoryItemRendererState;
import org.jfree.chart.renderer.StackedAreaRenderer;
import org.jfree.data.CategoryDataset;
import org.jfree.ui.RectangleEdge;

public class QLStackedAreaRenderer extends StackedAreaRenderer {
	public static final long serialVersionUID = 15;
	public QLStackedAreaRenderer() {
		super();
	}
    /**
     * Draw a single data item.
     *
     * @param g2  the graphics device.
     * @param state  the renderer state.
     * @param dataArea  the data plot area.
     * @param plot  the plot.
     * @param domainAxis  the domain axis.
     * @param rangeAxis  the range axis.
     * @param dataset  the data.
     * @param row  the row index (zero-based).
     * @param column  the column index (zero-based).
     */
    public void drawItem(Graphics2D g2,
                         CategoryItemRendererState state,
                         Rectangle2D dataArea,
                         CategoryPlot plot,
                         CategoryAxis domainAxis,
                         ValueAxis rangeAxis,
                         CategoryDataset dataset,
                         int row,
                         int column) {

        // plot non-null values...
        Number value = dataset.getValue(row, column);
        if (value == null) {
            return;
        }

        // leave the y values (y1, y0) untranslated as it is going to be be stacked
        // up later by previous series values, after this it will be translated.
        double x1 = domainAxis.getCategoryMiddle(
            column, getColumnCount(), dataArea, plot.getDomainAxisEdge()
        );
        double y1 = 0.0;  // calculate later
        double y1Untranslated = value.doubleValue();

        g2.setPaint(getItemPaint(row, column));
        g2.setStroke(getItemStroke(row, column));

        Polygon p=null;
        if (column != 0) {

            Number previousValue = dataset.getValue(row, column - 1);
            if (previousValue != null) {

                double x0 = domainAxis.getCategoryMiddle(
                    column - 1, getColumnCount(), dataArea, plot.getDomainAxisEdge()
                );
                double y0Untranslated = previousValue.doubleValue();

                // Get the previous height, but this will be different for both y0 and y1 as
                // the previous series values could differ.
                double previousHeightx0Untranslated = getPreviousHeight(dataset, row, column - 1);
                double previousHeightx1Untranslated = getPreviousHeight(dataset, row, column);

                // Now stack the current y values on top of the previous values.
                y0Untranslated += previousHeightx0Untranslated;
                y1Untranslated += previousHeightx1Untranslated;

                // Now translate the previous heights
                RectangleEdge location = plot.getRangeAxisEdge();
                double previousHeightx0 = rangeAxis.valueToJava2D(
                    previousHeightx0Untranslated, dataArea, location
                );
                double previousHeightx1 = rangeAxis.valueToJava2D(
                    previousHeightx1Untranslated, dataArea, location
                );

                // Now translate the current y values.
                double y0 = rangeAxis.valueToJava2D(y0Untranslated, dataArea, location);
                y1 = rangeAxis.valueToJava2D(y1Untranslated, dataArea, location);

                p = null;
                PlotOrientation orientation = plot.getOrientation();
                if (orientation == PlotOrientation.HORIZONTAL) {
                    p = new Polygon();
                    p.addPoint((int) y0, (int) x0);
                    p.addPoint((int) y1, (int) x1);
                    p.addPoint((int) previousHeightx1, (int) x1);
                    p.addPoint((int) previousHeightx0, (int) x0);
                }
                else if (orientation == PlotOrientation.VERTICAL) {
                    p = new Polygon();
                    p.addPoint((int) x0, (int) y0);
                    p.addPoint((int) x1, (int) y1);
                    p.addPoint((int) x1, (int) previousHeightx1);
                    p.addPoint((int) x0, (int) previousHeightx0);
                }
                g2.setPaint(getItemPaint(row, column));
                g2.setStroke(getItemStroke(row, column));
                g2.fill(p);
            }

        }

        // collect entity and tool tip information...
        if (state.getInfo() != null) {
            EntityCollection entities = state.getInfo().getOwner().getEntityCollection();
            Shape shape = new Rectangle2D.Double(x1 - 30.0, y1-30.0 , 60.0,60.0 );
            if (entities != null && shape != null) {
                String tip = null;
                CategoryToolTipGenerator tipster = getToolTipGenerator(row, column);
                if (tipster != null) {
                    tip = tipster.generateToolTip(dataset, row, column);
                }
                String url = null;
                if (getItemURLGenerator(row, column) != null) {
                    url = getItemURLGenerator(row, column).generateURL(dataset, row, column);
                }
                CategoryItemEntity entity =null;
                if(p!=null){
	                entity = new CategoryItemEntity(p, tip, url, dataset, row, dataset.getColumnKey(column), column);

                }else{
	                entity = new CategoryItemEntity(
		                    shape, tip, url, dataset, row, dataset.getColumnKey(column), column);
                }
       
                entities.addEntity(entity);
            }
        }

    }
}
