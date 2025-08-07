package tcla.contexts.tcla.webapi.springweb.tcldriver.jsonapi


data class TclDriverResourceAttributes(
    val name: String,
    val children: List<String>,
    val parent: String?
)
