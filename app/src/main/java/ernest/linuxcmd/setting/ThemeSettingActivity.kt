package ernest.linuxcmd.setting

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.webkit.WebSettings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ernest.linuxcmd.App
import ernest.linuxcmd.R
import ernest.linuxcmd.databinding.ActivityThemeSettingBinding
import ernest.linuxcmd.markdown.FlexMarkMarkdownParser
import ernest.linuxcmd.setting.ThemeListAdapter.OnListItemClickListener
import java.io.IOException
import java.io.InputStreamReader


class ThemeSettingActivity : AppCompatActivity(), OnListItemClickListener {
    private var themeList: MutableList<String> = ArrayList()
    private var adapter = ThemeListAdapter(themeList)
    private var markdownParser = FlexMarkMarkdownParser()
    private var currentTheme = ""
    private lateinit var binding: ActivityThemeSettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val assetManager = assets
        if (assetManager != null) {
            try {
                assetManager.list("theme/css")?.let {
                    themeList.addAll(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        adapter.setOnListItemClickListener(this)
        binding.rvThemeList.layoutManager = LinearLayoutManager(this)
        binding.rvThemeList.adapter = adapter
        val settings = binding.wvContent.settings
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
        settings.setSupportZoom(false)
        settings.javaScriptEnabled = true
        markdownParser.init()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (Intent.ACTION_SEARCH == intent.action) {
            val cmd = intent.getStringExtra(SearchManager.QUERY)
            if (cmd.isNullOrEmpty()) {

            } else {
                showThemePreview(cmd)
            }
        }
    }

    private fun showThemePreview(theme: String) {
        currentTheme = theme
        try {
            val content = getFileContent("theme.md")
            binding.wvContent.loadDataWithBaseURL(
                null,
                formatWithTemplate(theme, content),
                "text/html",
                "UTF-8",
                null
            );
            binding.wvContent.visibility = View.VISIBLE
            binding.rvThemeList.visibility = View.GONE
        } catch (e: IOException) {
            e.printStackTrace()
            Snackbar.make(binding.rootView, R.string.load_cmd_detail_error, Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    private fun getFileContent(file: String): String {
        val assetManager = assets
        return InputStreamReader(assetManager.open(file)).readText()
    }

    private fun formatWithTemplate(theme: String, content: String): String {
        return assets.open("template.html").reader().readText()
            .replace("style.css", theme) + markdownParser.parseMdToHtml(
            content
        )
    }


    override fun onListItemClick(position: Int) {
        showThemePreview(themeList[position])
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_theme_setting, menu)

        menu.findItem(R.id.save).setOnMenuItemClickListener {
            saveTheme()
            false
        }
        return true
    }

    private fun saveTheme() {
        if (currentTheme.isNotEmpty()) {
            AppSetting.putString("theme", currentTheme)
            finish()
        } else {
            Toast.makeText(this, R.string.no_modify, Toast.LENGTH_SHORT).show()
        }
    }


    override fun onBackPressed() {
        if (binding.rvThemeList.visibility != View.VISIBLE) {
            binding.rvThemeList.visibility = View.VISIBLE
            binding.wvContent.visibility = View.INVISIBLE
        } else {
            super.onBackPressed()
        }
    }
}