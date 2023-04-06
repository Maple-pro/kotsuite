package org.kotsuite.intellij

internal class ConversionException(
    message: String,
    internal val isError: Boolean = false,
    exception: Throwable? = null
) : RuntimeException(message, exception)