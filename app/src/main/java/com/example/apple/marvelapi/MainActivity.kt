package com.example.apple.marvelapi

import android.content.Context
import android.databinding.DataBindingUtil

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast

import com.example.apple.marvelapi.databinding.ActivityMainBinding
import com.example.apple.marvelapi.model.ComicsItem
import com.example.apple.marvelapi.model.Data
import com.squareup.picasso.Picasso

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Arrays
import java.util.Date

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        getData()
    }

    fun getData() {
        val simpleDateFormat = SimpleDateFormat("ddMMyyyyhhmmss")
        val format = simpleDateFormat.format(Date())

        val privKey = "ABC" //YOUR PRIVATE KEY
        val pubKey = "123" //YOUR PUBLIC KEY

        val toHash = format + privKey + pubKey

        val theHash = hashMDFive(toHash)

        val retrofit = Retrofit.Builder()
                .baseUrl("https://www.marvel.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create(ComicsAPI::class.java)

        val call = service.getJSON("https://gateway.marvel.com:443/v1/public/comics/1098?ts=$format&apikey=$pubKey&hash=$theHash")
        //val call = service.getJSON("http://google.com"); //USE THIS CALL TO SIMULATE NETWORK FAILURE/CHECK DATA PERSISTENCE


        call.enqueue(object : Callback<ComicsItem> {
            override fun onResponse(call: Call<ComicsItem>, response: retrofit2.Response<ComicsItem>) {
                
                if (response.isSuccessful) {

                    val data = response.body()!!.data
                    val name = data.results[0].title
                    val description = data.results[0].description
                    val image = data.results[0].thumbnail.path
                    val extension = data.results[0].thumbnail.extension
                    val path = "$image/standard_fantastic.$extension"

                    val model = ComicsViewModel()
                    binding!!.cvm = model

                    val imageView = findViewById<View>(R.id.imager) as ImageView

                    Picasso.get().load(path).resize(500, 500).centerCrop().into(imageView)
                    model.name.set(name)
                    model.description.set(description)

                    val mStringList = ArrayList<String>()
                    mStringList.add("$name@@@")
                    mStringList.add("$description@@@")
                    mStringList.add(path)

                    saveVals(mStringList)

                    //System.out.println("name of character " + name + " description " + description + " imageURL " + image + " path " + path);

                }
            }

            override fun onFailure(call: Call<ComicsItem>, t: Throwable) {
                var mStringList = ArrayList<String>()
                mStringList = retrieveVals()

                val name = mStringList[0]
                val description = mStringList[1]

                val imageView = findViewById<View>(R.id.imager) as ImageView
                imageView.setImageResource(R.drawable.ic_launcher_round)

                val model = ComicsViewModel()
                binding!!.cvm = model

                model.name.set(name)
                model.description.set(description)

                Toast.makeText(applicationContext, "Error with API", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun saveVals(mStringList: ArrayList<String>) {
        val file = "myfile"

        try {
            val fOut = openFileOutput(file, Context.MODE_PRIVATE)
            for (s in mStringList) {
                fOut.write(s.toByteArray())
            }
            fOut.close()
            //Toast.makeText(getBaseContext(),"file saved",Toast.LENGTH_SHORT).show();
        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
        }

    }

    fun retrieveVals(): ArrayList<String> {
        val file = "myfile"
        val buffer = StringBuffer("")
        try {
            val fileInputStream = openFileInput(file)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)

            var stringToRead: String? = bufferedReader.readLine()
            while (stringToRead != null) {
                //System.out.println("stringToRead is " + stringToRead);
                buffer.append(stringToRead)
                stringToRead = bufferedReader.readLine()
            }

            inputStreamReader.close()
        } catch (ioe: IOException) {
            ioe.printStackTrace()
        }

        val retdStrings = ArrayList(Arrays.asList(*buffer.toString().split("@@@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))

        return retdStrings
    }

    companion object {

        fun hashMDFive(s: String): String {
            var m: MessageDigest? = null

            try {
                m = MessageDigest.getInstance("MD5")
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }

            m!!.update(s.toByteArray(), 0, s.length)
            return BigInteger(1, m.digest()).toString(16)
        }
    }
}
