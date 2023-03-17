package com.deepsaikia.myweather

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.deepsaikia.myweather.models.WeatherResponse
import com.deepsaikia.myweather.network.WeatherService
import com.deepsaikia.myweather.utils.Constants
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import retrofit.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    //used to get the user's current location
    // private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var mProgressDialog: Dialog? = null
    private var mLatitude: Double = 19.0760
    private var mLongitude: Double = 72.8777
    private lateinit var mSharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.hide()
        // getLocationWeatherDetails()

        // mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Declaring a layout (changes are to be made to this)
        // Declaring a textview (which is inside the layout)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)

        // Refresh function for the layout
        swipeRefreshLayout.setOnRefreshListener{

            // Your code goes here
            // In this code, we are just changing the text in the
            // textbox
            Toast.makeText(applicationContext,"Refreshed",Toast.LENGTH_SHORT).show()
            getLocationWeatherDetails()
            showCustomProgressDialog()
            // This line is important as it explicitly refreshes only once
            // If "true" it implicitly refreshes forever
            swipeRefreshLayout.isRefreshing = false
        }



//        //restore the previously stored data
        mSharedPreferences = getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE)
        Log.e(
            "main string stored earlier",
            mSharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA, "").toString()
        )
        setupUI()
//
//        if (!isLocationEnabled()) {
//            Toast.makeText(
//                this, "Your location provider is turned off. Please turn it on.", Toast.LENGTH_SHORT
//            ).show()
//            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//            startActivity(intent)
//        } else {
//            Dexter.withActivity(this).withPermissions(
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.ACCESS_COARSE_LOCATION
//                ).withListener(object : MultiplePermissionsListener {
//                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
//                        if (report!!.areAllPermissionsGranted()) {
//                            requestLocationData()
//                        }
//
//                        if (report.isAnyPermissionPermanentlyDenied) {
//                            Toast.makeText(
//                                this@MainActivity,
//                                "You have denied location permission. Please allow it is mandatory.",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//
//                    override fun onPermissionRationaleShouldBeShown(
//                        permissions: MutableList<PermissionRequest>?, token: PermissionToken?
//                    ) {
//                        showRationalDialogForPermissions()
//                    }
//                }).onSameThread().check()
//        }

    }

    private fun updateLocation() {

    }

//
//    // A function which is used to verify that the location or GPS is enable or not of the user's device.
//    private fun isLocationEnabled(): Boolean {
//        val locationManager: LocationManager =
//            getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//            LocationManager.NETWORK_PROVIDER
//        )
//    }
//
//    //A function used to show the alert dialog when the permissions are denied and need to allow it from settings app info.
//    private fun showRationalDialogForPermissions() {
//        AlertDialog.Builder(this)
//            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
//            .setPositiveButton(
//                "GO TO SETTINGS"
//            ) { _, _ ->
//                try {
//                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                    val uri = Uri.fromParts("package", packageName, null)
//                    intent.data = uri
//                    startActivity(intent)
//                } catch (e: ActivityNotFoundException) {
//                    e.printStackTrace()
//                }
//            }.setNegativeButton("Cancel") { dialog, _ ->
//                dialog.dismiss()
//            }.show()
//    }

