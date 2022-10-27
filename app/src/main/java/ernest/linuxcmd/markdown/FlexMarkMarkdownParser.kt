package ernest.linuxcmd.markdown

import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.parser.Parser
import java.io.InputStreamReader

class FlexMarkMarkdownParser : MarkdownParser {
    private val parser = Parser.builder().build()
    private val htmlRenderer = HtmlRenderer.builder().build()
    override fun init() {

    }

    override fun parseMdToHtml(reader: InputStreamReader): String {
        return htmlRenderer.render(parser.parseReader(reader))
    }

    override fun parseMdToHtml(text: String): String {
        return htmlRenderer.render(parser.parse(text))
    }
}