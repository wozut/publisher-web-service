package tcla.contexts.tcla.database.springdatajpa.tcldriver

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.left
import arrow.core.nel
import arrow.core.right
import jakarta.inject.Named
import tcla.contexts.tcla.core.application.failures.Failure
import tcla.contexts.tcla.core.domain.tcldriver.TclDriverRepository
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriver
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverId
import tcla.contexts.tcla.core.domain.tcldriver.model.TclDriverName
import java.util.UUID

@Named
class SqlTclDriverRepository : TclDriverRepository {
    private val tclDrivers = listOf(
        TclDriver(
            id = TclDriverId(UUID.fromString("0fc00659-527b-4ad3-b449-007c9bb82a67")),
            name = TclDriverName("Team Characteristics"),
            children = listOf(
                TclDriverId(UUID.fromString("9607872f-059d-41ec-9662-f17778e5bc3a")),
                TclDriverId(UUID.fromString("66f9073f-43e7-4362-848e-010f4f53722c")),
                TclDriverId(UUID.fromString("fffb132c-154b-4f70-9290-0a8302e25e63"))
            ),
            parent = null
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("9607872f-059d-41ec-9662-f17778e5bc3a")),
            name = TclDriverName("Team Composition"),
            children = listOf(
                TclDriverId(UUID.fromString("3c2f2271-0551-454c-ac7b-74e2909b7159")),
                TclDriverId(UUID.fromString("583d82c7-96a3-4b6f-a71e-e45f46a949f7"))
            ),
            parent = TclDriverId(UUID.fromString("0fc00659-527b-4ad3-b449-007c9bb82a67"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("3c2f2271-0551-454c-ac7b-74e2909b7159")),
            name = TclDriverName("Team Complexity"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("9607872f-059d-41ec-9662-f17778e5bc3a"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("583d82c7-96a3-4b6f-a71e-e45f46a949f7")),
            name = TclDriverName("Team Competence"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("9607872f-059d-41ec-9662-f17778e5bc3a"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("66f9073f-43e7-4362-848e-010f4f53722c")),
            name = TclDriverName("Member Roles"),
            children = listOf(
                TclDriverId(UUID.fromString("4776965b-ae88-461f-bfe9-f2396d070fb0")),
                TclDriverId(UUID.fromString("adcbdd72-7727-4b3f-b630-3ef321fda248")),
                TclDriverId(UUID.fromString("b0821f0c-5708-421c-a3b8-6204ae11af56"))
            ),
            parent = TclDriverId(UUID.fromString("0fc00659-527b-4ad3-b449-007c9bb82a67"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("4776965b-ae88-461f-bfe9-f2396d070fb0")),
            name = TclDriverName("Role Clarity"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("66f9073f-43e7-4362-848e-010f4f53722c"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("adcbdd72-7727-4b3f-b630-3ef321fda248")),
            name = TclDriverName("Role Fit"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("66f9073f-43e7-4362-848e-010f4f53722c"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("b0821f0c-5708-421c-a3b8-6204ae11af56")),
            name = TclDriverName("Role Load"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("66f9073f-43e7-4362-848e-010f4f53722c"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("fffb132c-154b-4f70-9290-0a8302e25e63")),
            name = TclDriverName("Culture"),
            children = listOf(
                TclDriverId(UUID.fromString("9936e560-1b43-4df1-8f33-ccee53609e08")),
                TclDriverId(UUID.fromString("80730316-1903-46e4-b824-f808012634dd")),
                TclDriverId(UUID.fromString("7e1df8e7-7954-478f-b6d8-17a1cc94c3fc"))
            ),
            parent = TclDriverId(UUID.fromString("0fc00659-527b-4ad3-b449-007c9bb82a67"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("9936e560-1b43-4df1-8f33-ccee53609e08")),
            name = TclDriverName("Team Alignment"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("fffb132c-154b-4f70-9290-0a8302e25e63"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("80730316-1903-46e4-b824-f808012634dd")),
            name = TclDriverName("Team Interaction"),
            children = listOf(
                TclDriverId(UUID.fromString("d9922cef-111c-4247-8cfc-62026625c783")),
                TclDriverId(UUID.fromString("e451865d-0e18-4209-b1fa-5efeffe5d2b6"))
            ),
            parent = TclDriverId(UUID.fromString("fffb132c-154b-4f70-9290-0a8302e25e63"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("d9922cef-111c-4247-8cfc-62026625c783")),
            name = TclDriverName("Communication"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("80730316-1903-46e4-b824-f808012634dd"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("e451865d-0e18-4209-b1fa-5efeffe5d2b6")),
            name = TclDriverName("Knowledge Exchange"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("80730316-1903-46e4-b824-f808012634dd"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("7e1df8e7-7954-478f-b6d8-17a1cc94c3fc")),
            name = TclDriverName("Member Psychological Safety"),
            children = listOf(
                TclDriverId(UUID.fromString("56e29585-f823-47d1-990c-b19bbb8e1c39")),
                TclDriverId(UUID.fromString("d2f31415-9aa0-46e3-bde2-8ac550ef9ec3")),
                TclDriverId(UUID.fromString("373a00c8-07f0-4608-b3d6-fed22012ccab"))
            ),
            parent = TclDriverId(UUID.fromString("fffb132c-154b-4f70-9290-0a8302e25e63"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("56e29585-f823-47d1-990c-b19bbb8e1c39")),
            name = TclDriverName("Authenticity"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("7e1df8e7-7954-478f-b6d8-17a1cc94c3fc"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("d2f31415-9aa0-46e3-bde2-8ac550ef9ec3")),
            name = TclDriverName("Speaking-Up"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("7e1df8e7-7954-478f-b6d8-17a1cc94c3fc"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("373a00c8-07f0-4608-b3d6-fed22012ccab")),
            name = TclDriverName("Embracing Failure"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("7e1df8e7-7954-478f-b6d8-17a1cc94c3fc"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("d74d9b22-1e11-43df-bd1d-5c278e738ce4")),
            name = TclDriverName("Work Practices & Processes"),
            children = listOf(
                TclDriverId(UUID.fromString("9329f3ef-5c04-4956-ae32-14b517239d68")),
                TclDriverId(UUID.fromString("15e40f37-d863-467e-9f86-3818e165ad22")),
                TclDriverId(UUID.fromString("cd5ef942-a6f8-4a6e-8c4e-18c811b58c4c"))
            ),
            parent = null
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("9329f3ef-5c04-4956-ae32-14b517239d68")),
            name = TclDriverName("Use Of Information"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("d74d9b22-1e11-43df-bd1d-5c278e738ce4"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("15e40f37-d863-467e-9f86-3818e165ad22")),
            name = TclDriverName("Efficiency & Effectiveness"),
            children = listOf(
                TclDriverId(UUID.fromString("6635f911-daf8-4d1e-8a4b-6da9f9107201")),
                TclDriverId(UUID.fromString("eae15c6c-235b-4299-bc2f-5d5719a988fb")),
                TclDriverId(UUID.fromString("33a76fe3-3d60-484d-a44f-b9c038c35c54")),
                TclDriverId(UUID.fromString("4156000d-837e-4820-bc72-56cf73be8e51"))
            ),
            parent = TclDriverId(UUID.fromString("d74d9b22-1e11-43df-bd1d-5c278e738ce4"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("6635f911-daf8-4d1e-8a4b-6da9f9107201")),
            name = TclDriverName("Process"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("15e40f37-d863-467e-9f86-3818e165ad22"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("eae15c6c-235b-4299-bc2f-5d5719a988fb")),
            name = TclDriverName("Consistency"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("15e40f37-d863-467e-9f86-3818e165ad22"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("33a76fe3-3d60-484d-a44f-b9c038c35c54")),
            name = TclDriverName("Pace"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("15e40f37-d863-467e-9f86-3818e165ad22"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("4156000d-837e-4820-bc72-56cf73be8e51")),
            name = TclDriverName("Performance"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("15e40f37-d863-467e-9f86-3818e165ad22"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("cd5ef942-a6f8-4a6e-8c4e-18c811b58c4c")),
            name = TclDriverName("Adaptability"),
            children = listOf(
                TclDriverId(UUID.fromString("b1e1e80d-a4eb-497a-af14-5591c06abb1c")),
                TclDriverId(UUID.fromString("101bbde8-228c-4056-883b-add27306bb9c")),
                TclDriverId(UUID.fromString("efbd44d5-b79a-4e57-bfdf-d761aff3250b"))
            ),
            parent = TclDriverId(UUID.fromString("d74d9b22-1e11-43df-bd1d-5c278e738ce4"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("b1e1e80d-a4eb-497a-af14-5591c06abb1c")),
            name = TclDriverName("Resilience"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("cd5ef942-a6f8-4a6e-8c4e-18c811b58c4c"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("101bbde8-228c-4056-883b-add27306bb9c")),
            name = TclDriverName("Iterative Working"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("cd5ef942-a6f8-4a6e-8c4e-18c811b58c4c"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("efbd44d5-b79a-4e57-bfdf-d761aff3250b")),
            name = TclDriverName("Continuous Learning"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("cd5ef942-a6f8-4a6e-8c4e-18c811b58c4c"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("8ddef539-54f8-413c-87e5-a99eacd1db68")),
            name = TclDriverName("Task Characteristics"),
            children = listOf(
                TclDriverId(UUID.fromString("00cf4195-6bf4-4d5f-94dd-b045b4e735db")),
                TclDriverId(UUID.fromString("0c5c9ab1-6c3d-40f1-a700-cc677c26681f")),
                TclDriverId(UUID.fromString("9416fd0a-636e-4cda-a07c-edb93c888ee2"))
            ),
            parent = null
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("00cf4195-6bf4-4d5f-94dd-b045b4e735db")),
            name = TclDriverName("Problem Statement"),
            children = listOf(
                TclDriverId(UUID.fromString("b4ab706b-6f5f-4ac4-bbea-23f822a0b711")),
                TclDriverId(UUID.fromString("8e528d6a-5d2f-4287-9f9d-bcff50c76cdf"))
            ),
            parent = TclDriverId(UUID.fromString("8ddef539-54f8-413c-87e5-a99eacd1db68"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("b4ab706b-6f5f-4ac4-bbea-23f822a0b711")),
            name = TclDriverName("Problem Definition"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("00cf4195-6bf4-4d5f-94dd-b045b4e735db"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("8e528d6a-5d2f-4287-9f9d-bcff50c76cdf")),
            name = TclDriverName("Solution Alignment"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("00cf4195-6bf4-4d5f-94dd-b045b4e735db"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("0c5c9ab1-6c3d-40f1-a700-cc677c26681f")),
            name = TclDriverName("Complexity"),
            children = listOf(
                TclDriverId(UUID.fromString("8c95ba02-2fc1-46a4-b2c6-a4d7a3eb7524")),
                TclDriverId(UUID.fromString("e01fb140-36f5-4abc-b45b-ace3d4b8e43f"))
            ),
            parent = TclDriverId(UUID.fromString("8ddef539-54f8-413c-87e5-a99eacd1db68"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("8c95ba02-2fc1-46a4-b2c6-a4d7a3eb7524")),
            name = TclDriverName("Task Complexity"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("0c5c9ab1-6c3d-40f1-a700-cc677c26681f"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("e01fb140-36f5-4abc-b45b-ace3d4b8e43f")),
            name = TclDriverName("Contextual Complexity"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("0c5c9ab1-6c3d-40f1-a700-cc677c26681f"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("9416fd0a-636e-4cda-a07c-edb93c888ee2")),
            name = TclDriverName("Metrics"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("8ddef539-54f8-413c-87e5-a99eacd1db68"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("5a9f12ed-3e2d-445c-b699-8b426b1d7f9f")),
            name = TclDriverName("Work Environment & Tools"),
            children = listOf(
                TclDriverId(UUID.fromString("d5c1a5e3-c769-492f-bbf4-1fdf0d00d1ab")),
                TclDriverId(UUID.fromString("43792379-6c2e-4940-ba10-423a551f4741"))
            ),
            parent = null
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("d5c1a5e3-c769-492f-bbf4-1fdf0d00d1ab")),
            name = TclDriverName("Tools"),
            children = listOf(
                TclDriverId(UUID.fromString("894b7fab-c3ae-43e5-96a8-b36952b698ec")),
                TclDriverId(UUID.fromString("54986fdc-e3fd-4ff4-a776-cf8c5a206021"))
            ),
            parent = TclDriverId(UUID.fromString("5a9f12ed-3e2d-445c-b699-8b426b1d7f9f"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("894b7fab-c3ae-43e5-96a8-b36952b698ec")),
            name = TclDriverName("Tool Suitability"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("d5c1a5e3-c769-492f-bbf4-1fdf0d00d1ab"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("54986fdc-e3fd-4ff4-a776-cf8c5a206021")),
            name = TclDriverName("Tool Performance"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("d5c1a5e3-c769-492f-bbf4-1fdf0d00d1ab"))
        ),
        TclDriver(
            id = TclDriverId(UUID.fromString("43792379-6c2e-4940-ba10-423a551f4741")),
            name = TclDriverName("Environment"),
            children = listOf(),
            parent = TclDriverId(UUID.fromString("5a9f12ed-3e2d-445c-b699-8b426b1d7f9f"))
        )
    )

    override fun search(): Either<NonEmptyList<Failure>, List<TclDriver>> = tclDrivers.right()
    override fun findAllById(ids: List<TclDriverId>): Either<NonEmptyList<Failure>, List<TclDriver>> =
        tclDrivers
            .filter { tclDriver -> ids.contains(tclDriver.id) }
            .let { matchingEntities: List<TclDriver> ->
                when (matchingEntities.size < ids.size) {
                    true -> Failure.EntityNotFound.TclDriver.nel().left()
                    false -> matchingEntities.right()
                }
            }

}
