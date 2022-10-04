package com.example.pusanrestaurant

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pusanrestaurant.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {
    private val binding by lazy{ ActivityMainBinding.inflate(layoutInflater) }
    private val pageNo = "1"
    private val numOfRows = "150"
    private val resultType = "json"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val urlBuilder = StringBuilder(PusanApi.DOMAIN) /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "="+PusanApi.API_KEY) /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8")) /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8")) /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("resultType","UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")) /*JSON방식으로 호출 시 파라미터 resultType=json 입력*/

        val urlText = urlBuilder.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val url = URL(urlText)
            val urlConnection = url.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"

            if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                val streamReader = InputStreamReader(urlConnection.inputStream)
                val buffered = BufferedReader(streamReader)

                val content = StringBuilder()

                while (true) {
                    val line = buffered.readLine() ?: break
                    content.append(line)
                }

                buffered.close()
                urlConnection.disconnect()
                Log.d("오류",content.toString())
                var jsonObject = JSONObject(content.toString())
                jsonObject = jsonObject.getJSONObject("getFoodKr")
                val jsonArray = jsonObject.getJSONArray("item")

                val data = async{loadData(jsonArray)}

                withContext(Dispatchers.Main){
                    var adapter = CustomAdapter()
                    adapter.listData = data.await()
                    binding.recyclerView.adapter = adapter
                    binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
                }
            }
        }
        setContentView(binding.root)

    }

    private fun loadData(jsonArray: JSONArray):MutableList<Restaurant>{
        val data:MutableList<Restaurant> = mutableListOf()
        for(no in 1..numOfRows.toInt()){
            val jsonObject = jsonArray.getJSONObject(no-1)

            val title = jsonObject.getString("TITLE");
            val address = jsonObject.getString("ADDR1");
            val img = jsonObject.getString("MAIN_IMG_THUMB");
            val restaurant = Restaurant(img, title, address)
            data.add(restaurant)
        }
        return data
    }
}

