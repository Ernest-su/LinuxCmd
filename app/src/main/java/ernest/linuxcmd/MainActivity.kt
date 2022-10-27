package ernest.linuxcmd

import android.app.SearchManager
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ernest.linuxcmd.CmdListAdapter.OnListItemClickListener
import ernest.linuxcmd.databinding.ActivityMainBinding
import ernest.linuxcmd.markdown.FlexMarkMarkdownParser
import ernest.linuxcmd.markdown.MarkDownFileUtil
import ernest.linuxcmd.setting.AppSetting
import ernest.linuxcmd.setting.ThemeSettingActivity
import java.io.IOException
import java.io.InputStreamReader


class MainActivity : AppCompatActivity(), OnListItemClickListener {
    private var cmdList: MutableList<Pair<String, String>> = ArrayList()
    private var adapter = CmdListAdapter(cmdList)
    private var markdownParser = FlexMarkMarkdownParser()
    private var currentTheme = ""
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val assetManager = assets
        if (assetManager != null) {
            try {
                assetManager.list("command")?.let { list ->
                    cmdList.addAll(list.map {
                        it to MarkDownFileUtil.getTitle(this, it)
                    })
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        adapter.setOnListItemClickListener(this)
        binding.rvCmdList.layoutManager = LinearLayoutManager(this)
        binding.rvCmdList.adapter = adapter
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
                showCmdDetail(cmd)
            }
        }
    }

    private fun showCmdDetail(cmd: String) {
        try {
            val content = getFileContent(cmd)
            binding.wvContent.loadDataWithBaseURL(
                null,
                formatWithTemplate(content),
                "text/html",
                "UTF-8",
                null
            );
            binding.wvContent.visibility = View.VISIBLE
            binding.rvCmdList.visibility = View.GONE
        } catch (e: IOException) {
            e.printStackTrace()
            Snackbar.make(binding.rootView, R.string.load_cmd_detail_error, Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    private fun getFileContent(cmd: String): String {
        val assetManager = assets
        return InputStreamReader(assetManager.open(String.format("command/%s.md", cmd))).readText()
    }

    private fun formatWithTemplate(content: String): String {
        return assets.open("template.html").reader().readText()
            .replace(
                "style.css",
                currentTheme.ifEmpty { "style.css" }
            ) + markdownParser.parseMdToHtml(
            content
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                val query = getSuggestion(searchView, position)
                searchView.setQuery(query, true)
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val query = getSuggestion(searchView, position)
                searchView.setQuery(query, true)
                return false
            }
        })
        menu.findItem(R.id.showList).setOnMenuItemClickListener {
            binding.wvContent.visibility = View.GONE
            binding.rvCmdList.visibility = View.VISIBLE
            false
        }
        menu.findItem(R.id.setting).setOnMenuItemClickListener {
            startActivity(Intent(this, ThemeSettingActivity::class.java))
            false
        }
        return true
    }

    /**
     * 根据searchView弹出的suggestion列表position对应的文本
     *
     * @param searchView 要查找的 searchView
     * @param position   suggestion列表的position
     * @return 查找到的文本
     */
    private fun getSuggestion(searchView: SearchView, position: Int): String {
        val cursor = searchView.suggestionsAdapter.getItem(
            position
        ) as Cursor
        val index = cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1)
        return if (index >= 0) {
            cursor.getString(index)
        } else {
            ""
        }
    }

    override fun onListItemClick(position: Int) {
        showCmdDetail(cmdList[position].first.replace(".md", ""))
    }

    override fun onBackPressed() {
        if (binding.rvCmdList.visibility != View.VISIBLE) {
            binding.rvCmdList.visibility = View.VISIBLE
            binding.wvContent.visibility = View.INVISIBLE
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        currentTheme = AppSetting.getString("theme")
    }
}