package xyz.fcampbell.chatheads.testapp.util

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View

fun LayoutInflater.inflate(@LayoutRes resource: Int): View = inflate(resource, null)
