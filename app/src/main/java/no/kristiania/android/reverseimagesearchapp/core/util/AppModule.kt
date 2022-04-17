package no.kristiania.android.reverseimagesearchapp.core.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import no.kristiania.android.reverseimagesearchapp.data.local.ImageDao
import no.kristiania.android.reverseimagesearchapp.data.local.sqlLite.ImageDatabaseHelper
import no.kristiania.android.reverseimagesearchapp.data.remote.api.ReverseImageSearchApi
import no.kristiania.android.reverseimagesearchapp.data.remote.repo.ReverseImageSearchRepository
import no.kristiania.android.reverseimagesearchapp.data.remote.repo.ReverseImageSearchRepositoryImpl
import no.kristiania.android.reverseimagesearchapp.data.remote.use_case.GetReverseImageSearchItemData
import no.kristiania.android.reverseimagesearchapp.data.remote.use_case.GetUploadedImageUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

//    @Provides
//    @Singleton
//    fun providesImageDao(context: Context): ImageDao {
//        return ImageDatabase.initialize(context).imageDao
//
//    }

    @Provides
    @Singleton
    fun provideImageSearchApi(): ReverseImageSearchApi {
        val client = OkHttpClient.Builder()
            .build()

        val gson: Gson = GsonBuilder()
            .setLenient()
            .create()

        //Since we need to to retrieve the raw URL string when we upload image,
        //We need two converters, ScalarsConverter (for raw string),
        //And GSON for parsing json to objects
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URI)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
            .create(ReverseImageSearchApi::class.java)
    }

    @Provides
    @Singleton
    fun providesImageSearchRepository(api: ReverseImageSearchApi): ReverseImageSearchRepository {
        return ReverseImageSearchRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun providesDatabase(context: Context): ImageDatabaseHelper{
        return ImageDatabaseHelper(context)
    }
    @Provides
    @Singleton
    fun provideImageDao(db: ImageDatabaseHelper): ImageDao {
        return ImageDao(db)
    }

    @Provides
    @Singleton
    fun provideGetUploadedImageUrlUseCase(repository: ReverseImageSearchRepository): GetUploadedImageUrl {
        return GetUploadedImageUrl(repository)
    }

    @Provides
    @Singleton
    fun provideGetReverseImageSearchItemDataUseCase(repository: ReverseImageSearchRepository): GetReverseImageSearchItemData {
        return GetReverseImageSearchItemData(repository)
    }

//    @Provides
//    @Singleton
//    fun provideUploadedImageRepository(
//        api: ReverseImageFetchrApi
//    ): UploadedImageRepositoryImpl {
//        return UploadedImageRepositoryImpl(api = api)
//    }
}