package com.kpstv.yts.extensions.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.kpstv.common_moviesy.extensions.Coroutines
import com.kpstv.yts.AppInterface
import com.kpstv.yts.BuildConfig
import com.kpstv.yts.data.converters.AppDatabaseConverter
import com.kpstv.yts.data.models.AppDatabase
import com.kpstv.yts.services.UpdateService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateUtils @Inject constructor(
    @ApplicationContext private val context: Context,
    private val retrofitUtils: RetrofitUtils
) {
    companion object {
        const val UPDATE_URI = ""
    }
    /**
     * To make this suspend worker run on non suspendable method
     * we use a callback function.
     */
    fun check(onUpdateAvailable: (AppDatabase.Update) -> Unit, onError: (Exception) -> Unit) = Coroutines.io {
        try {
            val updatePair = fetchUpdateDetails()
            if (updatePair.second) {
                onUpdateAvailable.invoke(updatePair.first.update)
            }
        }catch (e: Exception) {
            onError(e)
        }
    }

    fun processUpdate(update: AppDatabase.Update) = with(context) {

        startService(Intent(this, UpdateService::class.java).apply {
            putExtra(UPDATE_URI, update.url)
        })
    }

    private suspend fun fetchUpdateDetails(): Pair<AppDatabase, Boolean> {
        val response = retrofitUtils.makeHttpCallAsync(AppInterface.APP_DATABASE_URL)
        if (response.isSuccessful) {
            val appDatabase = AppDatabaseConverter
                .toAppDatabaseFromString(response.body?.string())
            response.close()
            if (appDatabase == null) throw Exception("Failed to obtain details from the response")
            return Pair(appDatabase, appDatabase.update.versionCode > BuildConfig.VERSION_CODE)
        }else
            throw Exception("Failed to retrieve app database")
    }
}