package com.tomk.android.stockapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.tomk.android.stockapp.models.ChartItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tom Kowszun on 6/1/2018.
 */
public class GraphChartView extends View {

    private RectF graphBounds;
    private final Paint graphPaint;
    private List<ChartItem> chartItems;
    private int diagramType = GraphChart.BAR_GRAPH;

    private final int strokeWidth = 2;
    final Paint polyPaint = new Paint();

    public int polyFillColor = 0;
    public int standardGraphColor = 0;

    /**
     * Construct a GraphChartView
     *
     * @param context
     */
    public GraphChartView(Context context, List<ChartItem> chartItems) {
        super(context);
        this.chartItems = chartItems;

        polyFillColor = ContextCompat.getColor(getContext(), android.R.color.holo_blue_light);
        standardGraphColor = ContextCompat.getColor(getContext(), android.R.color.holo_green_light);


        graphPaint = new Paint();
        graphPaint.setAlpha(150);

        polyPaint.setColor(polyFillColor);
        polyPaint.setAlpha(100);
        polyPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }


    // draw the actual graph from the data points
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTimeItems(canvas);
    }

    private void drawTimeItems(Canvas canvas) {
        if (chartItems != null && chartItems.size() > 0) {
            float oldTop = this.chartItems.get(0).top;
            float oldLeft = this.chartItems.get(0).left;

            if (diagramType == GraphChart.FILL_GRAPH) {
                drawPoly(canvas, chartItems);
                int cnt = 0;
                for (ChartItem it : chartItems) {
                    graphPaint.setColor(standardGraphColor);
                    graphPaint.setStrokeWidth(strokeWidth);
                    canvas.drawLine(oldLeft, oldTop, it.left, it.top, graphPaint);
                    cnt++;
                    oldTop = it.top;
                    oldLeft = it.left;
                }
            } else {
                int cnt = 0;
                for (ChartItem it : chartItems) {
                    graphPaint.setStrokeWidth(strokeWidth);
                    graphPaint.setColor(standardGraphColor);

                    if (diagramType == GraphChart.BAR_GRAPH) {
                        graphPaint.setStrokeWidth(1);
                        canvas.drawRect(it.left, it.top, it.right, graphBounds.bottom, graphPaint);
                    } else if (diagramType == GraphChart.LINEAR_GRAPH) {
                        if (cnt == 0) {
//                            canvas.drawCircle(it.left, it.top, 10, graphPaint); // for testing
                            canvas.drawPoint(it.left, it.top, graphPaint);
                        } else {
                            canvas.drawLine(oldLeft, oldTop, it.left, it.top, graphPaint);
//                            canvas.drawCircle(it.left, it.top, 10, graphPaint);

                        }
                        oldTop = it.top;
                        oldLeft = it.left;

                    } else if (diagramType == GraphChart.MINOR_GRAPH) {

                        graphPaint.setStrokeWidth(4);
                        if (cnt == 0) {
                            canvas.drawPoint(it.left, it.top, graphPaint);
                        } else {
                            canvas.drawLine(oldLeft, oldTop, it.left, it.top, graphPaint);
                        }
                        oldTop = it.top;
                        oldLeft = it.left;
                    } else if (diagramType == GraphChart.CANDLE_STICK_GRAPH) {

//                        graphPaint.setColor(Color.CYAN);
//                        canvas.drawCircle(it.left, it.top, 5, graphPaint);
//                        graphPaint.setColor(Color.RED);
//                        canvas.drawCircle(it.left, it.bottom, 5, graphPaint);
//                        graphPaint.setColor(Color.YELLOW);
//                        canvas.drawCircle(it.right, it.top, 5, graphPaint);
//                        graphPaint.setColor(Color.WHITE);
//                        canvas.drawCircle(it.right, it.bottom, 5, graphPaint);

                        graphPaint.setColor(it.color);
                        graphPaint.setStrokeWidth(3);

                        canvas.drawRect(it.left, it.top, it.right, it.bottom, graphPaint);

                        canvas.drawLine(it.middle, it.top, it.middle, it.highWickTip, graphPaint);
                        canvas.drawLine(it.middle, it.bottom, it.middle, it.highWickBase, graphPaint);

                        canvas.drawLine(it.middle, it.bottom, it.middle, it.lowWickTip, graphPaint);
                        canvas.drawLine(it.middle, it.bottom, it.middle, it.lowWickBase, graphPaint);

                    } else // effectively default becomes a point graph
                    {
                        canvas.drawPoint(it.left, it.top, graphPaint);
                    }
                    cnt++;
                }
            }
        }
    }

    /**
     * @param canvas The canvas to draw on
     */
    private void drawPoly(Canvas canvas, List<ChartItem> chartItems) {

        if (chartItems.size() < 2) {
            return;
        }

        int lastIndex = chartItems.size() - 1;

        float graphLeft = chartItems.get(0).left;
        float graphBottom = graphBounds.bottom;

        // path
        Path polyPath = new Path();
        polyPath.moveTo(graphLeft, graphBottom);
        int i, len;
        len = chartItems.size();
        for (i = 0; i < len; i++) {
            polyPath.lineTo(chartItems.get(i).left, chartItems.get(i).top);
        }
        polyPath.lineTo(chartItems.get(i - 1).left, graphBottom);

        // draw
        canvas.drawPath(polyPath, polyPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        h = h - GraphChart.boxLineThicknessStat / 2;
        graphBounds = new RectF(0, 0, w, h);
    }

    public void updateData(List<ChartItem> chartItems) {
        this.chartItems = chartItems;

    }

    public void setDiagramType(int diagramType) {
        this.diagramType = diagramType;
    }


}