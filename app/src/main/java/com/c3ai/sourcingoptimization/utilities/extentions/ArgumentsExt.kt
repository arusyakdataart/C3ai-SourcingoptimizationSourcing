package com.c3ai.sourcingoptimization.utilities.extentions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1

inline fun <reified T: Any> intentFor(context: Context): Intent = Intent(context, T::class.java)

inline fun <reified T : Activity> Context.createIntent(vararg extras: Pair<KProperty1<T, *>, Any?>): Intent {
    return intentFor<T>(this).setExtras(*extras)
}

fun <T : Fragment> T.setArgs(vararg args: Pair<KProperty0<Any?>, Any?>): T = apply {
    arguments = bundleOf(*args.map { it.first.name to it.second }.toTypedArray())
}

fun <T : Activity> Intent.setExtras(vararg extras: Pair<KProperty1<T, Any?>, Any?>): Intent = apply {
    putExtra(DELEGATED_ARGS_BUNDLE_EXTRA, bundleOf(*extras.map { it.first.name to it.second }.toTypedArray()))
}

inline fun <reified T> Fragment.arg(
    defaultValue: T? = null
) = FragmentArgumentDelegate(defaultValue, T::class.java, null is T)

inline fun <reified T> Activity.extra(
    defaultValue: T? = null
) = ActivityExtraDelegate(defaultValue, T::class.java, null is T)

@Suppress("ClassNaming")
private object UNINITIALIZED_VALUE

internal const val DELEGATED_ARGS_BUNDLE_EXTRA = "DELEGATED_ARGS_BUNDLE_EXTRA"

class ActivityExtraDelegate<T>(
    defaultValue: T?,
    clazz: Class<T>,
    nullable: Boolean
) : BundleValueDelegate<Activity, T>(defaultValue, clazz, nullable) {

    override fun getBundle(thisRef: Activity) = thisRef.intent.getBundleExtra(DELEGATED_ARGS_BUNDLE_EXTRA)
}

class FragmentArgumentDelegate<T>(
    defaultValue: T?,
    clazz: Class<T>,
    nullable: Boolean
) : BundleValueDelegate<Fragment, T>(defaultValue, clazz, nullable) {

    override fun getBundle(thisRef: Fragment) = thisRef.arguments
}

abstract class BundleValueDelegate<in R, T> internal constructor(
    private val defaultValue: T?,
    private val clazz: Class<T>,
    private val isNullable: Boolean
) : ReadOnlyProperty<R, T> {

    private var internalValue: Any? = UNINITIALIZED_VALUE

    override fun getValue(thisRef: R, property: KProperty<*>): T {
        val key = property.name

        return when (internalValue) {
            UNINITIALIZED_VALUE -> provideValue(thisRef, key).also { internalValue = it }
            else -> internalValue as T
        }
    }

    private fun provideValue(thisRef: R, key: String): T {
        val bundleValue = getBundle(thisRef)?.get(key)
        return when {
            bundleValue == null -> {
                if (isNullable) {
                    null as T
                } else {
                    defaultValue ?: error("No default value provided for argument $key")
                }
            }
            clazz.isInstance(bundleValue) -> bundleValue as T
            else -> throw ClassCastException("Incompatible type: ${bundleValue::class.java}, excepted: $clazz")
        }
    }

    abstract fun getBundle(thisRef: R): Bundle?
}
