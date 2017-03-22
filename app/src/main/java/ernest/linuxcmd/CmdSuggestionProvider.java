package ernest.linuxcmd;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by suqishuo on 2017/3/22.
 * desc:
 */

public class CmdSuggestionProvider extends ContentProvider {
    private static final String[] COLUMNS = {BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1,};
    private List<String> fileNames = new ArrayList<>();

    @Override
    public boolean onCreate() {
        AssetManager assetManager = getContext().getAssets();
        if (assetManager != null) {
            try {
                fileNames.addAll(Arrays.asList(assetManager.list("cmd")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        MatrixCursor cursor = new MatrixCursor(COLUMNS);
        int index = 0;
        for (int i = 0; i < fileNames.size(); i++) {
            String cmd = fileNames.get(i).replace(".md", "");
            if (cmd.toLowerCase().contains(uri.getLastPathSegment().toLowerCase())) {
                cursor.addRow(new String[]{String.valueOf(index), cmd});
                index++;
            }
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
