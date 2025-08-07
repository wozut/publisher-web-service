package tcla.libraries.jsonserialization

fun String.escaped(): String =
    replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\"", "\\\"")
        .replace("\t", "\\t")
        .replace("\b", "\\b")
