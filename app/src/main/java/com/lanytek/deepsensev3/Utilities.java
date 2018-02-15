package com.lanytek.deepsensev3;

import android.app.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by JC1DA on 3/16/16.
 */
public class Utilities {
    public static void copyFile(Activity activity, final String f) {
        InputStream in;
        try {
            in = activity.getAssets().open(f);
            final File of = new File(activity.getDir("execdir", activity.MODE_PRIVATE), f);

            final OutputStream out = new FileOutputStream(of);

            final byte b[] = new byte[65535];
            int sz = 0;
            while ((sz = in.read(b)) > 0) {
                out.write(b, 0, sz);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class box {
        public float x,y,w,h;
    }

    public static class sortable_bbox {
        public int index;
        public int classid;
        public float [][] probs;
    }

    public static float getColorPixel(int pixel, int color) {
        float value = 0;

        switch (color) {
            case 0:
                value = (float)((pixel >> 16) & 0x000000ff) / 255.0f;
                break;
            case 1:
                value = (float)((pixel >> 8) & 0x000000ff) / 255.0f;
                break;
            case 2:
                value = (float)(pixel & 0x000000ff) / 255.0f;
                break;
        }

        return value;
    }

    public static float colors[][] = { {1.0f,0.0f,1.0f} , {0.0f,0.0f,1.0f} , {0.0f,1.0f,1.0f} , {0.0f,1.0f,0.0f} , {1.0f,1.0f,0.0f} , {1.0f,0.0f,0.0f} };
    public static float get_color(int c, int x, int max)
    {
        float ratio = ((float)x/max)*5;
        int i = (int) Math.floor(ratio);
        int j = (int) Math.ceil(ratio);
        ratio -= i;
        float r = (1-ratio) * colors[i][c] + ratio*colors[j][c];
        //printf("%f\n", r);
        return r;
    }

    public static void convert_yolo_detections(float [] predictions, int classes, int num, int square, int side, int w, int h, float thresh, float [][] probs, box [] boxes, int only_objectness)
    {
        int i,j,n;
        //int per_cell = 5*num+classes;
        for (i = 0; i < side * side; ++i){
            int row = i / side;
            int col = i % side;
            for(n = 0; n < num; ++n){
                int index = i*num + n;
                int p_index = side*side*classes + i*num + n;
                float scale = predictions[p_index];
                int box_index = side*side*(classes + num) + (i*num + n)*4;
                boxes[index].x = (predictions[box_index + 0] + col) / side * w;
                boxes[index].y = (predictions[box_index + 1] + row) / side * h;
                boxes[index].w = (float) (Math.pow(predictions[box_index + 2], ((square != 0) ? 2 : 1)) * w);
                boxes[index].h = (float) (Math.pow(predictions[box_index + 3], ((square != 0) ? 2 : 1)) * h);
                for(j = 0; j < classes; ++j){
                    int class_index = i*classes;
                    float prob = scale*predictions[class_index+j];
                    probs[index][j] = (prob > thresh) ? prob : 0;
                }
                if(only_objectness != 0){
                    probs[index][0] = scale;
                }
            }
        }
    }

    public static void convert_yolo_detections_mt(final float [] predictions, final int classes, final int num, final int square, final int side, final int w, final int h, final float thresh, final float [][] probs, final box [] boxes, final int only_objectness)
    {

        ExecutorService executor = Executors.newFixedThreadPool(4);
        for (int idx = 0; idx < side * side; ++idx){
            final int i = idx;
            Runnable worker = new Runnable() {
                @Override
                public void run() {
                    int row = i / side;
                    int col = i % side;
                    for(int n = 0; n < num; ++n){
                        int index = i*num + n;
                        int p_index = side*side*classes + i*num + n;
                        float scale = predictions[p_index];
                        int box_index = side*side*(classes + num) + (i*num + n)*4;
                        boxes[index].x = (predictions[box_index + 0] + col) / side * w;
                        boxes[index].y = (predictions[box_index + 1] + row) / side * h;
                        boxes[index].w = (float) (Math.pow(predictions[box_index + 2], ((square != 0) ? 2 : 1)) * w);
                        boxes[index].h = (float) (Math.pow(predictions[box_index + 3], ((square != 0) ? 2 : 1)) * h);
                        for(int j = 0; j < classes; ++j){
                            int class_index = i*classes;
                            float prob = scale*predictions[class_index+j];
                            probs[index][j] = (prob > thresh) ? prob : 0;
                        }
                        if(only_objectness != 0){
                            probs[index][0] = scale;
                        }
                    }
                }
            };
            executor.execute(worker);
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static float overlap(float x1, float w1, float x2, float w2)
    {
        float l1 = x1 - w1/2;
        float l2 = x2 - w2/2;
        float left = l1 > l2 ? l1 : l2;
        float r1 = x1 + w1/2;
        float r2 = x2 + w2/2;
        float right = r1 < r2 ? r1 : r2;
        return right - left;
    }

    public static float box_intersection(box a, box b)
    {
        float w = overlap(a.x, a.w, b.x, b.w);
        float h = overlap(a.y, a.h, b.y, b.h);
        if(w < 0 || h < 0) return 0;
        float area = w*h;
        return area;
    }

    public static float box_union(box a, box b)
    {
        float i = box_intersection(a, b);
        float u = a.w*a.h + b.w*b.h - i;
        return u;
    }

    public static float box_iou(box a, box b)
    {
        return box_intersection(a, b)/box_union(a, b);
    }

    public static void do_nms_sort(box [] boxes, float [][] probs, int total, int classes, float thresh)
    {
        int i, j, k;
        sortable_bbox [] s = new sortable_bbox[total];
        for(i = 0 ; i < s.length ; i++)
            s[i] = new sortable_bbox();

        for(i = 0; i < total; ++i){
            s[i].index = i;
            s[i].classid = 0;
            s[i].probs = probs;
        }

        for(k = 0; k < classes; ++k){
            for(i = 0; i < total; ++i){
                s[i].classid = k;
            }

            Arrays.sort(s, new Comparator<sortable_bbox>() {
                @Override
                public int compare(sortable_bbox a, sortable_bbox b) {
                    float diff = a.probs[a.index][a.classid] - b.probs[b.index][b.classid];
                    if (diff < 0) return 1;
                    else if (diff > 0) return -1;
                    return 0;
                }
            });

            for(i = 0; i < total; ++i){
                if(probs[s[i].index][k] == 0) continue;
                box a = boxes[s[i].index];
                for(j = i+1; j < total; ++j){
                    box b = boxes[s[j].index];
                    if (box_iou(a, b) > thresh){
                        probs[s[j].index][k] = 0;
                    }
                }
            }
        }
    }

}
