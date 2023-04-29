package com.yoveuio.demo.common.utils

import org.springframework.util.PropertyPlaceholderHelper
import java.util.function.UnaryOperator

@Suppress("unused")
object PlaceholderUtils {
    private const val PLACEHOLDER_PREFIX = "{{"
    private const val PLACEHOLDER_SUFFIX = "}}"

    fun replace(source: String?, placeholderResolver: UnaryOperator<String?>): String {
        val propertyPlaceholderHelper = PropertyPlaceholderHelper(PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX)
        return propertyPlaceholderHelper.replacePlaceholders(
            source!!
        ) { t: String? -> placeholderResolver.apply(t) }
    }
}