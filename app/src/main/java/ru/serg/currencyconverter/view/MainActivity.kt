package ru.serg.currencyconverter.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.serg.currencyconverter.R
import ru.serg.currencyconverter.databinding.ActivityMainBinding
import ru.serg.currencyconverter.helper.EndPoints
import ru.serg.currencyconverter.helper.Resource
import ru.serg.currencyconverter.helper.Utility
import ru.serg.currencyconverter.model.Rates
import ru.serg.currencyconverter.view.history.HistoryFragment
import ru.serg.currencyconverter.viewmodel.MainViewModel
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private var selectedItem1: String? = "USA"
    private var selectedItem2: String? = "EUR"

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        setTheme(R.style.AppTheme_NoActionBar)

        super.onCreate(savedInstanceState)

        Utility.makeStatusBarTransparent(this)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initSpinner()

        setUpClickListener()
    }

    private fun initSpinner() {

        val spinner1 = binding.spnFirstCountry

        spinner1.setItems(getAllCountries())

        spinner1.setOnClickListener {
            Utility.hideKeyboard(this)
        }

        spinner1.setOnItemSelectedListener { _, _, _, item ->
            val countryCode = getCountryCode(item.toString())
            val currencySymbol = getSymbol(countryCode)
            selectedItem1 = currencySymbol
            binding.txtFirstCurrencyName.text = selectedItem1
        }
        spinner1.requestFocus()

        val spinner2 = binding.spnSecondCountry

        spinner1.setOnClickListener {
            Utility.hideKeyboard(this)
        }

        spinner2.setItems(getAllCountries())

        spinner2.setOnItemSelectedListener { _, _, _, item ->
            val countryCode = getCountryCode(item.toString())
            val currencySymbol = getSymbol(countryCode)
            selectedItem2 = currencySymbol
            binding.txtSecondCurrencyName.text = selectedItem2
        }
    }

    private fun getSymbol(countryCode: String?): String? {
        val availableLocales = Locale.getAvailableLocales()
        for (i in availableLocales.indices) {
            if (availableLocales[i].country == countryCode
            ) return Currency.getInstance(availableLocales[i]).currencyCode
        }
        return ""
    }

    private fun getCountryCode(countryName: String) =
        Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }

    private fun getAllCountries(): ArrayList<String> {

        val locales = Locale.getAvailableLocales()
        val countries = arrayListOf<String>()
        for (locale in locales) {
            val country = locale.displayCountry
            if (country.trim { it <= ' ' }.isNotEmpty() && !countries.contains(country)) {
                countries.add(country)
            }
        }

        countries.sort()

        return countries
    }

    private fun setUpClickListener() {

        binding.btnConvert.setOnClickListener {

            val numberToConvert = binding.etFirstCurrency.text.toString()

            if (numberToConvert.isEmpty() || numberToConvert == "0") {
                Snackbar.make(
                    binding.mainLayout,
                    getString(R.string.no_value_input),
                    Snackbar.LENGTH_LONG
                )
                    .withColor(ContextCompat.getColor(this, R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(this, R.color.white))
                    .show()
            } else if (!Utility.isNetworkAvailable(this)) {
                Snackbar.make(
                    binding.mainLayout,
                    getString(R.string.no_connection),
                    Snackbar.LENGTH_LONG
                )
                    .withColor(ContextCompat.getColor(this, R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(this, R.color.white))
                    .show()
            } else {
                doConversion()
            }
        }

        binding.txtContact.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            val data: Uri = Uri.parse("mailto:sarevyakin@gmail.com?subject=CurrencyConverter")
            intent.data = data
            startActivity(intent)
        }

        binding.txtHistory.setOnClickListener {
            HistoryFragment(mainViewModel.history).show(supportFragmentManager, "TAG")
        }

    }

    fun deleteHistory() {
        mainViewModel.dropDatabase()
        Snackbar.make(
            binding.mainLayout,
            getString(R.string.hisrory_cleared),
            Snackbar.LENGTH_LONG
        )
            .withColor(ContextCompat.getColor(this, R.color.design_default_color_primary_dark))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .show()
    }

    private fun doConversion() {

        Utility.hideKeyboard(this)

        binding.prgLoading.visibility = View.VISIBLE

        binding.btnConvert.visibility = View.GONE

        val apiKey = EndPoints.API_KEY
        val from = selectedItem1.toString()
        val to = selectedItem2.toString()
        val amount = binding.etFirstCurrency.text.toString().toDouble()

        mainViewModel.getConvertedData(apiKey, from, to, amount)

        observeUi()

    }


    private fun observeUi() {
        mainViewModel.data.observe(this, { result ->

            when (result.status) {
                Resource.Status.SUCCESS -> {
                    if (result.data?.status == "success") {

                        val map: Map<String, Rates>

                        map = result.data.rates

                        map.keys.forEach {
                            val rateForAmount = map[it]?.rate_for_amount

                            mainViewModel.convertedRate.value = rateForAmount

                            val formattedString =
                                String.format("%,.2f", mainViewModel.convertedRate.value)

                            binding.etSecondCurrency.setText(formattedString)
                        }

                        binding.prgLoading.visibility = View.GONE
                        binding.btnConvert.visibility = View.VISIBLE
                    } else if (result.data?.status == "fail") {
                        val layout = binding.mainLayout
                        Snackbar.make(
                            layout,
                            getString(R.string.something_wrong),
                            Snackbar.LENGTH_LONG
                        )
                            .withColor(ContextCompat.getColor(this, R.color.dark_red))
                            .setTextColor(ContextCompat.getColor(this, R.color.white))
                            .show()

                        binding.prgLoading.visibility = View.GONE
                        binding.btnConvert.visibility = View.VISIBLE
                    }
                }
                Resource.Status.ERROR -> {
                    val layout = binding.mainLayout
                    Snackbar.make(
                        layout,
                        getString(R.string.something_wrong),
                        Snackbar.LENGTH_LONG
                    )
                        .withColor(ContextCompat.getColor(this, R.color.dark_red))
                        .setTextColor(ContextCompat.getColor(this, R.color.white))
                        .show()

                    binding.prgLoading.visibility = View.GONE
                    binding.btnConvert.visibility = View.VISIBLE
                }
                Resource.Status.LOADING -> {
                    binding.prgLoading.visibility = View.VISIBLE
                    binding.btnConvert.visibility = View.GONE
                }
            }
        })
    }

    private fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar {
        this.view.setBackgroundColor(colorInt)
        return this
    }

}