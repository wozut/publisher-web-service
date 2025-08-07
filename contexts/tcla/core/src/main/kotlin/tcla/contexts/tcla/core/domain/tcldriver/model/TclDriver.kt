package tcla.contexts.tcla.core.domain.tcldriver.model

data class TclDriver(
    val id: TclDriverId,
    val name: TclDriverName,
    val children: List<TclDriverId>,
    val parent: TclDriverId?
) {
    val isCluster: Boolean = parent == null
    val hasChildren: Boolean = children.isNotEmpty()

    fun addChild(tclDriverId: TclDriverId): TclDriver = copy(children = children.plus(tclDriverId))

    fun setParent(tclDriverId: TclDriverId?): TclDriver = copy(parent = tclDriverId)
}