//    //to request the current location.
//    @Suppress("DEPRECATION")
//    @SuppressLint("MissingPermission")
//    private fun requestLocationData() {
//
//        val mLocationRequest = LocationRequest()
//        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        mFusedLocationClient.requestLocationUpdates(
//            mLocationRequest, mLocationCallback, Looper.myLooper()
//        )
//    }
//
//    //get the current location details.
//    private val mLocationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//
//            val mLastLocation: Location? = locationResult.lastLocation
//            if (mLastLocation != null) {
//                mLatitude = mLastLocation.latitude
//            }
//            Log.e("Current Latitude", "$mLatitude")
//            if (mLastLocation != null) {
//                mLongitude = mLastLocation.longitude
//            }
//            Log.e("Current Longitude", "$mLongitude")
//
//            getLocationWeatherDetails()
//        }
//    }

    //details of the current location based on the latitude longitude

    private fun getLocationWeatherDetails() {

        if (Constants.isNetworkAvailable(this@MainActivity)) {
            val retrofit: Retrofit = Retrofit.Builder()
                // API base URL.
                .baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build()
            val service: WeatherService = retrofit.create(WeatherService::class.java)
            val listCall: Call<WeatherResponse> = service.getWeather(
                mLatitude, mLongitude, Constants.METRIC_UNIT, Constants.APP_ID
            )

            //showCustomProgressDialog()

            listCall.enqueue(object : Callback<WeatherResponse> {
                @SuppressLint("SetTextI18n")
                override fun onResponse(
                    response: Response<WeatherResponse>, retrofit: Retrofit
                ) {
                    if (response.isSuccess) {

                        //hideProgressDialog()
                        val weatherList: WeatherResponse = response.body()
                        val weatherResponseJsonString = Gson().toJson(weatherList)
                        val editor = mSharedPreferences.edit()
                        editor.putString(Constants.WEATHER_RESPONSE_DATA, weatherResponseJsonString)
                        //Log.e("ds",weatherResponseJsonString)
                        editor.apply()
                        Log.d("response", weatherResponseJsonString)

                        setupUI()

                    } else {
                        when (val sc = response.code()) {
                            400 -> {
                                Log.e("Error 400", "Bad Request")
                            }
                            404 -> {
                                Log.e("Error 404", "Not Found")
                            }
                            else -> {
                                Log.e("Error", "$sc Generic Error")
                            }
                        }
                    }
                    hideProgressDialog()
                }

                override fun onFailure(t: Throwable) {
                    //hideProgressDialog() // Hides the progress dialog
                    Log.e("Errorrrrr", t.message.toString())
                    hideProgressDialog()
                }
            })
        } else {
            Toast.makeText(
                this@MainActivity, "No internet connection available.", Toast.LENGTH_SHORT
            ).show()
            hideProgressDialog()
        }
    }

    private fun showCustomProgressDialog() {
        mProgressDialog = Dialog(this)
        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)
        mProgressDialog!!.show()
    }

    private fun hideProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog!!.dismiss()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        val weatherResponseJsonString =
            mSharedPreferences.getString(Constants.WEATHER_RESPONSE_DATA, "")

        if (!weatherResponseJsonString.isNullOrEmpty()) {

            val weatherList =
                Gson().fromJson(weatherResponseJsonString, WeatherResponse::class.java)
            for (index in weatherList.weather.indices) {
                tv_main.text = weatherList.weather[index].main
                tv_main_description.text = weatherList.weather[index].description
                tv_temp.text = weatherList.main.pressure.toString() + " P"
                tv_humidity.text = weatherList.main.humidity.toString() + "%"
                tv_min.text = weatherList.main.temp.toString() + " °C"
                tv_max.text = "RF " + weatherList.main.feels_like.toString() + " °C"
                tv_speed.text = weatherList.wind.speed.toString()
                tv_name.text = weatherList.name
                tv_country.text = weatherList.sys.country
                tv_sunrise_time.text = unixTime(weatherList.sys.sunrise.toLong())
                tv_sunset_time.text = unixTime(weatherList.sys.sunset.toLong())
                Latitude.text = "Latitude : " + weatherList.coord.lat.toString()
                Longitude.text = "Longitude : " + weatherList.coord.lon.toString()
                time.text = "Time of data calculation " + unixTime(weatherList.dt.toLong())
                // Here we update the main icon
                when (weatherList.weather[index].icon) {
                    "01d" -> iv_main.setImageResource(R.drawable.sunny)
                    "02d" -> iv_main.setImageResource(R.drawable.cloud)
                    "03d" -> iv_main.setImageResource(R.drawable.cloud)
                    "04d" -> iv_main.setImageResource(R.drawable.cloud)
                    "04n" -> iv_main.setImageResource(R.drawable.cloud)
                    "10d" -> iv_main.setImageResource(R.drawable.rain)
                    "11d" -> iv_main.setImageResource(R.drawable.storm)
                    "13d" -> iv_main.setImageResource(R.drawable.snowflake)
                    "01n" -> iv_main.setImageResource(R.drawable.cloud)
                    "02n" -> iv_main.setImageResource(R.drawable.cloud)
                    "03n" -> iv_main.setImageResource(R.drawable.cloud)
                    "10n" -> iv_main.setImageResource(R.drawable.cloud)
                    "11n" -> iv_main.setImageResource(R.drawable.rain)
                    "13n" -> iv_main.setImageResource(R.drawable.snowflake)
                }
            }
        } else {
            showCustomProgressDialog()
            getLocationWeatherDetails()
        }


    }


    private fun unixTime(timex: Long): String? {
        val date = Date(timex * 1000L)
        @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("hh:mm a")
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }

}