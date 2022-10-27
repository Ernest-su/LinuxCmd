package ernest.linuxcmd

import android.app.SearchManager
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.provider.BaseColumns
import java.util.*

/**
 * Created by suqishuo on 2017/3/22.
 * desc:
 */
class CmdSuggestionProvider : ContentProvider() {
    private val fileNames: MutableList<String> = ArrayList()
    override fun onCreate(): Boolean {
        val assetManager = context?.assets
        if (assetManager != null) {
            try {
                assetManager.list("command")?.let {
                    fileNames.addAll(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor {
        val cursor = MatrixCursor(COLUMNS)
        var index = 0
        for (i in fileNames.indices) {
            val cmd = fileNames[i].replace(".md", "")
            if (cmd.lowercase(Locale.getDefault())
                    .contains(uri.lastPathSegment!!.lowercase(Locale.getDefault()))
            ) {
                cursor.addRow(arrayOf(index.toString(), cmd))
                index++
            }
        }
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    companion object {
        private val COLUMNS = arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1)
    }
}