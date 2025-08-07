package tcla.contexts.tcla.webapi.springweb.tcldriver.jsonapi

import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver


data class TclDriverResource(
    val id: String,
    val attributes: TclDriverResourceAttributes,
) {
    val type: String = TCL_DRIVER_JSON_API_TYPE
}

fun TclDriver.toResource(): TclDriverResource =
    TclDriverResource(
        id = id.value.toString(),
        attributes = TclDriverResourceAttributes(
            name = name.value,
            children = children.map { it.value.toString() },
            parent = when (parent) {
                null -> null
                else -> parent!!.value.toString()
            }
        )
    )
