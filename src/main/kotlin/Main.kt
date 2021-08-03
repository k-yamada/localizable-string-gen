import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

data class StringData(
    val name: String,
    val text: String
) {

    val iosLine: String
        get() {
            return """"$name" = "$text";"""
        }
}

internal fun readStrings(filePath: String): List<StringData> {
    val factory = DocumentBuilderFactory.newInstance()
    val builder = factory.newDocumentBuilder()
    val document = builder.parse(File(filePath))
    val resource = document.documentElement
    val strings = resource.getElementsByTagName("string")

    val result = mutableListOf<StringData>()
    for (i in 0 until strings.length) {
        val element = strings.item(i) as Element
        val name = element.getAttribute("name")
        val text = element.textContent
        result.add(StringData(name, text))
    }
    return result
}

fun main(args: Array<String>) {
    val parser = ArgParser("localizable-string-gen")
    val input by parser.option(ArgType.String, shortName = "i", description = "Input strings.xml").required()
    val output by parser.option(ArgType.String, shortName = "o", description = "Output Localizable.string")
    parser.parse(args)

    val strings = readStrings(input)
    val buf = StringBuilder().also { sbuilder ->
        strings.forEach {
            sbuilder.appendLine(it.iosLine)
            println(it.iosLine)
        }
    }
    val file = File("$output")
    file.writeText(buf.toString())
}
