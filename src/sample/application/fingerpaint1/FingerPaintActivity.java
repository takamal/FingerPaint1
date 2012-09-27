package sample.application.fingerpaint1;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
//import android.support.v4.app.Fragment.SavedState;
import android.view.Display;
import android.widget.ImageView;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

//���X�g5
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import android.os.Environment;
import android.content.SharedPreferences;
import android.graphics.Bitmap.CompressFormat;

//���X�g9
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

//���X�g12
import android.media.MediaScannerConnection;
import android.net.Uri;

//リスト26
import android.app.AlertDialog;
import android.content.DialogInterface;

public class FingerPaintActivity extends Activity implements OnTouchListener{

	Canvas canvas;
	Paint paint;
	Path path;
	Bitmap bitmap;
	float x1, y1;
	int w, h;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_main);
		ImageView iv = (ImageView)this.findViewById(R.id.imageView1);
		Display disp = ((WindowManager)this.getSystemService(
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
		float x = event.getX();
		float y = event.getY();
		
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
	
	//���X�g�U
	void save(){
		
		SharedPreferences prefs = this.getSharedPreferences(
				"FingerPaintPreferences", MODE_PRIVATE);
		int imageNumber = prefs.getInt("imageNumber", 1);
		File file = null;
		
		if(externalMediaChecker()){
			DecimalFormat form = new DecimalFormat("0000");
			String path = Environment.getExternalStorageDirectory() + "/mypaint/";
			File outDir = new File(path);
			if(!outDir.exists())outDir.mkdir();
			
		do{
			file = new File(path + "img" + form.format(imageNumber) + ".png");
			imageNumber++;
		}while(file.exists());
			if(writeImage(file)){
				scanMedia(file.getPath());
				SharedPreferences.Editor editor = prefs.edit();
				editor.putInt("imageNumber", imageNumber);
				editor.commit();
			}
		}
	}
	

	private boolean writeImage(File file) {
		FileOutputStream fo = null;
		try {
			fo = new FileOutputStream(file); //ディスクに書き出しを行う。
			bitmap.compress(CompressFormat.PNG, 100, fo);
			fo.flush();
			//TODO これはひどい
			fo.close();
		}catch(FileNotFoundException r){
			r.printStackTrace();
		}catch(IOException r) {
//			throw r;
//			System.out.println(e.getLocalizedMessage());
//			return false;
		}finally{
			try {
				fo.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		return true;
	}

	boolean externalMediaChecker() {
		boolean result = false;
		String status = Environment.getExternalStorageState();
		if(status.equals(Environment.MEDIA_MOUNTED))result = true;
		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater mi = getMenuInflater();
		mi.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.menu_save:
				save();
				break;
			case R.id.menu_open:
				Intent intent = new Intent(this, FilePicker.class);
				startActivityForResult(intent, 0);
				break;
			case R.id.menu_color_change:
				final String[] items = getResources().getStringArray(R.array.ColorName);
				final int[] colors = getResources().getIntArray(R.array.Color);
				AlertDialog.Builder ab = new AlertDialog.Builder(this);
				ab.setTitle(R.string.menu_color_change);
				ab.setItems(items, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int item) {
						paint.setColor(colors[item]);
						paint.setStrokeWidth(5);
					}
				});
				ab.show();
				break;
			case R.id.menu_eraser:
					this.paint.setColor(0xffffffff);
					this.paint.setStrokeWidth(27);
					break;
			case R.id.menu_new:
				ab = new AlertDialog.Builder(this);
				ab.setTitle(R.string.menu_new);
				ab.setMessage(R.string.confirm_new);
				ab.setPositiveButton(R.string.button_ok,
						new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								canvas.drawColor(Color.WHITE);
								((ImageView)findViewById(R.id.imageView1)).setImageBitmap(bitmap);
							}
						});
				ab.setNegativeButton(R.string.button_cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
				});
				ab.show();
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	MediaScannerConnection mc;
	
	void scanMedia(final String fp){
		mc  = new MediaScannerConnection(this, new MediaScannerConnection.MediaScannerConnectionClient() {

			@Override
			public void onScanCompleted(String arg0, Uri arg1) {
				disconnect();
			}
			@Override
			public void onMediaScannerConnected() {
				scanFile(fp);
			}
		});
		mc.connect();
	}
	
		void scanFile(String fp){mc.scanFile(fp, "image/png");}
		void disconnect(){mc.disconnect();}
		
	Bitmap loadImage(String path) {
		boolean landscape = false;
		Bitmap bm;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		int oh = options.outHeight;
		int ow = options.outWidth;
		
		if(ow > oh) {
			landscape = true;
			oh = options.outWidth;
			ow = options.outHeight;
		}
		
		options.inJustDecodeBounds = false;
		options.inSampleSize = Math.max(ow/w,  oh/h);
		bm = BitmapFactory.decodeFile(path, options);
		
		if(landscape) {
			Matrix matrix = new Matrix();
			matrix.setRotate(90.0f);
			bm = Bitmap.createBitmap(bm, 0, 0,
					bm.getWidth(), bm.getHeight(), matrix, false);
		}
		
		bm = Bitmap.createScaledBitmap(bm,
				(int)(w), (int)(w*(((double)oh)/((double)ow))), false);
		Bitmap offBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas offCanvas = new Canvas(offBitmap);
		offCanvas.drawBitmap(bm, 0, (h-bm.getHeight())/2, null);
		bm = offBitmap;
		return bm;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK){
			bitmap = loadImage(data.getStringExtra("fn"));
			canvas = new Canvas(bitmap);
			ImageView iv = (ImageView)this.findViewById(R.id.imageView1);
			iv.setImageBitmap(bitmap);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			AlertDialog.Builder ab = new AlertDialog.Builder(this);
			ab.setTitle(R.string.title_exit);
			ab.setMessage(R.string.confirm_new);
			ab.setPositiveButton(R.string.button_ok, 
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog	, int which) {
							finish();
						}				
			});
			ab.setNegativeButton(R.string.button_cancel,
					new DialogInterface.OnClickListener(){

						@Override
						public void onClick(DialogInterface dialog, int which) {
						}				
			});
			ab.show();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
}
