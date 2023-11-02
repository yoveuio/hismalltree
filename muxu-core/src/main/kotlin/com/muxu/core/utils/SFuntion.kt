package com.muxu.core.utils

import java.io.Serializable
import java.util.function.Function


interface SFunction<T, R> : Function<T, R>, Serializable

