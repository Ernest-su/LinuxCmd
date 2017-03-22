package ernest.linuxcmd;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements CmdListAdapter.OnListItemClickListener {
    @BindView(R.id.rootView)
    View rootView;
    @BindView(R.id.wvContent)
    WebView wvContent;

    CmdListAdapter adapter;
    List<String> cmdList = new ArrayList<>();
    @BindView(R.id.rvCmdList)
    RecyclerView rvCmdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AssetManager assetManager = getAssets();
        if (assetManager != null) {
            try {
                cmdList.addAll(Arrays.asList(assetManager.list("cmd")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        adapter = new CmdListAdapter(cmdList);
        adapter.setOnListItemClickListener(this);
        rvCmdList.setLayoutManager(new LinearLayoutManager(this));
        rvCmdList.setAdapter(adapter);

        WebSettings settings = wvContent.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String cmd = intent.getStringExtra(SearchManager.QUERY);
            if (!TextUtils.isEmpty(cmd)) {
                showCmdDetail(cmd);
            }
        }
    }

    private void showCmdDetail(String cmd) {

        try {
            AssetManager assetManager = getAssets();
            InputStreamReader reader = new InputStreamReader(assetManager.open(String.format("cmd/%s.md", cmd)));
            Parser parser = Parser.builder().build();
            Node document = parser.parseReader(reader);
            HtmlRenderer renderer = HtmlRenderer.builder().build();
            wvContent.loadData(renderer.render(document), "text/html; charset=UTF-8", null);
            wvContent.setVisibility(View.VISIBLE);
            rvCmdList.setVisibility(View.GONE);
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(rootView, R.string.load_cmd_detail_error, Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) (menu.findItem(R.id.search)).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                String query = getSuggestion(searchView, position);
                searchView.setQuery(query, true);
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                String query = getSuggestion(searchView, position);
                searchView.setQuery(query, true);
                return false;
            }
        });

        menu.findItem(R.id.showList).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                wvContent.setVisibility(View.GONE);
                rvCmdList.setVisibility(View.VISIBLE);
                return false;
            }

        });

        return true;
    }

    /**
     * 根据searchView弹出的suggestion列表position对应的文本
     *
     * @param searchView 要查找的 searchView
     * @param position   suggestion列表的position
     * @return 查找到的文本
     */
    private String getSuggestion(SearchView searchView, int position) {
        Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(
                position);
        return cursor.getString(cursor
                .getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
    }

    @Override
    public void onListItemClick(int position) {
        showCmdDetail(cmdList.get(position).replace(".md", ""));
    }

    @Override
    public void onBackPressed() {
        if (rvCmdList.getVisibility() != View.VISIBLE) {
            rvCmdList.setVisibility(View.VISIBLE);
            wvContent.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}
