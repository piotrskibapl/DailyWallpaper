package pl.piotrskiba.dailywallpaper

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import pl.piotrskiba.dailywallpaper.utils.AutoChangeUtils.scheduleWallpaperChanger

class SettingsFragment : PreferenceFragmentCompat(), OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
        addPreferencesFromResource(R.xml.preferences)
        val preferenceScreen = preferenceScreen
        val sharedPreferences = preferenceScreen.sharedPreferences
        val count = preferenceScreen.preferenceCount
        for (i in 0 until count) {
            val preference = preferenceScreen.getPreference(i)
            if (preference is ListPreference) {
                val value = sharedPreferences.getString(preference.getKey(), "")
                setPreferenceSummary(preference, value)
            }
        }
    }

    private fun setPreferenceSummary(preference: Preference, value: String?) {
        if (preference is ListPreference) {
            val preferenceIndex = preference.findIndexOfValue(value)
            if (preferenceIndex >= 0) {
                preference.summary = preference.entries[preferenceIndex]
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val preference = findPreference<Preference>(key)
        if (preference != null) {
            if (preference is ListPreference) {
                val value = sharedPreferences.getString(preference.getKey(), "")
                setPreferenceSummary(preference, value)
            }
            if (preference.key === getString(R.string.pref_interval_key)) {
                scheduleWallpaperChanger(context!!)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}