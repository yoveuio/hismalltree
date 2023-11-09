package com.hismalltree.core.utils.support

import java.util.*
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

class SFuntionUtils private constructor() {

    companion object {

        fun <T> fieldName(property: KProperty0<T>): String {
            return property.name
        }

        fun <T> fieldName(kFunction0: KProperty1<T, String?>): String {
            return handleFieldName(kFunction0.name)
        }

        fun <T> fieldName(kFunction0: KMutableProperty1<T, String?>): String {
            return handleFieldName(kFunction0.name)
        }

        private fun handleFieldName(name: String): String {
            var fieldName = name.substring("get".length)
            fieldName = fieldName.replaceFirst(
                (fieldName[0].toString() + "").toRegex(),
                (fieldName[0].toString() + "").lowercase(Locale.getDefault())
            )
            return fieldName
        }

        @JvmStatic
        fun main(args: Array<String>) {
            print(fieldName(A::a))
        }
    }

}

class A(val a: String) {
    fun getV(): String{
        return a;
    }
}
