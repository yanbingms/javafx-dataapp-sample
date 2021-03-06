/*
 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.javafx.experiments.dataapp.client.historytab;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import java.util.Collection;

public class TimeRangeSelector extends Region {
    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();
    private final AreaChart<Number,Number> chart = new AreaChart<>(xAxis,yAxis);
    private final Handle startHandle = new Handle();
    private final Handle endHandle = new Handle();
    private double chartPlotX, dragOffset;
    private Runnable dateChangeListener;
    
    private final DoubleProperty startValue = new SimpleDoubleProperty(10) {
        @Override protected void invalidated() { requestLayout(); }
    };
    private final DoubleProperty endValue = new SimpleDoubleProperty(20) {
        @Override protected void invalidated() { requestLayout(); }
    };
    
    public void setData(Collection<XYChart.Series<Number, Number>> data) {
        chart.getData().addAll(data);
    }
    
    public TimeRangeSelector() {
        getStyleClass().add("TimeRangeSelector");
        setPrefSize(1200,350);
        setMaxSize(Double.MAX_VALUE,350);
        chart.setLegendVisible(false);
        chart.setTitle(null);
        xAxis.setLabel(null);
        yAxis.setLabel(null);
        getChildren().addAll(chart, startHandle, endHandle);
        
        Platform.runLater(new Runnable() {
            @Override public void run() {
                requestLayout();
            }
        });
        startHandle.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                dragOffset = (startHandle.getWidth()/2) - e.getX();
            }
        });
        startHandle.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                if (dateChangeListener != null) dateChangeListener.run();
            }
        });
        startHandle.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                double posInUs = e.getSceneX() - localToScene(0, 0).getX() - dragOffset;
                double pixelPosOnChart = posInUs - chartPlotX;
                Number mouseValue = xAxis.getValueForDisplay(pixelPosOnChart);
                if (mouseValue != null && 
                        mouseValue.doubleValue() >= xAxis.getLowerBound() && 
                        mouseValue.doubleValue() <= xAxis.getUpperBound() &&
                        mouseValue.doubleValue() < endValue.get()) {
                    startValue.set(mouseValue.doubleValue());
                }
            }
        });
        endHandle.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                dragOffset = (endHandle.getWidth()/2) - e.getX();
            }
        });
        endHandle.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                if (dateChangeListener != null) dateChangeListener.run();
            }
        });
        endHandle.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
                double posInUs = e.getSceneX() - localToScene(0, 0).getX() - dragOffset;
                double pixelPosOnChart = posInUs - chartPlotX;
                Number mouseValue = xAxis.getValueForDisplay(pixelPosOnChart);
                if (mouseValue != null && 
                        mouseValue.doubleValue() >= xAxis.getLowerBound() && 
                        mouseValue.doubleValue() <= xAxis.getUpperBound() &&
                        mouseValue.doubleValue() > startValue.get()) {
                    endValue.set(mouseValue.doubleValue());
                }
            }
        });
    }

    public XYChart<Number, Number> getChart() {
        return chart;
    }

    public Runnable getDateChangeListener() {
        return dateChangeListener;
    }

    public void setDateChangeListener(Runnable dateChangeListener) {
        this.dateChangeListener = dateChangeListener;
    }

    @Override protected double computePrefHeight(double width) {
        return 200;
    }

    @Override protected double computePrefWidth(double height) {
        return 1000;
    }

    @Override protected void layoutChildren() {
        final double h = getHeight();
        // layout chart
        chart.resize(getWidth(), getHeight());
        // we need to cause the chart to layout immeadiatly so that we can query positions
        chart.layout(); 
        // calculate chart plot position relative to us
        Node chartPlotBackground = chart.lookup(".chart-plot-background");
        Node chartPlotArea = chart.lookup(".chart-content");
        chartPlotX = chartPlotArea.getBoundsInParent().getMinX() + 
                chartPlotBackground.getBoundsInParent().getMinX();
        // calculate X positions for start and end
        final double startXPos = chartPlotX + xAxis.getDisplayPosition(startValue.get());
        final double endXPos = chartPlotX + xAxis.getDisplayPosition(endValue.get());
        // layout start handle
        final double startHandleWidth = startHandle.prefWidth(-1);
        startHandle.resizeRelocate(
                startXPos - (startHandleWidth/2), 0, 
                startHandleWidth, h);
        // layout end handle
        final double endHandleWidth = endHandle.prefWidth(-1);
        endHandle.resizeRelocate(
                endXPos - (endHandleWidth/2), 0, 
                endHandleWidth, h);
    }
    
    private class Handle extends Pane {
        private final Pane thumb = new Pane();
        private final Line line = new Line();
        
        public Handle() {
            getStyleClass().add("handle");
            thumb.getStyleClass().add("thumb");
            getChildren().addAll(line,thumb);
        }

        @Override protected double computePrefHeight(double width) {
            return 20;
        }

        @Override protected double computePrefWidth(double height) {
            return getInsets().getLeft() + thumb.prefWidth(height) + getInsets().getRight();
        }

        @Override protected void layoutChildren() {
            final double w = getWidth();
            final double h = getHeight();
            line.setStartX(w/2);
            line.setEndX(w/2);
            line.setStartY(getInsets().getTop());
            line.setEndY(h-getInsets().getBottom());
            thumb.resizeRelocate(
                    getInsets().getLeft(), 
                    h - getInsets().getBottom() - thumb.prefHeight(w), 
                    w - getInsets().getLeft() - getInsets().getRight(), 
                    thumb.prefHeight(w));
        }
    }
    
    public DoubleProperty getLeftValue() {
        return startValue;
    }
    
    public DoubleProperty getRightValue() {
        return endValue;
    }
    
    public void setLeftValue(double value) {
        startValue.set(value);
    }
    
    public void setRightValue(double value) {
        endValue.set(value);
    }
}
