package com.alimuzaffar.compass;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/*
 * Portions (c) 2009 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Coby Plain coby.plain@gmail.com, Ali Muzaffar ali@muzaffar.me
 */

public class DrawSurfaceView extends View {
	Point me = new Point(-33.870932d, 151.204727d, "Me");
	Paint mPaint = new Paint();
	Matrix transform = new Matrix();
	private double OFFSET = 0d; //we aren't using this yet, that will come in the next step
	private double screenWidth, screenHeight = 0d;
	private Bitmap[] mSpots, mBlips;

	public static ArrayList<Point> props = new ArrayList<Point>();
	static {
		props.add(new Point(90d, 110.8000, "North Pole"));
		props.add(new Point(-90d, -110.8000, "South Pole"));
		props.add(new Point(-33.870932d, 151.8000, "East"));
		props.add(new Point(-33.870932d, 150.8000, "West"));
	}

	public DrawSurfaceView(Context c, Paint paint) {
		super(c);
	}

	public DrawSurfaceView(Context context, AttributeSet set) {
		super(context, set);
		mPaint.setColor(Color.GREEN);
		mPaint.setTextSize(50);
		mPaint.setStrokeWidth(DpiUtils.getPxFromDpi(getContext(), 2));
		mPaint.setAntiAlias(true);
		
		mSpots = new Bitmap[props.size()];
		for (int i = 0; i < mSpots.length; i++) 
			mSpots[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.dot);

		mBlips = new Bitmap[props.size()];
		for (int i = 0; i < mBlips.length; i++)
			mBlips[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.blip);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		Log.d("onSizeChanged", "in here w=" + w + " h=" + h);
		screenWidth = (double) w;
		screenHeight = (double) h;

	}

	@Override
	protected void onDraw(Canvas canvas) {

		for (int i = 0; i < mBlips.length; i++) {
			Bitmap blip = mBlips[i]; //we aren't drawing these yet
			Bitmap spot = mSpots[i];
			Point u = props.get(i);
			
			if (spot == null ||  blip == null)
				continue;
			
			int spotCentreX = spot.getWidth() / 2;
			int spotCentreY = spot.getHeight() / 2;
			
			u.x = (float)screenWidth/3 * (i) - spotCentreX;
			u.y = (float)screenHeight/2 + (50*i) - spotCentreY; 
			
			canvas.drawBitmap(spot, u.x, u.y, mPaint);
			canvas.drawText(u.description, u.x, u.y, mPaint);
		}
	}

	public void setOffset(float offset) {
		this.OFFSET = offset;
	}

	public void setMyLocation(double latitude, double longitude) {
		me.latitude = latitude;
		me.longitude = longitude;
	}

}
