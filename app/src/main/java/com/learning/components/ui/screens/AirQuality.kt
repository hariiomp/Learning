package com.learning.components.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL


data class AirQualityData(
    val aqi: Double,
    val pm25: Double,
    val pm10: Double,
    val co: Double,
    val no2: Double,
    val so2: Double,
    val ozone: Double
)

@Composable
fun AirQuality(
) {

    var airQuality by remember {
        mutableStateOf<AirQualityData?>(null)
    }
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Url("https://lottie.host/f7fe0a16-49c0-4373-9b8d-cdf150fbce8c/JjMZOKQ4z4.json")
    )
    LaunchedEffect(Unit) {

        val response = getAirQuality()
        val json = JSONObject(response)
        val current = json.getJSONObject("current")

        airQuality = AirQualityData(
            aqi = current.getDouble("us_aqi"),
            pm25 = current.getDouble("pm2_5"),
            pm10 = current.getDouble("pm10"),
            co = current.getDouble("carbon_monoxide"),
            no2 = current.getDouble("nitrogen_dioxide"),
            so2 = current.getDouble("sulphur_dioxide"),
            ozone = current.getDouble("ozone")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .clip(RoundedCornerShape(12.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color(0xFFDEE5F3)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {

                LottieAnimation(
                    composition = composition,
                    iterations = LottieConstants.IterateForever,
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    contentScale = ContentScale.Crop,

                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AQI",
                        color = Color.Gray,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${airQuality?.aqi ?: "Loading..."}",
                        color = Color.Black,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

}

suspend fun getAirQuality(): String{

    return withContext(Dispatchers.IO) {
        val url = URL(
            "https://air-quality-api.open-meteo.com/v1/air-quality" +
                    "?latitude=28.599998" +
                    "&longitude=77.20001" +
                    "&current=us_aqi,pm2_5,pm10,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone"
        )

        val connection = url.openConnection() as HttpURLConnection

        connection.inputStream
            .bufferedReader()
            .use { it.readText() }
    }
}

@Preview(showBackground = true)
@Composable
fun AirQualityPreview() {
    AirQuality()
}