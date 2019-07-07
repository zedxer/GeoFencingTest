package com.zedxer.testproject

import android.Manifest
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.gun0912.tedpermission.PermissionListener
import com.zedxer.testproject.mapareas.MapAreaManager
import com.zedxer.testproject.mapareas.MapAreaMeasure
import com.zedxer.testproject.mapareas.MapAreaWrapper
import com.zedxer.testproject.services.GPSservices

class ActivityCreateNewLocation : FragmentActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var circleManager: MapAreaManager? = null
    private var radius: EditText? = null
    private var latitude: String? = null
    private var longitude: String? = null
    private var radiusval: String? = null
    private var locationManager: LocationManager? = null
    private var location: Location? = null
    private var myCurrentLocation: Location? = null
    private var mLastLocation: Location? = null
    private var mCurrLocationMarker: Marker? = null
    private var mCircle: Circle? = null
    private var radiusInMeters = 100.0
    private var strokeColor = -0x10000 //Color Code you want
    private var shadeColor = 0x44ff0000
    private var isPermissionGranted = false
    //    GPSTracker gpsTracker;
    private var permissionListener: PermissionListener? = null
    var isServiceAttached = false
    private val connection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is GPSservices.LocationServices) {
                print("service audio service player set")


            }
        }
    }

    private fun startsForegroundService(context: Context, intent: Intent): ComponentName? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            doBindService(intent)
            context.startForegroundService(intent)
        } else {
            doBindService(intent)
            context.startService(intent)
        }
    }


    private fun doBindService(intent: Intent) {
        isServiceAttached = true
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        doUnbindService()
        super.onDestroy()
    }
    private fun doUnbindService() {
        isServiceAttached = false
        unbindService(connection)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createnewlocation)


        getLocaion()
        //        gpsTracker = new GPSTracker(ActivityCreateNewLocation.this);

        radius = findViewById<View>(R.id.radius) as EditText

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        /*       if (isPermissionGranted) {
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    //Got the location!
                    Toast.makeText(ActivityCreateNewLocation.this, "" + location.getLatitude() + location.getLongitude(), Toast.LENGTH_SHORT).show();
                }
            };
            MyLocation myLocation = new MyLocation();
            myLocation.getLocation(this, locationResult);
        }*/
    }


    fun GoBackLocattion(v: View) {
        finish()
    }

    fun GoBackok(v: View) {
        //data collect


        val lname = findViewById<View>(R.id.name) as EditText
        if (radiusval == null || lname.text.toString().matches("".toRegex())) {

            Toast.makeText(applicationContext, "PLease Write name of location and select radius", Toast.LENGTH_SHORT)
                .show()

        } else {


            val i = Intent()
            i.putExtra("Radius", radiusval)
            i.putExtra("Longitude", longitude)
            i.putExtra("Latitude", latitude)
            i.putExtra("Name", lname.text.toString())
            setResult(RESULT_OK, i)
            Toast.makeText(this, radiusval + longitude + latitude, Toast.LENGTH_SHORT).show()

            Log.v("LATLONG", myCurrentLocation?.longitude.toString() + " " + myCurrentLocation?.latitude)
            Log.v("LATLONG", "circle radius $radiusval$longitude$latitude")
            val locationB = Location("point B")
            locationB.latitude = java.lang.Double.parseDouble(latitude!!)
            locationB.longitude = java.lang.Double.parseDouble(longitude!!)

            val distance = myCurrentLocation?.distanceTo(locationB)?.toDouble()
            Toast.makeText(this, "DISTANCE is $distance", Toast.LENGTH_SHORT).show()
            Log.v("LATLONG", "DISTANCE is $distance")

            //            finish();
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        circleManager = MapAreaManager(mMap!!,

            4, Color.RED, Color.HSVToColor(70, floatArrayOf(173f, 216f, 230f)), //styling

            R.drawable.move, R.drawable.resize, //custom drawables for move and resize icons

            0.5f, 0.5f, 0.5f, 0.5f, //sets anchor point of move / resize drawable in the middle

            MapAreaMeasure(
                100.0,
                MapAreaMeasure.Unit.pixels
            ), //circles will start with 100 pixels (independent of zoom level)

            object : MapAreaManager.CircleManagerListener { //listener for all circle events

                override fun onResizeCircleEnd(draggableCircle: MapAreaWrapper) {
                    radius!!.setText(draggableCircle.radius.toString())


                    latitude = draggableCircle.center.latitude.toString()
                    longitude = draggableCircle.center.longitude.toString()
                    radiusval = draggableCircle.radius.toString()
                    // Toast.makeText(ActivityCreateNewLocation.this, "do something on drag end circle: " + draggableCircle, Toast.LENGTH_SHORT).show();
                }

                override fun onCreateCircle(draggableCircle: MapAreaWrapper) {
                    radius!!.setText(draggableCircle.radius.toString())
                    latitude = draggableCircle.center.latitude.toString()
                    longitude = draggableCircle.center.longitude.toString()
                    radiusval = draggableCircle.radius.toString()

                    val intent = Intent(this@ActivityCreateNewLocation, GPSservices::class.java)

                    intent.putExtra("LAT", latitude?.toDouble())
                    intent.putExtra("LONG", longitude?.toDouble())
                    intent.putExtra("RADIUS", longitude?.toDouble())
                   startsForegroundService(this@ActivityCreateNewLocation, intent)
                    // Toast.makeText(ActivityCreateNewLocation.this, "do something on crate circle: " + draggableCircle, Toast.LENGTH_SHORT).show();
                }

                override fun onMoveCircleEnd(draggableCircle: MapAreaWrapper) {
                    radius!!.setText(draggableCircle.radius.toString())
                    latitude = draggableCircle.center.latitude.toString()
                    longitude = draggableCircle.center.longitude.toString()
                    radiusval = draggableCircle.radius.toString()
                    // Toast.makeText(ActivityCreateNewLocation.this, "do something on moved circle: " + draggableCircle, Toast.LENGTH_SHORT).show();
                }

                override fun onMoveCircleStart(draggableCircle: MapAreaWrapper) {
                    radius!!.setText(draggableCircle.radius.toString())
                    latitude = draggableCircle.center.latitude.toString()
                    longitude = draggableCircle.center.longitude.toString()
                    radiusval = draggableCircle.radius.toString()
                    //Toast.makeText(ActivityCreateNewLocation.this, "do something on move circle start: " + draggableCircle, Toast.LENGTH_SHORT).show();
                }

                override fun onResizeCircleStart(draggableCircle: MapAreaWrapper) {
                    radius!!.setText(draggableCircle.radius.toString())
                    latitude = draggableCircle.center.latitude.toString()
                    longitude = draggableCircle.center.longitude.toString()
                    radiusval = draggableCircle.radius.toString()
                    // Toast.makeText(ActivityCreateNewLocation.this, "do something on resize circle start: " + draggableCircle, Toast.LENGTH_SHORT).show();
                }

                override fun onMinRadius(draggableCircle: MapAreaWrapper) {
                    radius!!.setText(draggableCircle.radius.toString())
                    latitude = draggableCircle.center.latitude.toString()
                    longitude = draggableCircle.center.longitude.toString()
                    radiusval = draggableCircle.radius.toString()
                    // Toast.makeText(ActivityCreateNewLocation.this, "do something on min radius: " + draggableCircle, Toast.LENGTH_SHORT).show();
                }

                override fun onMaxRadius(draggableCircle: MapAreaWrapper) {
                    radius!!.setText(draggableCircle.radius.toString())
                    latitude = draggableCircle.center.latitude.toString()
                    longitude = draggableCircle.center.longitude.toString()
                    radiusval = draggableCircle.radius.toString()
                    // Toast.makeText(ActivityCreateNewLocation.this, "do something on max radius: " + draggableCircle, Toast.LENGTH_LONG).show();
                }
            })

        // Add a marker in Sydney and move the camera
        //        LatLng myLoc = new LatLng(24.9150883, 67.0260892);
        //        LatLng myLoc = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        if (location != null)
            myLoc = LatLng(location!!.latitude, location!!.longitude)
        else {
            myLoc = LatLng(0.0, 0.0)
            //            myLoc = new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude());
            //            myLoc = new LatLng(CurrentConditionValues.location.latitude,CurrentConditionValues.location.longitude);
        }

        mMap!!.addMarker(MarkerOptions().position(myLoc).title("MY CURRENT LOCATION"))
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 13f))

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //Location Permission already granted
                mMap!!.isMyLocationEnabled = true
            } else {
                //Request Location Permission
                checkLocationPermission()
            }
        } else {
            mMap!!.isMyLocationEnabled = true
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton("OK") { dialogInterface, i ->
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(
                            this@ActivityCreateNewLocation,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .create()
                    .show()


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }


    internal fun getLocaion() {
        /*
        locationManager = (LocationManager) this
                .getSystemService(Context.LOCATION_SERVICE);


        Criteria locationCritera = new Criteria();
        String providerName = locationManager.getBestProvider(locationCritera,
                true);
        if (providerName != null)
            location = locationManager.getLastKnownLocation(providerName);*/

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //To request location updates
        //        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000,
            5f, object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    mMap!!.clear()
                    myCurrentLocation = location
                    mMap!!.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)))

                    mLastLocation = location
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker!!.remove()
                    }

                    //Place current location marker
                    val latLng = LatLng(location.latitude, location.longitude)
                    val markerOptions = MarkerOptions()
                    markerOptions.position(latLng)
                    markerOptions.title("Current Position")
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                    mCurrLocationMarker = mMap!!.addMarker(markerOptions)

                    val addCircle = CircleOptions().center(latLng).radius(radiusInMeters).fillColor(shadeColor)
                        .strokeColor(strokeColor).strokeWidth(8f)
                    mCircle = mMap!!.addCircle(addCircle)

                    //move map camera
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                    mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))

                }

                override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

                }

                override fun onProviderEnabled(s: String) {

                }

                override fun onProviderDisabled(s: String) {

                }
            })
    }

    companion object {
        var myLoc: LatLng = LatLng(0.0, 0.0)

        /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }*/

        val MY_PERMISSIONS_REQUEST_LOCATION = 99
    }
}
