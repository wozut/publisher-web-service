package tcla.contexts.tcla.typeform

import arrow.core.flatMap
import arrow.core.right
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import tcla.contexts.tcla.core.domain.assessment.model.AssessmentId
import tcla.contexts.tcla.core.domain.questionnaire.create.buildTclQuestionnaire
import tcla.contexts.tcla.core.domain.questionnaire.model.ExternalQuestionnaireId
import tcla.contexts.tcla.core.domain.questionnaire.model.QuestionnaireId
import tcla.contexts.tcla.core.domain.questionnaire.model.ResponseAcceptanceInterval
import java.time.Duration
import java.time.Instant
import java.util.UUID

class ResponsesToQuestionnaireFillingsKtTest {

    @Test
    fun `convert typeform responses into questionnaire fillings without parsing errors`() {
        ResponseAcceptanceInterval(start = Instant.now().plus(Duration.ofDays(1)), end = Instant.now().plus(Duration.ofDays(7)))
            .flatMap { interval ->
                val throwable: Throwable? = catchThrowable {
                    responsesToQuestionnaireFillings(
                        responseBody = RESPONSE_BODY,
                        questions = buildTclQuestionnaire(
                            questionnaireId = QuestionnaireId(UUID.randomUUID()),
                            externalQuestionnaireId = ExternalQuestionnaireId("sad"),
                            externalQuestionnaireIsPublic = false,
                            assessmentId = AssessmentId(UUID.randomUUID()),
                            interval = interval,
                            maximumAmountToBeCollected = 5,
                            includeGenderQuestion = true,
                            includeWorkFamiliarityQuestion = true,
                            includeTeamFamiliarityQuestion = true,
                            includeModeOfWorkingQuestion = true
                        ).questions
                    )
                }
                throwable.right()
            }.fold(
                { Assertions.fail(it.toString()) },
                { throwable -> assertThat(throwable).isNull() }
            )
    }

