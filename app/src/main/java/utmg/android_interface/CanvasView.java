package utmg.android_interface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import android.content.SharedPreferences;


public class CanvasView extends View {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    Context context;

    private Paint mPaint1;
    private Paint mPaint2;
    private Paint mPaint3;

    private Paint mPaintWP1;
    private Paint mPaintWP2;
    private Paint mPaintWP3;

    private float mX, mY;
    private static final float TOLERANCE = 5;

    ArrayList<Float> xCoordVec1;
    ArrayList<Float> yCoordVec1;
    ArrayList<Float> zCoordVec1;
    ArrayList<Float> xWaypoint1;
    ArrayList<Float> yWaypoint1;

    ArrayList<Float> xCoordVec2;
    ArrayList<Float> yCoordVec2;
    ArrayList<Float> zCoordVec2;
    ArrayList<Float> xWaypoint2;
    ArrayList<Float> yWaypoint2;

    ArrayList<Float> xCoordVec3;
    ArrayList<Float> yCoordVec3;
    ArrayList<Float> zCoordVec3;
    ArrayList<Float> xWaypoint3;
    ArrayList<Float> yWaypoint3;

    SharedPreferences pref;
    SharedPreferences.Editor prefEditor;
    int mode;


    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        pref = c.getSharedPreferences("Pref", 0);
        mode = pref.getInt("mode", 0);

        // we set a new Path
        mPath = new Path();

        // Paint instantiations for trajectories ///////////////////////////////////////////////////
        mPaint1 = new Paint();
        mPaint1.setAntiAlias(true);
        mPaint1.setColor(DataShare.getInstance("quad1").getQuadColour());
        mPaint1.setStyle(Paint.Style.STROKE);
        mPaint1.setStrokeJoin(Paint.Join.ROUND);
        mPaint1.setStrokeWidth(4f);

        mPaint2 = new Paint();
        mPaint2.setAntiAlias(true);
        mPaint2.setColor(DataShare.getInstance("quad2").getQuadColour());
        mPaint2.setStyle(Paint.Style.STROKE);
        mPaint2.setStrokeJoin(Paint.Join.ROUND);
        mPaint2.setStrokeWidth(4f);

        mPaint3 = new Paint();
        mPaint3.setAntiAlias(true);
        mPaint3.setColor(DataShare.getInstance("quad3").getQuadColour());
        mPaint3.setStyle(Paint.Style.STROKE);
        mPaint3.setStrokeJoin(Paint.Join.ROUND);
        mPaint3.setStrokeWidth(4f);
        ////////////////////////////////////////////////////////////////////////////////////////////

        // Paint instantiations for waypoints //////////////////////////////////////////////////////
        mPaintWP1 = new Paint();
        mPaintWP1.setAntiAlias(true);
        mPaintWP1.setStyle(Paint.Style.STROKE);
        mPaintWP1.setStrokeWidth(1);
        mPaintWP1.setStyle(Paint.Style.FILL);
        mPaintWP1.setColor(DataShare.getInstance("quad1").getQuadColour());
        mPaintWP1.setTextSize(25);

        mPaintWP2 = new Paint();
        mPaintWP2.setAntiAlias(true);
        mPaintWP2.setStyle(Paint.Style.STROKE);
        mPaintWP2.setStrokeWidth(1);
        mPaintWP2.setStyle(Paint.Style.FILL);
        mPaintWP2.setColor(DataShare.getInstance("quad2").getQuadColour());
        mPaintWP2.setTextSize(25);

        mPaintWP3 = new Paint();
        mPaintWP3.setAntiAlias(true);
        mPaintWP3.setStyle(Paint.Style.STROKE);
        mPaintWP3.setStrokeWidth(1);
        mPaintWP3.setStyle(Paint.Style.FILL);
        mPaintWP3.setColor(DataShare.getInstance("quad3").getQuadColour());
        mPaintWP3.setTextSize(25);
        ////////////////////////////////////////////////////////////////////////////////////////////

        // instantiate x, y arrays
        xCoordVec1 = new ArrayList<>();
        yCoordVec1 = new ArrayList<>();
        zCoordVec1 = new ArrayList<>();
        xWaypoint1 = new ArrayList<>();
        yWaypoint1 = new ArrayList<>();

        xCoordVec2 = new ArrayList<>();
        yCoordVec2 = new ArrayList<>();
        zCoordVec2 = new ArrayList<>();
        xWaypoint2 = new ArrayList<>();
        yWaypoint2 = new ArrayList<>();

