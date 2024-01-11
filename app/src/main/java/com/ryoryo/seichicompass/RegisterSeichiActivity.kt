package com.ryoryo.seichicompass

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.ryoryo.seichicompass.databinding.ActivityRegisterSeichiBinding
import com.ryoryo.seichicompass.lib.Constants
import com.ryoryo.seichicompass.model.NewSeichiInfo
import com.ryoryo.seichicompass.network.SeichiInfoApi
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * References for getting information from intent
 * - https://medium.com/url-memo/share-oher-app-b9effacf42b2
 * - https://developer.android.com/training/sharing/receive?hl=ja#kotlin
 */
class RegisterSeichiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterSeichiBinding

    private val intentPattern = Regex(""".*(https://maps.+)""")
    private val coordinateURLPattern = Regex("""https://maps\.app\.goo\.gl/.+""")

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterSeichiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val url = getURLFromIntent()

        binding.coordinateURL.text = url
        url?.let {
            if (!coordinateURLPattern.matches(it)) {
                Log.i(Constants.APP_TAG, "Invalid url: $it")
                Toast.makeText(this, R.string.alert_invalid_coordinate_url, Toast.LENGTH_LONG).show()
                binding.registerSeichiButton.isEnabled = false
            }
        }

        binding.registerSeichiButton.setOnClickListener {
            val title = binding.titleInput.text.toString()
            val description = binding.descriptionInput.text.toString()
            val infoSource = binding.infoSourceInput.text.toString()
            val url = binding.coordinateURL.text.toString()
            val newInfo = NewSeichiInfo(title, description, infoSource, url)

            val canPost = title.isNotEmpty() && description.isNotEmpty()
                    && infoSource.isNotEmpty() && url.isNotEmpty()
            val toast = Toast.makeText(this, "Error", Toast.LENGTH_LONG)

            if (canPost) {
                lifecycleScope.launch {
                    binding.registerLoading.loadingOverlay.visibility = View.VISIBLE
                    binding.registerSeichiButton.isEnabled = false

                    try {
                        SeichiInfoApi.retrofitService.postSeichi(newInfo)

                        val intent = Intent(this@RegisterSeichiActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } catch (e: Exception) {
                        toast.show()
                        Log.i(Constants.APP_TAG, e.toString())
                        binding.registerLoading.loadingOverlay.visibility = View.GONE
                        binding.registerSeichiButton.isEnabled = true
                    }
                }
            }
        }
    }

    private fun getURLFromIntent(): String? {
        return when (intent.action) {
            Intent.ACTION_SEND -> {
                val s = intent.extras?.getString(Intent.EXTRA_TEXT)
                s?.let {
                    val match = intentPattern.find(it)
                    match?.groupValues?.get(1)
                }
            }
            else -> null
        }
    }
}