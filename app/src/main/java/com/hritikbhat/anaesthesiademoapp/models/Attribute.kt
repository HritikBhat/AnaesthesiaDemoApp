package com.hritikbhat.anaesthesiademoapp.models

data class Attribute(
    val attribute_image_url: String,
    val color_code: Any,
    val images: List<String>,
    val option_id: String,
    val price: String,
    val swatch_url: String,
    val value: String
)