How to make a simple augmented reality app
===============================================
by Coby Plain and Ali Muzaffar

Overview
---------------

This tutorial will over the following points

- How to get compass data
- How to get GPS location
- How to do a basic image overlay – and why this is important
- Using GPS and compass data to interpret points around you
- Plotting points on the screen
- Putting it all together


Let's get started
-------------------

Open a new project and set up the permissions in the `AndroidManifest.xml`. We will need access to the camera as well as both the course and fine location.

    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    
    <uses-feature android:name="android.hardware.camera" required="true" />
    <uses-feature android:name="android.hardware.camera.autofocus" android:required="false" >

We will be targeting devices using Gingerbread (Android Version 2.3, API 10) so to adhere to this we will not be using the latest sensor API. Thus you will note that some of the code we will use is deprecated - so again this is so we can keep the code backwards compatible.

Using the compass and GPS
-----------------------------

To use the compass we will need to use the sensor manager. We will need to declare this in our main activity using the following code:

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private final SensorEventListener mListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            Log.d(TAG, "sensorChanged (" + event.values[0] + ", " + event.values[1] + ", 
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

Right now it doesn't do much. The method `onSensorChanged()` is where we are going to be getting all our compass data, it will be called every time the compass detects a change. All Android devices have super sensitive compasses so you may want to add a little something to make it limit itself but for now we will leave it the way it is.

The other thing we will need to declare is a `LocationManager`, this will be handling all of our GPS data.

    LocationManager locMgr;

Now that we have them both declared we initialise them in `onCreate()`.

    mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    setContentView(R.layout.activity_main);
    
    locMgr = (LocationManager) this.getSystemService(LOCATION_SERVICE); // <2>
    LocationProvider high = locMgr.getProvider(locMgr.getBestProvider(LocationUtils.createFineCriteria(), true));

    // using high accuracy provider... to listen for updates
    locMgr.requestLocationUpdates(high.getName(), 0, 0f, new LocationListener() {

        public void onLocationChanged(Location location) {
            // do something here to save this new location
        }

        public void onStatusChanged(String s, int i, Bundle bundle) {
        
        }

        public void onProviderEnabled(String s) {
            // try switching to a different provider
        }

        public void onProviderDisabled(String s) {
            // try switching to a different provider
        }
    });

As you can see the `LocationManager` uses a helper class called `LocationUtils` this can be pulled into your project from anyone of the projects in this repo. On Android location can be either coarse or fine. Course location is a rough estimate of your location based on Wi-Fi or mobile connections, the advantage of course is it gets your location very quickly. Fine location is very precise - normally around 50 meters variance, this is determined using the GPS on a device. While fine location data is far more accurate it does take a while to determine and thus if you use just it your app could seem very slow. A smarter way is to use your coarse location until you have a fix with the GPS. As you can see int the listener, right now we aren't doing anything with the location data but this will change soon.

Before we move on it is important to know that using a resource like the compass is exactly the same as using a resource like the camera. Other apps can't use it unless we give it up. If the user backgrounds our app it's pretty safe to say that we don't still need the compass so it is good practice to release it in `onPause()`. This also means we will need it back again in `onResume()`

    @Override
    protected void onResume() {
        super.onResume();

    mSensorManager.registerListener(mListener, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mListener);
        super.onPause();
    }

So if you run your project now you will see a blank screen! Not very interesting..... But if you look in your logcat you should be able to see a huge stream of compass data. You should be able to see the first column change as you turn your device around - this is the data we will be working with. For something more complex you could also use the other columns, I suggest you take a moment to have a play and see how it all works.

- The code up to here matches the first step in the repo.


The next step
----------------------------

So we have compass data and we have location data but we aren't doing anything with it. To make an augmented reality app we will need to do some drawing, so let’s get it all ready! The second project in the repo you will has a class called `dpiUtils` this is a helper class like `locationUtils` we won't be going over it in detail so just drop it into your project. I do recommend having a peek inside to see how it all works, you may find a use for it somewhere else! While your dragging classes across also grab `CameraSurfaceView` this class is simply eye candy for our app, without it everything still works but it's not as nice. Our `CameraSurfaceView` is just a slightly modified version of the `CamerSurfaceView` from the  Google API demos, so we won't go through it either.

The last thing we need in getting set up is a place to draw. So create a new class called `DrawSurfaceView` that extends `View`. This is where all the interesting stuff will be happening.

Composing the layout
---------------------

In our layout xml file we will now need to add the two new views. To have it look like the app is 'drawing' on the camera we will need to put them over the top of one another. One great way of doing this is using a `FrameLayout` however you could just as easily use a `RelativeLayout`. Make sure to put the camera first, otherwise it will be drawn over our `DrawSurfaceView` and we won't see anything. So our layout should look like this:

    <FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        android:id="@+id/activity_main"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical" >
    
        <com.cobyplain.augmentreality.CameraSurfaceView
            android:id="@+id/cameraSurfaceView"
            android:layout_width="fill_parent"
            android:layout_height="fill_parent" />
        
        <com.cobyplain.augmentreality.DrawSurfaceView
            android:id="@+id/drawSurfaceView"
            android:layout_width="fill_parent"
            android:layout_height="fill_parent" />
    
    </FrameLayout>


Send the compass data
-----------------------

Ok so we are going to take a slight detour now to make sure we give our `DrawSurfaceView` everything we will need later on.

First we will need to declare and initialise our `DrawSurfaceView` in the main activity.

    private DrawSurfaceView mDrawView;

    // in onCreate()
    mDrawView = (DrawSurfaceView) findViewById(R.id.drawSurfaceView);

now we have access to the `DrawSurfaceView` we can start passing data to it! Remember the `SensorManager`? Now we need to go back and give it some real purpose.

    private final SensorEventListener mListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (mDrawView != null) {
                mDrawView.setOffset(event.values[0]);
                mDrawView.invalidate();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

You can see here that re have replaced the debug output with a `setOffset()` method. Don't worry about any errors, we will make `setOffset` soon. you can also see a call to invalidate `DrawSurfaceView`, what this will do is force `DrawSurfaceView` to redraw its canvas every time the compass reports a change. If you remember how the logcat looked this is very often so I suggest if your using this somewhere else you may want to do a check to see if it has moved more than a degree or something similar. For our purposes it doesn't matter.

Receive the compass data
-------------------------

In `DrawSurfaceView` declare and initialise a double called `OFFSET`. This will hold the current value sent by the compass.

    private double OFFSET = 0d;

Then all we need to do is set it!

    public void setOffset(float offset) {
        this.OFFSET = offset;
    }

super easy!

The same for our location
-----------------------------

In `onLocationChanged` our `LocationManager` needs:

    mDrawView.setMyLocation(location.getLatitude(), location.getLongitude());
    mDrawView.invalidate();

and in the `DrawSurfaceView`

    public void setMyLocation(double latitude, double longitude) {
        me.latitude = latitude;
        me.longitude = longitude;
    }

Don't worry about `me`, we will be setting this up next.


Having something to draw
---------------------------

So we have our camera, we have our canvas and we have our data, so what's missing? A location to draw! We will be creating an object to hold all the information pertaining to each location we want to draw. This could contain anything from a name to a picture to a layout, all we need is a latitude, a longitude, a name and x, y coordinates for our purposes. So create a class called `Point`, it should look like this:

    public class Point {
        public double longitude = 0f;
        public double latitude = 0f;
        public String description;
        public float x, y = 0;
        
        public Point(double lat, double lon, String desc) {
            this.latitude = lat;
            this.longitude = lon;
            this.description = desc;
        }
    }

I will say now that this class really should have 'getters and setters'. As this tutorial was originally made and delivered as a presentation we kept it simple for the interest of time. If you do use 'getters and setters' (and you really should) remember this later when we access these variables.

Now what we will do is set up some `Point`s to use. We are going to hard code these values for demonstration but you could easily work in values from a web service or another source. So in our `DrawSurfaceView` 

    public static ArrayList<Point> props = new ArrayList<Point>();
    static {
        props.add(new Point(90d, 110.8000, "North Pole"));
        props.add(new Point(-90d, -110.8000, "South Pole"));
        props.add(new Point(-33.870932d, 151.8000, "East"));
        props.add(new Point(-33.870932d, 150.8000, "West"));
    }

and we will also make a `Point` for our device

    Point me = new Point(-33.870932d, 151.204727d, "Me");

The initial latlong for `me` is arbitrary as we set it using our `LocationManager` before it is used. Also the East and West points will look a little strange to anyone not in the centre of Sydney, they are dummy points we used for example - if you want you could look up the latlong of points to the east and west of you and use them.

Setting up bitmaps and paint
--------------------------------

So we have our points, now we need something to represent them with. In the repo under steps 2, 3 and 4 you should find our resources (they won't change) which you will need to copy into your project - or you can use images of your own! First we will need to declare the bitmaps and the paint we will use

    Paint mPaint = new Paint();
    private Bitmap[] mSpots, mBlips;
    private Bitmap mRadar;

The spots will be drawn on the screen and the blips will be used for a radar. Now let’s initialise them in our `DrawSurfaceView' constructor:

    mPaint.setColor(Color.GREEN);
    mPaint.setTextSize(50);
    mPaint.setStrokeWidth(DpiUtils.getPxFromDpi(getContext(), 2));
    mPaint.setAntiAlias(true);
    
    mRadar = BitmapFactory.decodeResource(context.getResources(), R.drawable.radar);
    
    mSpots = new Bitmap[props.size()];
    for (int i = 0; i < mSpots.length; i++) 
        mSpots[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.dot);

    mBlips = new Bitmap[props.size()];
    for (int i = 0; i < mBlips.length; i++)
        mBlips[i] = BitmapFactory.decodeResource(context.getResources(), R.drawable.blip);

it is also very important to know that when we set up a `View` in a layout we need to use a different constructor from normal. It will look like this before you fill it:

    public DrawSurfaceView(Context context, AttributeSet set) {
        super(context, set);
    }

Screen  width and height
------------------------------

To do our drawing we will also need the screen width and height. This is so we have values we can use to position the points. First we will declare two doubles called `screenWidth` and `screenHeight`

    private double screenWidth, screenHeight = 0d;

then we set them in `onSizeChanged` - this also means if you want the user to be able to change the orientation of the app the values will be updated to the new width and height.

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = (double) w;
        screenHeight = (double) h;
    }

Basic drawing
------------------------

We now have everything we need for simple drawing. As `DrawSurfaceView` extends `View` is has access to `onDraw()`, this is what does our drawing and is also what gets called when `DrawSurfaceView` is invalidated.

We will override `onDraw()` and put in this:

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

You can see that we don't use `OFFSET` this is because this isn't what our final `onDraw()` will look like. What we do is loop through all the props and for each one assign a x and y value. This translates to an x and y position on the screen. We need to subtract the centre of the `spot`s because when we give an x and y position to draw an image from it is the position of the top left corner of the image. You can see here we have split the screen up and placed the `spot`s equally far apart across the screen.

- The code up to here matches the first second in the repo.

Your turn
--------------

So this is the part of our tutorial where you have a challenge (of course you can skip it but I hope you give it a shot). Step 3 in the repo is set up with all the code from before but the `onDraw()` is empty. Also you will see there are two new methods `distInMeters()` and `bearing()`. These two methods are going to be used to determine where on the screen to draw the points.

- Your first challenge is to move the `spot`s across the screen and have them be positioned where they are in the real world. You won't yet need to use `distInMeters()`


How you do it
----------------

    for (int i = 0; i < mSpots.length; i++) {
        Bitmap spot = mSpots[i];
        Point u = props.get(i);
        
        if (spot == null)
            continue;
        
        double angle = bearing(me.latitude, me.longitude, u.latitude, u.longitude) - OFFSET;
        double xPos, yPos;
        
        if(angle < 0)
            angle = (angle+360)%360;
        
        double posInPx = angle * (screenWidth / 90d);
        
        int spotCentreX = spot.getWidth() / 2;
        int spotCentreY = spot.getHeight() / 2;
        xPos = posInPx - spotCentreX;
        
        if (angle <= 45) 
            u.x = (float) ((screenWidth / 2) + xPos);
        
        else if (angle >= 315) 
            u.x = (float) ((screenWidth / 2) - ((screenWidth*4) - xPos));
        
        else
            u.x = (float) (float)(screenWidth*9); //somewhere off the screen
        
        u.y = (float)screenHeight/2 + spotCentreY;
        canvas.drawBitmap(spot, u.x, u.y, mPaint); //camera spot
        canvas.drawText(u.description, u.x, u.y, mPaint); //text
    }

So you can see we loop through each point like before but this time we use the `angle` to position the spot. As the `OFFSET` is constantly changing, so too is the `angle` and thus the `spot` moves.

Round two!
---------------

- You second challenge is to use the same method and thinking to move the points around on the radar. This time you will need `distInMeters()`. Also remember the radar is circular and you will need to think about how points move around it.

How you do it
-----------------

    for (int i = 0; i < mBlips.length; i++) {
        Bitmap blip = mBlips[i];
        Point u = props.get(i);
        double dist = distInMetres(me, u);
        
        if (blip == null)
            continue;
        
        if(dist > 70)
            dist = 70; //we have set points very far away for demonstration
        
        double angle = bearing(me.latitude, me.longitude, u.latitude, u.longitude) - OFFSET;
        double xPos, yPos;
        
        if(angle < 0)
            angle = (angle+360)%360;
        
        xPos = Math.sin(Math.toRadians(angle)) * dist;
        yPos = Math.sqrt(Math.pow(dist, 2) - Math.pow(xPos, 2));
    
        if (angle > 90 && angle < 270)
            yPos *= -1;
        
        int blipCentreX = blip.getWidth() / 2;
        int blipCentreY = blip.getHeight() / 2;
        
        xPos = xPos - blipCentreX;
        yPos = yPos + blipCentreY;
        canvas.drawBitmap(blip, (radarCentreX + (int) xPos), (radarCentreY - (int) yPos), mPaint); //radar blip
        
    }

You can see here we have used some trig to position the radar blips but most of the code is the same. You can see we use Pythagoras to position `yPos` this will always return a positive value and thus we need to invert it to draw on the bottom half of the radar. A better way to position `yPos` would be to use the line:

    yPos = Math.cosMath.toRadians(angle)) * xPos;

and then there would be no need to invert the value.

Finally
-----------

We have some working code, we can optimise it a lot - a few ways have been mentioned already but there are a number of other ways also. The last thing to do is combine the radar loop and the spot loop into one. An example of this can be seen in Step 4 in the repo.


#####Who we are
Coby Plain - apps on Google Play at <http://bit.ly/cplainapps>

Ali Muzaffar - apps on Google Play at <http://bit.ly/amplayapps> 


- This tutorial was designed to be presented at an Android Developers meet-up in Sydney, Australia. I have converted it into written form so that the repo can be used by others. If for any reason it is unclear or confusing I apologise
