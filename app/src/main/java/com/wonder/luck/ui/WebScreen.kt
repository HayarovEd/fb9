package com.wonder.luck.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.addCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

private var mUploadMessage: ValueCallback<Uri?>? = null
private var mCapturedImageURI: Uri? = null
private var mFilePathCallback: ValueCallback<Array<Uri>>? = null
private var mCameraPhotoPath: String? = null

@Composable
fun WebScreen(
    modifier: Modifier = Modifier,
    url: String,
) {
    val context = LocalContext.current
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->

        var results: Array<Uri>? = null

        if (result.data == null) {
            // If there is not data, then we may have taken a photo
            if (mCameraPhotoPath != null) {
                results = arrayOf(Uri.parse(mCameraPhotoPath))
            }
        } else {
            val dataString = result.data!!.dataString
            if (dataString != null) {
                results = arrayOf(Uri.parse(dataString))
            }
        }
        mFilePathCallback!!.onReceiveValue(results)
        mFilePathCallback = null
    }
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(4.dp),
    ) {
        AndroidView(factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = WebViewClient()
                webChromeClient = ChromeClient(activityResultLauncher)
                val webSettings = settings
                webSettings.apply {
                    javaScriptEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    setSupportZoom(false)
                    allowFileAccess = true
                    allowContentAccess = true
                    domStorageEnabled = true
                    javaScriptCanOpenWindowsAutomatically = true
                }
                val cookieManager = CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                onBackPressedDispatcher?.addCallback {
                    if (this@apply.canGoBack()) {
                        this@apply.goBack()
                    }
                }
                loadUrl(url)
            }
        }, update = {
            it.loadUrl(url)
        })

    }

}

class ChromeClient(private val activityResultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>) :
    WebChromeClient() {

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    fun createImageFile(): File {
        // Create an image file name
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
    }
    // For Android 5.0
    @SuppressLint("QueryPermissionsNeeded")
    override fun onShowFileChooser(
        view: WebView,
        filePath: ValueCallback<Array<Uri>>,
        fileChooserParams: FileChooserParams
    ): Boolean {
        // Double check that we don't have any existing callbacks
        if (mFilePathCallback != null) {
            mFilePathCallback!!.onReceiveValue(null)
        }
        mFilePathCallback = filePath
        var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        var photoFile: File? = null
        try {
            photoFile = createImageFile()
            takePictureIntent?.putExtra("PhotoPath", mCameraPhotoPath)
        } catch (ex: IOException) {
            // Error occurred while creating the File
            Log.e("ErrorCreatingFile", "Unable to create Image File", ex)
        }

        // Continue only if the File was successfully created
        if (photoFile != null) {
            mCameraPhotoPath = "file:" + photoFile.absolutePath
            takePictureIntent?.putExtra(
                MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photoFile)
            )
        } else {
            takePictureIntent = null
        }
        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
        contentSelectionIntent.type = "image/*"
        val intentArray: Array<Intent?> = takePictureIntent?.let { arrayOf(it) } ?: arrayOfNulls(0)
        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
        activityResultLauncher.launch(chooserIntent)
        return true
    }


}