package hu.ait.weatherinfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hu.ait.weatherinfo.adapter.CityAdapter
import hu.ait.weatherinfo.data.CityWeather
import hu.ait.weatherinfo.databinding.ActivityDetailsBinding
import hu.ait.weatherinfo.network.WeatherAPI
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailsActivity : AppCompatActivity() {

    lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(CityAdapter.KEY_DATA)) {
            binding.tvCityName.text = intent.getStringExtra(CityAdapter.KEY_DATA)
        }

    }

    override fun onResume() {
        super.onResume()

        var retrofit = Retrofit.Builder().baseUrl("https://api.openweathermap.org/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        var weatherAPI = retrofit.create(WeatherAPI::class.java)
        val city= intent.getStringExtra(CityAdapter.KEY_DATA)
        val call = weatherAPI.getWeather(
            city!!, "metric",
            "2088bdc192793c41c32439e751b185a2"
        )
        call.enqueue(object : Callback<CityWeather> {
            override fun onResponse(call: Call<CityWeather>, response: Response<CityWeather>) {
                binding.tvTemp.text = "${response.body()!!.main!!.temp}" + " degree."
            }

            override fun onFailure(call: Call<CityWeather>, t: Throwable) {
                binding.tvTemp.text = "Error: ${t.message}"
            }
        })


    }
}