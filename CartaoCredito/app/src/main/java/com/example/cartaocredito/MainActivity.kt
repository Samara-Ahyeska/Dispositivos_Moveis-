package com.example.cartaocredito

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.cartaocredito.databinding.ActivityMainBinding
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isShowingBack = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTextWatchers()
        setupFocusListeners()
    }

    private fun setupTextWatchers() {

        binding.etNumber.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val cleanString = s.toString().replace(" ", "")
                val formatted = StringBuilder()
                for (i in cleanString.indices) {
                    if (i > 0 && i % 4 == 0) formatted.append(" ")
                    formatted.append(cleanString[i])
                }

                binding.etNumber.setText(formatted.toString())
                binding.etNumber.setSelection(binding.etNumber.text!!.length)

                binding.tvCardNumberPreview.text = if (formatted.isEmpty()) "#### #### #### ####" else formatted.toString()

                identifyCardBrand(cleanString)

                isFormatting = false
            }
        })


        binding.etHolder.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString().uppercase()
                binding.tvCardHolderPreview.text = if (text.isEmpty()) "NOME DO TITULAR" else text
            }
        })

        binding.etExpires.addTextChangedListener(object : TextWatcher {
            private var isFormatting = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (isFormatting) return
                isFormatting = true

                val cleanString = s.toString().replace("/", "")
                val formatted = StringBuilder()
                for (i in cleanString.indices) {
                    if (i == 2) formatted.append("/")
                    formatted.append(cleanString[i])
                }

                binding.etExpires.setText(formatted.toString())
                binding.etExpires.setSelection(binding.etExpires.text!!.length)

                val displayVal = if (formatted.isEmpty()) "MM/AA" else formatted.toString()
                binding.tvCardExpiresPreview.text = displayVal

                validateExpirationDate(formatted.toString())

                isFormatting = false
            }
        })


        binding.etCvv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()
                binding.tvCardCvvPreview.text = if (text.isEmpty()) "123" else text
            }
        })
    }

    private fun validateExpirationDate(dateStr: String) {
        if (dateStr.length == 5) {
            val parts = dateStr.split("/")
            if (parts.size == 2) {
                val month = parts[0].toIntOrNull() ?: 0
                val shortYear = parts[1].toIntOrNull() ?: 0
                val fullYear = 2000 + shortYear

                val calendar = Calendar.getInstance()
                val currentYear = calendar.get(Calendar.YEAR)
                val currentMonth = calendar.get(Calendar.MONTH) + 1

                if (fullYear < currentYear || (fullYear == currentYear && month < currentMonth) || month !in 1..12) {
                    binding.tilExpires.error = "Cartão inválido"
                } else {
                    binding.tilExpires.error = null
                }
            } else {
                binding.tilExpires.error = "Cartão inválido"
            }
        } else {
            binding.tilExpires.error = null
        }
    }

    private fun setupFocusListeners() {
        binding.etCvv.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) flipCard(true)
        }

        val frontFocusListener: (Boolean) -> Unit = { hasFocus ->
            if (hasFocus) flipCard(false)
        }

        binding.etNumber.setOnFocusChangeListener { _, hasFocus -> frontFocusListener(hasFocus) }
        binding.etHolder.setOnFocusChangeListener { _, hasFocus -> frontFocusListener(hasFocus) }
        binding.etExpires.setOnFocusChangeListener { _, hasFocus -> frontFocusListener(hasFocus) }
    }

    private fun flipCard(showBack: Boolean) {
        if (isShowingBack == showBack) return
        isShowingBack = showBack

        val scale = applicationContext.resources.displayMetrics.density
        binding.cardFront.cameraDistance = 8000 * scale
        binding.cardBack.cameraDistance = 8000 * scale

        if (showBack) {
            binding.cardFront.animate().rotationY(-90f).setDuration(150).withEndAction {
                binding.cardFront.visibility = View.GONE
                binding.cardBack.visibility = View.VISIBLE
                binding.cardBack.rotationY = 90f
                binding.cardBack.animate().rotationY(0f).setDuration(150).start()
            }.start()
        } else {
            binding.cardBack.animate().rotationY(90f).setDuration(150).withEndAction {
                binding.cardBack.visibility = View.GONE
                binding.cardFront.visibility = View.VISIBLE
                binding.cardFront.rotationY = -90f
                binding.cardFront.animate().rotationY(0f).setDuration(150).start()
            }.start()
        }
    }

    private fun identifyCardBrand(number: String) {
        when {
            isAmex(number) -> applyCardStyle(Color.parseColor("#007BC1"), "AMEX")
            isDiners(number) -> applyCardStyle(Color.parseColor("#004B87"), "DINERS")
            isJcb(number) -> applyCardStyle(Color.parseColor("#0079C1"), "JCB")
            number.startsWith("4") -> applyCardStyle(Color.parseColor("#1A1F71"), "VISA")
            isMastercard(number) -> applyCardStyle(Color.parseColor("#EB001B"), "MASTERCARD")
            isDiscover(number) -> applyCardStyle(Color.parseColor("#FF6600"), "DISCOVER")
            isElo(number) -> applyCardStyle(Color.parseColor("#00A499"), "ELO")
            else -> applyCardStyle(Color.parseColor("#1A1A1A"), "")
        }
    }

    private fun isAmex(number: String): Boolean {
        if (number.length < 3) return false
        val prefix3 = number.substring(0, 3).toIntOrNull() ?: 0
        return prefix3 in 340..349 || prefix3 in 370..379
    }

    private fun isDiners(number: String): Boolean {
        if (number.length < 2) return false
        val prefix2 = number.substring(0, 2).toIntOrNull() ?: 0
        val prefix3 = if (number.length >= 3) number.substring(0, 3).toIntOrNull() ?: 0 else 0
        val isRange300to305 = prefix3 in 300..305
        return isRange300to305 || prefix3 == 309 || prefix2 == 36 || prefix2 == 38
    }

    private fun isJcb(number: String): Boolean {
        if (number.length < 3) return false
        val prefix3 = number.substring(0, 3).toIntOrNull() ?: 0
        return prefix3 in 352..358
    }

    private fun isMastercard(number: String): Boolean {
        if (number.length < 2) return false
        val prefix3 = if (number.length >= 3) number.substring(0, 3).toIntOrNull() ?: 0 else 0
        val prefix2 = number.substring(0, 2).toIntOrNull() ?: 0
        return prefix2 in 51..55 || prefix3 in 222..272
    }

    private fun isDiscover(number: String): Boolean {
        if (number.length < 3) return false
        val prefix3 = number.substring(0, 3).toIntOrNull() ?: 0
        val prefix2 = number.substring(0, 2).toIntOrNull() ?: 0
        return prefix3 == 601 || prefix3 in 644..649 || prefix2 == 65
    }

    private fun isElo(number: String): Boolean {
        if (number.length < 3) return false
        val prefix3 = number.substring(0, 3).toIntOrNull() ?: 0
        return prefix3 == 431 || prefix3 == 506 || prefix3 == 636
    }

    private fun applyCardStyle(bgColor: Int, brandName: String) {
        binding.cardFront.setCardBackgroundColor(bgColor)
        binding.cardBack.setCardBackgroundColor(bgColor)


        binding.tvBrandName.text = brandName


        if (brandName.isEmpty()) {
            binding.ivCardBrandBack.visibility = View.GONE
        } else {
            binding.ivCardBrandBack.visibility = View.VISIBLE
            val drawableRes = when (brandName) {
                "VISA" -> R.drawable.visa
                "MASTERCARD" -> R.drawable.mastercard
                "ELO" -> R.drawable.elo
                "AMEX" -> R.drawable.amex
                else -> return
            }
            binding.ivCardBrandBack.setImageResource(drawableRes)
        }
    }
}