    companion object {
        private const val RESPONSE_BODY = """
 {
    "items": [
        {
            "landing_id": "lojwjd6jsfhqs1lojwjwhz6ul6u4acl1",
            "token": "lojwjd6jsfhqs1lojwjwhz6ul6u4acl1",
            "response_id": "lojwjd6jsfhqs1lojwjwhz6ul6u4acl1",
            "landed_at": "2023-10-07T14:44:45Z",
            "submitted_at": "2023-10-07T14:47:14Z",
            "metadata": {
                "user_agent": "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36",
                "platform": "other",
                "referer": "https://hasukdft0yo.typeform.com/to/inf7K4JB",
                "network_id": "e8613b3dec",
                "browser": "default"
            },
            "hidden": {},
            "calculated": {
                "score": 0
            },
            "answers": [
                {
                    "field": {
                        "id": "aYKNSJRajXMy",
                        "type": "opinion_scale",
                        "ref": "tcl-question-1"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "SsqSpDF9NQZT",
                        "type": "opinion_scale",
                        "ref": "tcl-question-2"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "7ZsK9onlL7pm",
                        "type": "opinion_scale",
                        "ref": "tcl-question-3"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "QWy3GwCxGDEG",
                        "type": "opinion_scale",
                        "ref": "tcl-question-4"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "Yy0Y8TUe6Put",
                        "type": "opinion_scale",
                        "ref": "tcl-question-5"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "vyuurFsKpqtv",
                        "type": "opinion_scale",
                        "ref": "tcl-question-6"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "9uMc4otHTtuA",
                        "type": "opinion_scale",
                        "ref": "tcl-question-7"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "gL0XDkfIKrsN",
                        "type": "opinion_scale",
                        "ref": "tcl-question-8"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "V8vFMpFq8koB",
                        "type": "opinion_scale",
                        "ref": "tcl-question-9"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "p5bdrIUNfIP3",
                        "type": "opinion_scale",
                        "ref": "tcl-question-10"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "hchQ4gto6lTZ",
                        "type": "opinion_scale",
                        "ref": "tcl-question-11"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "udkNUSa4zALS",
                        "type": "opinion_scale",
                        "ref": "tcl-question-12"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "kTGzrAMOuvUF",
                        "type": "opinion_scale",
                        "ref": "tcl-question-13"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "ZjesLBjJUkmF",
                        "type": "opinion_scale",
                        "ref": "tcl-question-14"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "KsgL6z8YSjni",
                        "type": "opinion_scale",
                        "ref": "tcl-question-15"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "CqYx8ssLFWbp",
                        "type": "opinion_scale",
                        "ref": "tcl-question-16"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "BoiMN4d3ZoHk",
                        "type": "opinion_scale",
                        "ref": "tcl-question-17"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "tga11yNXzfWA",
                        "type": "opinion_scale",
                        "ref": "tcl-question-18"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "wpst90N5oSWM",
                        "type": "opinion_scale",
                        "ref": "tcl-question-19"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "pA63OjSR4xvz",
                        "type": "opinion_scale",
                        "ref": "tcl-question-20"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "cpWRZmZic12r",
                        "type": "opinion_scale",
                        "ref": "tcl-question-21"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "auNp0g3diDmE",
                        "type": "opinion_scale",
                        "ref": "tcl-question-22"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "rqObt93XoEQf",
                        "type": "opinion_scale",
                        "ref": "tcl-question-23"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "NVJcUpMqPzdU",
                        "type": "opinion_scale",
                        "ref": "tcl-question-24"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "l44sBYN34NZO",
                        "type": "opinion_scale",
                        "ref": "tcl-question-25"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "ybyUxIGDV9cl",
                        "type": "opinion_scale",
                        "ref": "tcl-question-26"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "X0tiEQufeXzf",
                        "type": "opinion_scale",
                        "ref": "tcl-question-27"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "lnWtqtNHRuGR",
                        "type": "opinion_scale",
                        "ref": "tcl-question-28"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "g1zNA15hl3Mr",
                        "type": "opinion_scale",
                        "ref": "tcl-question-29"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "8d5N4knP7rU4",
                        "type": "opinion_scale",
                        "ref": "tcl-question-30"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "5D11fRX7mMCK",
                        "type": "opinion_scale",
                        "ref": "tcl-question-31"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "oanzMSNCjXDM",
                        "type": "opinion_scale",
                        "ref": "tcl-question-32"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "Gdz9gva06XG1",
                        "type": "opinion_scale",
                        "ref": "tcl-question-33"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "v2DqWrp1YvYN",
                        "type": "opinion_scale",
                        "ref": "tcl-question-34"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "st1nDou2Mf3d",
                        "type": "opinion_scale",
                        "ref": "tcl-question-35"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "4igpYZkICxo7",
                        "type": "opinion_scale",
                        "ref": "tcl-question-36"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "MtkmWhl8ZGxv",
                        "type": "opinion_scale",
                        "ref": "tcl-question-37"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "X8fqPza7bLS1",
                        "type": "opinion_scale",
                        "ref": "tcl-question-38"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "a7OHNUQI2jkb",
                        "type": "opinion_scale",
                        "ref": "tcl-question-39"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "YUlDAUrOIVtO",
                        "type": "opinion_scale",
                        "ref": "tcl-question-40"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "kWcZ7xww8AP6",
                        "type": "opinion_scale",
                        "ref": "tcl-question-41"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "Whs7aNtOEARJ",
                        "type": "opinion_scale",
                        "ref": "tcl-question-42"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "E4uGQgvTQP7O",
                        "type": "opinion_scale",
                        "ref": "tcl-question-43"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "ywHODYOx2r6q",
                        "type": "opinion_scale",
                        "ref": "tcl-question-44"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "bGTAUimNCptN",
                        "type": "opinion_scale",
                        "ref": "tcl-question-45"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "7SyhbGeHvRKa",
                        "type": "opinion_scale",
                        "ref": "tcl-question-46"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "pWEAi7UJ3f1T",
                        "type": "opinion_scale",
                        "ref": "tcl-question-47"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "6YoY3XVSJzAu",
                        "type": "opinion_scale",
                        "ref": "tcl-question-48"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "HpYpqFAoUeuV",
                        "type": "opinion_scale",
                        "ref": "tcl-question-49"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "iACYLjRmX3mK",
                        "type": "opinion_scale",
                        "ref": "tcl-question-50"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "HyxI4ukntazG",
                        "type": "opinion_scale",
                        "ref": "tcl-question-51"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "Jpqz2fRmKdtD",
                        "type": "opinion_scale",
                        "ref": "tcl-question-52"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "tYGOdpHvhpck",
                        "type": "opinion_scale",
                        "ref": "tcl-question-53"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "n6Ib9vam8B5D",
                        "type": "opinion_scale",
                        "ref": "tcl-question-54"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "rYKcOxBcWXcM",
                        "type": "opinion_scale",
                        "ref": "tcl-question-55"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "7UPwQ3xyr0b6",
                        "type": "opinion_scale",
                        "ref": "tcl-question-56"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "HxyzW3Ac5wXw",
                        "type": "opinion_scale",
                        "ref": "tcl-question-57"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "VrLGycUJtdX0",
                        "type": "opinion_scale",
                        "ref": "tcl-question-58"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "wf5fKbqxWIVX",
                        "type": "opinion_scale",
                        "ref": "tcl-question-59"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "o4AZi79xUtP6",
                        "type": "opinion_scale",
                        "ref": "tcl-question-60"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "xlpDsn3D0RlX",
                        "type": "opinion_scale",
                        "ref": "tcl-question-61"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "UTkphTdeTIu6",
                        "type": "opinion_scale",
                        "ref": "tcl-question-62"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "eUZXwlBWt8Zs",
                        "type": "opinion_scale",
                        "ref": "tcl-question-63"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "OPvm0NyW6q3q",
                        "type": "opinion_scale",
                        "ref": "tcl-question-64"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "fKyYbYWnz1mp",
                        "type": "opinion_scale",
                        "ref": "tcl-question-65"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "N4iMj8x8BGs8",
                        "type": "opinion_scale",
                        "ref": "tcl-question-66"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "hFIloaLA2E3l",
                        "type": "opinion_scale",
                        "ref": "tcl-question-67"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "xnsxKRiSyfWH",
                        "type": "opinion_scale",
                        "ref": "tcl-question-68"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "Um9T2pwQ1f6Q",
                        "type": "opinion_scale",
                        "ref": "tcl-question-69"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "FPd516yzk1Gz",
                        "type": "opinion_scale",
                        "ref": "tcl-question-70"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "gBmSdGSE1Oh4",
                        "type": "opinion_scale",
                        "ref": "tcl-question-71"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "437GqAVzE32v",
                        "type": "opinion_scale",
                        "ref": "tcl-question-72"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "LZSMeL5zxC5v",
                        "type": "opinion_scale",
                        "ref": "tcl-question-73"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "r91P9IR5YxeN",
                        "type": "opinion_scale",
                        "ref": "tcl-question-74"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "8dm06rZp7Syr",
                        "type": "opinion_scale",
                        "ref": "tcl-question-75"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "51Zb1vy1zwQd",
                        "type": "opinion_scale",
                        "ref": "tcl-question-76"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "19kLisd1H1ec",
                        "type": "opinion_scale",
                        "ref": "tcl-question-77"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "75gE6Gr5Uzfg",
                        "type": "opinion_scale",
                        "ref": "tcl-question-78"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "Bihaau4IQSqK",
                        "type": "opinion_scale",
                        "ref": "tcl-question-79"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "rvK7kTyxNP0m",
                        "type": "opinion_scale",
                        "ref": "tcl-question-80"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "oPMFGa4a5sVi",
                        "type": "opinion_scale",
                        "ref": "tcl-question-81"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "tveHaqNxokHn",
                        "type": "opinion_scale",
                        "ref": "tcl-question-82"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "raVsWDyPeyct",
                        "type": "opinion_scale",
                        "ref": "tcl-question-83"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "WAr4mAIsgxnm",
                        "type": "opinion_scale",
                        "ref": "tcl-question-84"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "L1wc7smhebfB",
                        "type": "opinion_scale",
                        "ref": "tcl-question-85"
                    },
                    "type": "number",
                    "number": 3
                },
                {
                    "field": {
                        "id": "bJHPY2kMNUzY",
                        "type": "opinion_scale",
                        "ref": "tcl-question-86"
                    },
                    "type": "number",
                    "number": 3
                }
            ]
        }
    ],
    "total_items": 1,
    "page_count": 1
}
        """
    }
}
