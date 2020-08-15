package com.kpstv.yts.extensions.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.kpstv.yts.AppInterface.Companion.handleRetrofitError
import com.kpstv.yts.R
import com.kpstv.yts.data.db.repository.FavouriteRepository
import com.kpstv.yts.data.models.Movie
import com.kpstv.yts.data.models.response.Model
import com.kpstv.yts.extensions.colorFrom
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import kotlin.reflect.KClass

class AppUtils {

    companion object {

        fun getBulletSymbol(): Spanned {
            return getHtmlText("&#8226;")
        }


        fun getHtmlText(text: String): Spanned {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(text)
            }
        }

        fun setTextViewColor(textView: TextView, text: String, color: Int) {
            (textView.text as Spannable).setSpan(
                ForegroundColorSpan(color), 0, text.length,
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }

        /**
         * Always pass this@Activity as context.
         * Else it won't resolve theme
         */
        fun getColorFromAttr(
            context: Context,
            @AttrRes attrColor: Int,
            typedValue: TypedValue = TypedValue(),
            resolveRefs: Boolean = true
        ): Int {
            // TODO: Make a check here
            context.theme.resolveAttribute(attrColor, typedValue, resolveRefs)
            return typedValue.data
        }

        fun getColoredString(mString: String, colorId: Int): Spannable? {
            val spannable: Spannable = SpannableString(getHtmlText(mString))
            spannable.setSpan(
                ForegroundColorSpan(colorId),
                0,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spannable
        }

        @SuppressLint("ResourceType")
        fun launchUrl(context: Context, url: String, dark: Boolean = true) = with(context) {
            val builder = CustomTabsIntent.Builder()
            var color: Int = R.color.colorPrimary_New
            if (!dark) color = R.color.colorPrimary
            builder.setToolbarColor(colorFrom(color))
            val customTabsIntent = builder.build()
            try {
                val packageInfo: PackageInfo =
                    packageManager.getPackageInfo("com.android.chrome", 0)
                customTabsIntent.intent.setPackage("com.android.chrome")
                customTabsIntent.launchUrl(this, Uri.parse(url))
            } catch (e: Exception) {
                Log.e("Chrome", "Chrome not installed: " + e.message)
                launchUrlIntent(url, this)
            }
        }

        fun hideKeyboard(context: Context) {
            val imm: InputMethodManager =
                context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }

        fun refactorYTSUrl(url: String): String {
            // https://yts.mx/torrent/download/F83269473864732884FBC77182408DC0EC9A5E08
            // https://yts.mx/assets/images/movies/spider_man_far_from_home_2019/background.jpg
            // https://yts.mx/movie/spider-man-far-from-home-2019
            //  return "${YTS_BASE_URL}${url.substring(14)}"
            return url
        }

        fun launchUrlIntent(url: String?, context: Context) {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            context.startActivity(i)
        }

        fun CafebarToast(context: Activity, message: String) {
            handleRetrofitError(
                context,
                Exception(message)
            )
        }

        fun getImdbUrl(id: String): String {
            return "https://www.imdb.com/title/${id}"
        }

        fun getMagnetUrl(hash: String, url_encoded_text: String): String {
            return """magnet:?xt=urn:btih:${hash}&dn=${url_encoded_text}
                |&tr=http://125.227.35.196:6969/announce
                |&tr=http://125.227.35.196:6969/announce
                |&tr=http://210.244.71.25:6969/announce
                |&tr=http://210.244.71.26:6969/announce
                |&tr=http://213.159.215.198:6970/announce
                |&tr=http://37.19.5.139:6969/announce
                |&tr=http://37.19.5.155:6881/announce
                |&tr=http://46.4.109.148:6969/announce
                |&tr=http://87.248.186.252:8080/announce
                |&tr=http://asmlocator.ru:34000/1hfZS1k4jh/announce
                |&tr=http://bt.evrl.to/announce
                |&tr=http://bt.pornolab.net/ann?uk=G7n8vXUdTt
                |&tr=http://bt.rutracker.org/ann
                |&tr=http://mgtracker.org:6969/announce
                |&tr=http://pubt.net:2710/announce
                |&tr=http://tracker.baravik.org:6970/announce
                |&tr=http://tracker.dler.org:6969/announce
                |&tr=http://tracker.filetracker.pl:8089/announce
                |&tr=http://tracker.grepler.com:6969/announce
                |&tr=http://tracker.mg64.net:6881/announce
                |&tr=http://tracker.tiny-vps.com:6969/announce
                |&tr=http://tracker.torrentyorg.pl/announce
                |&tr=http://tracker1.wasabii.com.tw:6969/announce
                |&tr=http://tracker2.wasabii.com.tw:6969/announce
                |&tr=udp://168.235.67.63:6969
                |&tr=udp://182.176.139.129:6969
                |&tr=udp://37.19.5.155:2710
                |&tr=udp://46.148.18.250:2710
                |&tr=udp://46.4.109.148:6969
                |&tr=udp://[2001:67c:28f8:92::1111:1]:2710
                |&tr=udp://bt.xxx-tracker.com:2710
                |&tr=udp://ipv6.leechers-paradise.org:6969
                |&tr=udp://opentor.org:2710
                |&tr=udp://public.popcorn-tracker.org:6969
                |&tr=udp://tracker.blackunicorn.xyz:6969
                |&tr=udp://tracker.ccc.de:80
                |&tr=udp://tracker.coppersurfer.tk:6969
                |&tr=udp://tracker.filetracker.pl:8089
                |&tr=udp://tracker.grepler.com:6969
                |&tr=udp://tracker.leechers-paradise.org:6969
                |&tr=udp://tracker.openbittorrent.com:80
                |&tr=udp://tracker.opentrackr.org:1337
                |&tr=udp://tracker.publicbt.com:80
                |&tr=udp://tracker.tiny-vps.com:6969""".trimMargin()
        }

        fun saveImageFromUrl(src: String, file: File) {
            try {
                val url = URL(src)
                val connection: HttpURLConnection = url
                    .openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()
                val input: InputStream = connection.inputStream
                file.outputStream().use { input.copyTo(it) }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        fun getVideoDuration(context: Context, videoFile: File): Long? {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, Uri.fromFile(videoFile))
            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            retriever.release()
            return time?.toLong()
        }

        // TODO: Remove this obsolete method
        fun getDirSize(dir: File): Long {
            if (dir.exists()) {
                var result: Long = 0
                val fileList: Array<File>? = dir.listFiles()
                for (i in fileList?.indices!!) { // Recursive call if it's a directory
                    result += if (fileList[i].isDirectory) {
                        getDirSize(fileList[i])
                    } else { // Sum the file size in bytes
                        fileList[i].length()
                    }
                }
                return result // return the file size
            }
            return 0
        }

        fun getSizePretty(size: Long?, addPrefix: Boolean = true): String? {
            val df = DecimalFormat("0.00")
            val sizeKb = 1024.0f
            val sizeMb = sizeKb * sizeKb
            val sizeGb = sizeMb * sizeKb
            val sizeTerra = sizeGb * sizeKb
            return if (size != null) {
                when {
                    size < sizeMb -> df.format(size / sizeKb)
                        .toString() + if (addPrefix) " KB" else ""
                    size < sizeGb -> df.format(
                        size / sizeMb
                    ).toString() + " MB"
                    size < sizeTerra -> df.format(size / sizeGb)
                        .toString() + if (addPrefix) " GB" else ""
                    else -> ""
                }
            } else "0" + if (addPrefix) " B" else ""
        }

        fun join(list: ArrayList<String>, separator: String): String {
            var builder = StringBuilder()
            for (i in 0 until list.size) {
                builder.append(list[i]).append(separator)
            }
            builder.append(list[list.size - 1])
            return builder.toString()
        }

        @JvmName("deleteRecurse")
        fun deleteRecursive(fileOrDirectory: File?) {
            if (fileOrDirectory == null) return
            val allFiles = fileOrDirectory.listFiles()
            if (fileOrDirectory.isDirectory && allFiles != null)
                for (child in allFiles) {
                    deleteRecursive(child)
                }
            fileOrDirectory.delete()
        }

        fun downloadFile(url: String, outputFile: File) {
            try {
                val u = URL(url);
                val conn = u.openConnection();
                val contentLength = conn.contentLength;

                val stream = DataInputStream(u.openStream());

                val buffer = ByteArray(contentLength)
                stream.readFully(buffer);
                stream.close();

                val fos = DataOutputStream(FileOutputStream(outputFile));
                fos.write(buffer);
                fos.flush();
                fos.close();
            } catch (e: FileNotFoundException) {
                Log.e(TAG, "downloadFile: ${e.message}}")
                return
            } catch (e: IOException) {
                Log.e(TAG, "downloadFile: ${e.message}}")
                return
            }
        }

        fun checkIfServiceIsRunning(context: Context, serviceClass: KClass<*>) = with(context) {
            val manager: ActivityManager =
                getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.qualifiedName == service.service.className) {
                    return@with true
                }
            }
            return@with false
        }

        fun installApp(context: Context, apk: File) {
            val install = Intent(Intent.ACTION_INSTALL_PACKAGE)
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                install.data =
                    FileProvider.getUriForFile(context, context.packageName + ".provider", apk)
            } else {
                apk.setReadable(true, false)
                install.data = Uri.fromFile(apk)
            }
            context.startActivity(install)
        }

        private val TAG = "Utils"
    }
}