        xCoordVec3 = new ArrayList<>();
        yCoordVec3 = new ArrayList<>();
        zCoordVec3 = new ArrayList<>();
        xWaypoint3 = new ArrayList<>();
        yWaypoint3 = new ArrayList<>();
    }

    // override onSizeChanged
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // your Canvas will draw onto the defined Bitmap
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }


    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPath with the mPaint1 on the canvas when onDraw
        if (mode == 0) {
            canvas.drawPath(mPath, mPaint1);
        }
        else if (mode == 1) {
            for (int i = 0; i < xWaypoint1.size(); i++) {

                canvas.drawText(Integer.toString(i+1), xWaypoint1.get(i), yWaypoint1.get(i), mPaintWP1);
            }
        }
    }

    // when ACTION_DOWN start touch according to the x,y values
    private void startTouch(float x, float y) {
        if (mode == 0) {
            mPath.reset();
            // instantiate x, y arrays
            xCoordVec1 = new ArrayList<>();
            yCoordVec1 = new ArrayList<>();
            zCoordVec1 = new ArrayList<>();
        }

        mPath.moveTo(x, y);
        mX = x;
        mY = y;

        if (mode == 1) {
            xWaypoint1.add(mX);
            yWaypoint1.add(mY);
            xCoordVec1.add(yMeters()); // x-y swap due to screen rotation... or something. I dunno.
            yCoordVec1.add(xMeters());
            zCoordVec1.add(zObject.getInstance().getZ());
            super.onDraw(mCanvas);
        }

    }

    // when ACTION_MOVE move touch according to the x,y values
    private void moveTouch(float x, float y) {
        if (mode == 0) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOLERANCE || dy >= TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
                xCoordVec1.add(yMeters());
                yCoordVec1.add(xMeters());
                zCoordVec1.add(zObject.getInstance().getZ());
//                mPaint1.setAlpha(  (int)((zObject.getInstance().getZ()/pref.getFloat("newAltitude",2))*255.0)  );
                mPaint1.setStrokeWidth(4f);

                Log.i("canvasView_touch_input", Float.toString(xMeters()) + "\t" + Float.toString(yMeters()));
            }
        }
    }

    // when ACTION_UP stop touch
    private void upTouch() { //mPath.lineTo(mX, mY);
    }

    public void clearCanvas() {
        xCoordVec1 = new ArrayList<>();
        yCoordVec1 = new ArrayList<>();
        zCoordVec1 = new ArrayList<>();
        xWaypoint1 = new ArrayList<>();
        yWaypoint1 = new ArrayList<>();

        mPath.reset();
        invalidate();
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                //upTouch();
                invalidate();
                break;
        }
        return true;
    }

    // current ArrayList of coordinate vectors for quad1
    public ArrayList<Float> getxCoordVec1() { return xCoordVec1; }
    public ArrayList<Float> getyCoordVec1() { return yCoordVec1; }
    public ArrayList<Float> getzCoordVec1() { return zCoordVec1; }

    // current ArrayList of coordinate vectors for quad2
    public ArrayList<Float> getxCoordVec2() { return xCoordVec2; }
    public ArrayList<Float> getyCoordVec2() { return yCoordVec2; }
    public ArrayList<Float> getzCoordVec2() { return zCoordVec2; }

    // current ArrayList of coordinate vectors for quad3
    public ArrayList<Float> getxCoordVec3() { return xCoordVec3; }
    public ArrayList<Float> getyCoordVec3() { return yCoordVec3; }
    public ArrayList<Float> getzCoordVec3() { return zCoordVec3; }

    // center x coordinate of bitmap
    public int getCenterX() {
        if(mBitmap != null) {
            return mBitmap.getWidth() / 2;
        }
        return 0;
    }

    // center y coordinate of bitmap
    public int getCenterY() {
        if (mBitmap != null) {
            return mBitmap.getHeight() / 2;
        }
        return 0;
    }

    // transform, normalize and scale x to meters
    public float xMeters() {
        float transX = -(mX - getCenterX());
        if(mBitmap != null) {
            float normX = transX/mBitmap.getWidth();
            return normX * 5;
        }
        return 0;

    }

    // transform, normalize and scale y to meters
    public float yMeters() {
        // transY = -mY + getCenterY
        float transY = -(mY - getCenterY());
        if(mBitmap != null) {
            float normY = transY/mBitmap.getHeight();
            return normY * 3;
        }
        return 0;

    }

}
