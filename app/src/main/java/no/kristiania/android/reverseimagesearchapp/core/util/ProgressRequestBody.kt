package no.kristiania.android.reverseimagesearchapp.core.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

private const val TAG = "MainActivityTAG"
class ProgressRequestBody(
    private val f: File,
    private val contentType: String,
    private val listener: UploadCallback
) : RequestBody() {

    interface UploadCallback {
        fun onProgressUpdate(percentage: Int)
        fun onError()
        fun onFinish()
    }

    override fun contentType() = MediaType.parse("$contentType/*")

    override fun contentLength() = f.length()


    override fun writeTo(sink: BufferedSink) {
        Log.i(TAG, "in writeto: ${f.length()}")
        val length = f.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(f)
        var uploaded = 0L

        //Decided to use .use instead of try/catch, as documentation says that
        //stream will be closed correctly whether exception is thrown or not
        //So we made the decision to use this
        fileInputStream.use { inputStream ->
            var read: Int
            val handler = Handler(Looper.getMainLooper())

            //While inputStream is reading from file, we use lambda expression
            //Also, to let our reader be updated, this will make it possible
            //For us to know, where in the process we are,
            //And be able to make a progressbar
            //We set it to stop at -1, that means content is read finish
            while(inputStream.read(buffer).also { read = it } != -1){
                //The handler.post, posts to our UI thread
                handler.post(ProgressUpdater(uploaded, length))
                uploaded += read
                sink.write(buffer, 0, read)
            }
        }
    }

    inner class ProgressUpdater(
        private val uploaded: Long,
        private val total: Long
    ) : Runnable {
        override fun run() {
            listener.onProgressUpdate((100 * uploaded / total).toInt())
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 1048
    }

}