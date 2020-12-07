package com.rwawrzyniak.flighthelper.di

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.rwawrzyniak.flighthelper.business.data.db.CacheDb
import com.rwawrzyniak.flighthelper.business.data.db.CacheDb.Companion.DB_NAME
import com.rwawrzyniak.flighthelper.business.data.network.NetworkConstants
import com.rwawrzyniak.flighthelper.business.data.network.NetworkConstants.BASE_STATIC_RESOURCES_URL
import com.rwawrzyniak.flighthelper.business.data.network.NetworkConstants.BASE_URL
import com.rwawrzyniak.flighthelper.business.data.network.RyanairApi
import com.rwawrzyniak.flighthelper.business.data.network.RyanairStaticResourcesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object ApplicationModule {

	@Singleton
	@Provides
	fun provideInputMethodManager(@ApplicationContext context: Context): InputMethodManager {
		return context.getSystemService(
			Activity.INPUT_METHOD_SERVICE
		) as InputMethodManager
	}

	@Singleton
	@Provides
	fun provideDatabase(
		@ApplicationContext app: Context
	) = Room.databaseBuilder(
		app,
		CacheDb::class.java,
		DB_NAME
	).build()

	@Singleton
	@Provides
	fun provideStationsDao(db: CacheDb) = db.getStationsDao()

	// TODO instead of creating two separate builder with @named pass url as a param
	@Singleton
	@Provides
	@Named("default")
	fun provideRetrofitBuilder(gsonBuilder: Gson): Retrofit.Builder = Retrofit.Builder()
		.baseUrl(BASE_URL)
		.addConverterFactory(GsonConverterFactory.create(gsonBuilder))

	@Singleton
	@Provides
	@Named("staticBuilder")
	fun provideRetrofitBuilderForStatic(gsonBuilder: Gson): Retrofit.Builder = Retrofit.Builder()
		.baseUrl(BASE_STATIC_RESOURCES_URL)
		.addConverterFactory(GsonConverterFactory.create(gsonBuilder))


	@Singleton
	@Provides
	fun provideGsonBuilder(): Gson = GsonBuilder()
		.create()

	@Singleton
	@Provides
	fun provideRyanairStaticResourceApi(@Named("staticBuilder") retrofitBuilder: Retrofit.Builder, httpClient: OkHttpClient): RyanairStaticResourcesApi =
		retrofitBuilder
			.client(httpClient)
			.build()
			.create(RyanairStaticResourcesApi::class.java)

	@Singleton
	@Provides
	fun provideRyanairApi(retrofitBuilder: Retrofit.Builder, httpClient: OkHttpClient): RyanairApi =
		retrofitBuilder
			.client(httpClient)
			.build()
			.create(RyanairApi::class.java)

	@Singleton
	@Provides
	fun provideHttpClient(
		loggingInterceptor: HttpLoggingInterceptor
	): OkHttpClient = OkHttpClient().newBuilder()
		.addInterceptor(loggingInterceptor)
		.build()

	@Singleton
	@Provides
	fun provideHttpLoginInterceptor() =
		HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
}
