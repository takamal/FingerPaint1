package sample.application.fingerpaint1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.view.Display;
import android.widget.ImageView;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class FingerPaintActivity extends Activity implements OnTouchListener{

	public Canvas canvas;
	public Paint paint;
	public Path path;
	public Bitmap bitmap;
	public float x1, y1;
	public int w, h;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ImageView iv = (ImageView)findViewById(R.id.imageView1);
		Display disp = ((WindowManager)getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay();
		this.w = disp.getWidth();
		this.h = disp.getHeight();
		this.bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		this.paint = new Paint();
		this.path = new Path();
		this.canvas = new Canvas(bitmap);
		
		this.paint.setStrokeWidth(5);
		this.paint.setStyle(Paint.Style.STROKE);
		this.paint.setStrokeJoin(Paint.Join.ROUND);
		this.paint.setStrokeCap(Paint.Cap.ROUND);
		this.canvas.drawColor(Color.WHITE);
		iv.setImageBitmap(bitmap);
		iv.setOnTouchListener(this);
		
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Float x = event.getX();
		Float y = event.getY();
		
		switch (event.getAction()){
		case MotionEvent.ACTION_DOWN:
			path.reset();
			path.moveTo(x, y);
			x1 = x;
			y1 = y;
			break;
		case MotionEvent.ACTION_MOVE:
			path.quadTo(x1, y1, x, y);
			x1 = x;
			y1 = y;
			canvas.drawPath(path, paint);
			path.reset();
			path.moveTo(x, y);
			break;
		case MotionEvent.ACTION_UP:
			if (x == x1 && y == y1)y1 = y1 + 1;
			path.quadTo(x1, y1, x, y);
			canvas.drawPath(path,paint);
			path.reset();
			break;
		}
		ImageView iv = (ImageView)this.findViewById(R.id.imageView1);
		iv.setImageBitmap(bitmap);
		
		return true;
	}
}
