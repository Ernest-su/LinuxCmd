package ernest.linuxcmd

import android.app.SearchManager
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import ernest.linuxcmd.CmdListAdapter.OnListItemClickListener
import ernest.linuxcmd.databinding.ActivityMainBinding
import ernest.linuxcmd.markdown.CommonMarkMarkdownParser
import ernest.linuxcmd.markdown.FlexMarkMarkdownParser
import ernest.linuxcmd.markdown.MarkdownParser
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class MainActivity : AppCompatActivity(), OnListItemClickListener {
    private var cmdList: MutableList<String> = ArrayList()
    private var adapter = CmdListAdapter(cmdList)
    private var markdownParser = FlexMarkMarkdownParser()

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val assetManager = assets
        if (assetManager != null) {
            try {
                assetManager.list("command")?.let {
                    cmdList.addAll(it)
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
            val html = getHtmlString(cmd)
            binding.wvContent.loadDataWithBaseURL(
                null,
                getTemplate() + html,
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

    @Throws(IOException::class)
    private fun getHtmlString(cmd: String): String {
        val assetManager = assets
        val reader = InputStreamReader(assetManager.open(String.format("command/%s.md", cmd)))
        return markdownParser.parseMdToHtml(reader)
    }

    private fun getTemplate(): String {
        return assets.open("template.html").reader().readText()
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
        showCmdDetail(cmdList[position].replace(".md", ""))
    }

    override fun onBackPressed() {
        if (binding.rvCmdList.visibility != View.VISIBLE) {
            binding.rvCmdList.visibility = View.VISIBLE
            binding.wvContent.visibility = View.INVISIBLE
        } else {
            super.onBackPressed()
        }
    }
}