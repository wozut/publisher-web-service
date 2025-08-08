package tcla.contexts.tcla.core.domain.report.model


object CopyrightTextMother {
    fun default(value: String = "Copyright (c) 2023 Team Topologies Ltd and FlowOnRails SL. All Rights Reserved."): CopyrightText =
        CopyrightText(
            value = value
        )
}
