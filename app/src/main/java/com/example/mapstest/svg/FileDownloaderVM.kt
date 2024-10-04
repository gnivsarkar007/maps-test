package com.example.mapstest.svg

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.sam43.svginteractiondemo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.IOException
import java.io.InputStream

class FileDownloaderVM(application: Application) : AndroidViewModel(application) {
    fun downloadFileFromServer(url: String): LiveData<ResponseBody> {
        // svg map of USA -> https://upload.wikimedia.org/wikipedia/commons/1/1a/Blank_US_Map_(states_only).svg
        // Using the custom svg file -> https://svgshare.com/i/Gzd.svg [Thanks to SVG Share website]
        return Repository.downloadFileFromServer(url)
    }

    suspend fun readXML(stream: InputStream): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val buffer = ByteArray(stream.available())
            stream.read(buffer)
            stream.close()
            String(buffer, charset("UTF-8")) // you just need to specify the charsetName
        } catch (e: IOException) {
            // Error handling
            null
        }
        null
    }
}

