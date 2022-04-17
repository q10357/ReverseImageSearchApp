package no.kristiania.android.reverseimagesearchapp
//
//import android.content.Context
//import androidx.core.app.NotificationCompat
//import androidx.work.CoroutineWorker
//import androidx.work.ForegroundInfo
//import androidx.work.WorkerParameters
//import kotlinx.coroutines.delay
//import no.kristiania.android.reverseimagesearchapp.data.remote.use_case.GetReverseImageSearchItemData
//import java.util.*
//import javax.inject.Inject
//import kotlin.random.Random.Default.nextInt
//
////class DownloadWorker(
////    private val getReverseImageSearchItemData: GetReverseImageSearchItemData,
////    private val context: Context,
////    private val workerParams: WorkerParameters
////): CoroutineWorker(context, workerParams) {
////
//////    override suspend fun doWork(): Result {
////        initForegroundService()
////        delay(1000)
////
////    }
////
////    private suspend fun initForegroundService() {
////        setForeground(
////            ForegroundInfo(
////                1,
////                NotificationCompat.Builder(context, "download_channel")
////                    .setSmallIcon(R.drawable.ic_upload)
////                    .setContentText("Downloading...")
////                    .setContentTitle("Results")
////                    .build()
////            )
////        )
////    }
////}