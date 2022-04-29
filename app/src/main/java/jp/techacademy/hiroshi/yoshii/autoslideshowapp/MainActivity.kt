package jp.techacademy.hiroshi.yoshii.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.provider.MediaStore
import android.content.ContentUris
import android.os.Handler
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val PERMISSIONS_REQUEST_CODE = 100

    private var index: Int = 0
    private var id: Long = -1
    private var idList: MutableList<Long> = mutableListOf()
    private var mTimer: Timer? = null
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }
        back_button.setOnClickListener(this)
        startstop_button.setOnClickListener(this)
        start_button.setOnClickListener(this)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )

        if (cursor!!.moveToFirst()) {
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            idList.add(cursor.getLong(fieldIndex))

            while (cursor.moveToNext()) {
                idList.add(cursor.getLong(fieldIndex))
            }
            cursor.close()
            imageView.setImageURI(imageUri)
        }
    }
        override fun onClick(v: View) {

            when (v.id) {
                R.id.start_button -> getStartImage()
                R.id.startstop_button -> getStartstopImage()
                R.id.back_button -> getBackImage()
            }
        }
    private fun getStartstopImage() {
        if (mTimer == null) {
            startstop_button.text = "停止"
            start_button.isClickable  = false
            back_button.isClickable  = false
            mTimer = Timer()
            mTimer!!.schedule(object : TimerTask() {
                override fun run() {
                    mHandler.post {
                        getStartImage()
                    }
                }
            }, 2000, 2000)
        } else {
            startstop_button.text = "再生"
            start_button.isClickable = true
            back_button.isClickable  = true
            if (mTimer != null) {
                mTimer!!.cancel()
                mTimer = null
            }
        }
    }

    private fun getStartImage() {

        if(index + 1 == idList.size){
            index = 0
        } else {
            index++
        }
        setImageId()
    }

    private fun getBackImage() {

        if(index  == 0){
            index = idList.size - 1
        } else {
            index--
        }
        setImageId()
    }
    private fun setImageId() {
        id = idList[index]
        imageView.setImageURI(ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id))
    }



}

