package com.auxgroup.mutipointtouch;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {
private ImageView iv;
    //缩放控制
    private Matrix matrix=new Matrix();
    private Matrix savedmatrix=new Matrix();
    //不同状态的表示
    private static  final  int NONE=0;
    private static  final  int DRAG=1;
    private static  final  int ZOOM=2;
    private int mode=NONE;

    private PointF startpoint=new PointF();
    private PointF midpoint=new PointF();
    private float oriDis=1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv= (ImageView) findViewById(R.id.imageView);
        iv.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ImageView view= (ImageView) v;
        switch (event.getAction()&MotionEvent.ACTION_MASK){
            // 单指
            case MotionEvent.ACTION_DOWN:
                matrix.set(view.getImageMatrix());
                savedmatrix.set(matrix);
                startpoint.set(event.getX(),event.getY());
                mode=DRAG;
                break;
            // 双指
            case MotionEvent.ACTION_POINTER_DOWN:
                oriDis=distance(event);
                if (oriDis >10f) {
                    savedmatrix.set(matrix);
                    midpoint=middle(event);
                    mode=ZOOM;
                }
                break;
            // 手指放开
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            // 单指滑动事件
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // 是一个手指拖动
                    matrix.set(savedmatrix);
                    matrix.postTranslate(event.getX() - startpoint.x, event.getY() - startpoint.y);
                } else if (mode == ZOOM) {
                    // 两个手指滑动
                    float newDist = distance(event);
                    if (newDist > 10f) {
                        matrix.set(savedmatrix);
                        float scale = newDist / oriDis;
                        matrix.postScale(scale, scale, midpoint.x, midpoint.y);
                    }
                }
                break;

        }
        view.setImageMatrix(matrix);
        return true;
    }
    // 计算两个触摸点之间的距离
    private float distance(MotionEvent event){
        float x=event.getX(0)-event.getX(1);
        float y=event.getY(0)-event.getY(1);
        return (float) Math.sqrt(x*x+y*y);
    }
    // 计算两个触摸点的中点
    private PointF middle(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
    }
}
