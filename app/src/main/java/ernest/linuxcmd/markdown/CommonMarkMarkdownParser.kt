package ernest.linuxcmd.markdown

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.InputStreamReader

class CommonMarkMarkdownParser : MarkdownParser {
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