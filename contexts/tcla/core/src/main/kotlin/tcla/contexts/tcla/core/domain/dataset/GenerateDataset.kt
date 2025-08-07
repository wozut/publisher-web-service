package tcla.contexts.tcla.core.domain.dataset

import java.io.File

fun main() {
    generateDataset("/absolute/path/to/file.csv")
}

fun generateDataset(path: String) {
    val file = File(path)
    file.appendText("java variable declaration,is good name\n")

    val one = "1"
    genericNouns.forEach { genericNoun ->
        countableNouns.forEach { countableNoun ->
            val numberOf =
                "numberOf${pluralize(countableNoun.replaceFirstChar { firstChar: Char -> firstChar.uppercase() })}"
            file.appendText(csvLine("$javaIntType $genericNoun = 10;", numberOf))
            file.appendText(csvLine("$javaIntType $numberOf = 10;", one))
            val indexOf = "indexOf${countableNoun.replaceFirstChar { firstChar: Char -> firstChar.uppercase() }}"
            file.appendText(csvLine("$javaIntType $genericNoun = 10;", indexOf))
            file.appendText(csvLine("$javaIntType $indexOf = 10;", one))
            val positionOf = "positionOf${countableNoun.replaceFirstChar { firstChar: Char -> firstChar.uppercase() }}"
            file.appendText(csvLine("$javaIntType $genericNoun = 10;", positionOf))
            file.appendText(csvLine("$javaIntType $positionOf = 10;", one))
            val orderOf = "orderOf${countableNoun.replaceFirstChar { firstChar: Char -> firstChar.uppercase() }}"
            file.appendText(csvLine("$javaIntType $genericNoun = 10;", orderOf))
            file.appendText(csvLine("$javaIntType $orderOf = 10;", one))

            file.appendText(csvLine("$javaLongType $genericNoun = 10L;", numberOf))
            file.appendText(csvLine("$javaLongType $numberOf = 10L;", one))

            file.appendText(csvLine("$javaLongType $genericNoun = 10L;", indexOf))
            file.appendText(csvLine("$javaLongType $indexOf = 10L;", one))

            file.appendText(csvLine("$javaLongType $genericNoun = 10L;", positionOf))
            file.appendText(csvLine("$javaLongType $positionOf = 10L;", one))

            file.appendText(csvLine("$javaLongType $genericNoun = 10L;", orderOf))
            file.appendText(csvLine("$javaLongType $orderOf = 10L;", one))

        }

        uncountableNouns.forEach { uncountableNoun ->
            val amountOf = "amountOf${uncountableNoun.replaceFirstChar { firstChar: Char -> firstChar.uppercase() }}"
            file.appendText(csvLine("$javaFloatType $genericNoun = 10.0f;", amountOf))
            file.appendText(csvLine("$javaFloatType $amountOf = 10.0f;", one))

            file.appendText(csvLine("$javaDoubleType $genericNoun = 10.0d;", amountOf))
            file.appendText(csvLine("$javaDoubleType $amountOf = 10.0d;", one))

        }

        countableNouns.plus(uncountableNouns).forEach { noun ->
            typesOfMeasurement.forEach { typeOfMeasurement ->
                val typeOfMeasurementOf =
                    "${typeOfMeasurement}Of${noun.replaceFirstChar { firstChar: Char -> firstChar.uppercase() }}"
                file.appendText(csvLine("$javaFloatType $genericNoun = 10.0f;", typeOfMeasurementOf))
                file.appendText(csvLine("$javaFloatType $typeOfMeasurementOf = 10.0f;", one))

                file.appendText(csvLine("$javaDoubleType $genericNoun = 10.0d;", typeOfMeasurementOf))
                file.appendText(csvLine("$javaDoubleType $typeOfMeasurementOf = 10.0d;", one))
            }
        }

        adjectives.forEach { adjective ->
            val isVariableName = "is${adjective.replaceFirstChar { firstChar: Char -> firstChar.uppercase() }}"
            file.appendText(csvLine("$javaBooleanType $genericNoun = true;", isVariableName))
            file.appendText(csvLine("$javaBooleanType $isVariableName = true;", one))

            file.appendText(csvLine("$javaBooleanType $genericNoun = false;", isVariableName))
            file.appendText(csvLine("$javaBooleanType $isVariableName = false;", one))
        }
    }

    countableNouns.forEach { countableNoun ->
        val rightVariableName = pluralize(countableNoun)
        file.appendText(csvLine("List<${countableNoun.replaceFirstChar { firstChar: Char -> firstChar.uppercase() }}> ${countableNoun}List = new ArrayList<${countableNoun.replaceFirstChar { firstChar: Char -> firstChar.uppercase() }}>();", rightVariableName))
        file.appendText(csvLine("List<${countableNoun.replaceFirstChar { firstChar: Char -> firstChar.uppercase() }}> $rightVariableName = new ArrayList<${countableNoun.replaceFirstChar { firstChar: Char -> firstChar.uppercase() }}>();", one))
    }
}

fun csvLine(javaVariableDeclaration: String, isGoodName: String): String =
    "${csvRow(javaVariableDeclaration, isGoodName)}\n"

fun csvRow(javaVariableDeclaration: String, isGoodName: String): String = "$javaVariableDeclaration,$isGoodName"

private fun pluralize(countableNoun: String) = "${countableNoun}s"


const val javaTextType = "String"
const val javaIntType = "int"
const val javaLongType = "long"
const val javaFloatType = "float"
const val javaDoubleType = "double"
const val javaBooleanType = "boolean"
const val javaCharType = "char"

val javaIntegerTypes = listOf(javaIntType, javaLongType)

val javaPrimitiveDataTypes =
    listOf(
        javaIntType,
        javaLongType,
        javaFloatType,
        javaDoubleType,
        javaBooleanType,
        javaCharType
    )

val adjectives = listOf(
    "valid",
    "enabled",
    "active",
    "operational"
)

val countableNouns = listOf(
    "book",
    "car"
)

val uncountableNouns = listOf(
    "paper",
    "water",
)

val typesOfMeasurement = listOf(
    "weight",
    "height",
    "length",
    "depth",
    "width",
    "volume",
)

val genericNouns = listOf(
    "data",
    "value"
)

val abbreviations = listOf(
    "a",
    "b",
    "c",
    "d",
    "e",
    "f",
    "g",
    "h",
    "i",
    "j",
    "k",
    "l",
    "m",
    "n",
    "o",
    "p",
    "q",
    "r",
    "s",
    "t",
    "u",
    "v",
    "w",
    "x",
    "y",
    "z",
)

/**
 * TODO:
 *
 * generate correct line for each incorrect line
 *
 * int i users -> numberOfUsers
 *
 * List<User> userList -> users
 * List<Book> bookList -> book
 *
 * boolean b = true -> isValid, isEnabled, isActive
 *
 * number -> numberOfBooks, indexOf, position, order
 * data ->
 * temp
 * value
 * t1 -> temperature1
 * t2 -> temperature2
 * avgT -> averageTemperature
 * r -> result
 * r -> radius
 *
 * "frequency",
 * "speed",
 * "time"
 *
 * nonspecific
 * abbreviation
 */