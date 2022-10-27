package ernest.linuxcmd.markdown

import android.content.Context

object MarkDownFileUtil {
    private val regex = Regex("\\n===+([\\s\\S]*?)##")
    fun getTitle(context: Context, fileName: String): String {
        return getTitleFromContent(context.assets.open("command/$fileName").reader().readText())

    }

    private fun getTitleFromContent(content: String): String {
        return (regex.find(content)?.groupValues?.get(1) ?: "").trim()
    }
}