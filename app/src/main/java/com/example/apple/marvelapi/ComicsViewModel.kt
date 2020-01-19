package com.example.apple.marvelapi

import android.databinding.BaseObservable
import android.databinding.BindingAdapter
import android.databinding.ObservableField
import android.widget.ImageView


class ComicsViewModel : BaseObservable() {
    var name = ObservableField<String>()
    var description = ObservableField<String>()
}
