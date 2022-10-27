package ernest.linuxcmd.setting

import android.annotation.SuppressLint
import android.content.Context
import androidx.preference.PreferenceManager

@SuppressLint("StaticFieldLeak")
object AppSetting {
    private lateinit var context: Context
    fun init(context: Context) {
        AppSetting.context = context
    }

    fun getString(key: String, default: String = ""): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(key, default)
            ?: default
    }

    fun putString(key: String, value: String) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putString(key, value).apply()
    }


    fun getLong(key: String, default: Long = 0): Long {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getLong(key, default)
    }

    fun putLong(key: String, value: Long) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putLong(key, value).apply()
    }

    fun getInt(key: String, default: Int = 0): Int {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key, default)
    }

    fun putInt(key: String, value: Int) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putInt(key, value).apply()
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(key, default)
    }

    fun putBoolean(key: String, value: Boolean) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit().putBoolean(key, value).apply()
    }

    fun getStringSet(key: String, default: Set<String> = setOf()): Set<String> {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getStringSet(key, default)
            ?: default
    }
}