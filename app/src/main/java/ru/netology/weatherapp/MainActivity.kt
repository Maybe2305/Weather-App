package ru.netology.weatherapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import ru.netology.weatherapp.data.WeatherModel
import ru.netology.weatherapp.screens.DialogSearch
import ru.netology.weatherapp.screens.MainCard
import ru.netology.weatherapp.screens.TabLayout
import ru.netology.weatherapp.ui.theme.WeatherAppTheme

const val KEY_API = "51b30cacea3448578cb163326240103"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                val daysList = remember {
                    mutableStateOf(listOf<WeatherModel>())
                }

                val dialogState = remember {
                    mutableStateOf(false)
                }

                val currentDay = remember {
                    mutableStateOf(WeatherModel(
                        "",
                        "",
                        "0.0",
                        "",
                        "",
                        "0.0",
                        "0.0",
                        ""
                    )
                    )
                }
                val city = remember {
                    mutableStateOf("Moscow")
                }

                if (dialogState.value) {
                    DialogSearch(
                        dialogState,
                        onSubmit = {
                            city.value = it
                            getData(it, this, daysList, currentDay)
                        })
                }

                getData(city.value, this, daysList, currentDay)
                Image(
                    painter = painterResource(id = R.drawable.weather_app_bg),
                    contentDescription = "im1",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.8f),
                    contentScale = ContentScale.FillHeight
                )
                Column {

                    MainCard(currentDay, onClickSync = {
                        getData("Moscow", this@MainActivity, daysList, currentDay)
                    }, onClickSearch = { dialogState.value = true })
                    TabLayout(daysList, currentDay)

                }

            }
        }
    }
}

private fun getData(city: String, context: Context, daysList: MutableState<List<WeatherModel>>,
                    currentDay: MutableState<WeatherModel>) {

    val url = "https://api.weatherapi.com/v1/forecast.json?" +
            "key=$KEY_API&" +
            "q=${city}&" +
            "days=3&" +
            "aqi=no&" +
            "alerts=no&" +
            "lang=ru"

    val queue = Volley.newRequestQueue(context)
    val sRequest = StringRequest(
        Request.Method.GET,
        url,
        {
            response ->
            val resp = String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
            val list = getWeatherByDays(resp)
            currentDay.value = list[0]
            daysList.value = list
        },
        {
            Log.d("MyLog", "VolleyError: $it")
        }
    )
    queue.add(sRequest)
}

private fun getWeatherByDays(response: String): List<WeatherModel> {

    if (response.isEmpty()) return listOf()
    val list = ArrayList<WeatherModel>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

    for (i in 0 until days.length()) {
        val item = days[i] as JSONObject
        list.add(
            WeatherModel(
                city,
                item.getString("date"),
                "",
                item.getJSONObject("day").getJSONObject("condition")
                    .getString("text"),
                item.getJSONObject("day").getJSONObject("condition")
                    .getString("icon"),
                item.getJSONObject("day").getString("mintemp_c"),
                item.getJSONObject("day").getString("maxtemp_c"),
                item.getJSONArray("hour").toString()
            )
        )
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemp = mainObject.getJSONObject("current").getString("temp_c")
    )
    return list
}
