package com.github.maplepro.kotsuite

internal class ConversionException(
    message: String,
    internal val isError: Boolean = false,
    exception: Throwable? = null
) : RuntimeException(message, exception)