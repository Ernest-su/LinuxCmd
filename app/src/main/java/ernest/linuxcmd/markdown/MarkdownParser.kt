package ernest.linuxcmd.markdown

import java.io.InputStreamReader

interface MarkdownParser {
    fun init()

    fun parseMdToHtml(reader: InputStreamReader): String

    fun parseMdToHtml(text: String): String
}