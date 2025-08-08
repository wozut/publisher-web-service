package tcla.contexts.tcla.core.domain.tcldriver.model

import java.util.Random

object TclDriverMother {
    fun default(
        id: TclDriverId = TclDriverIdMother.default(),
        name: TclDriverName = TclDriverNameMother.default(),
        children: List<TclDriverId> = listOf(TclDriverIdMother.default(), TclDriverIdMother.default()),
        parent: TclDriverId? = TclDriverIdMother.default()
    ): TclDriver = TclDriver(
        id = id,
        name = name,
        children = children,
        parent = parent
    )

    fun name(name: String = "Driver - ${Random().nextInt()}"): TclDriverName = TclDriverNameMother.default(name)
}
