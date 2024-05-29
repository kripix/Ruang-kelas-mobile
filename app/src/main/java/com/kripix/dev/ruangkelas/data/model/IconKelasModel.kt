package com.kripix.dev.ruangkelas.data.model

data class IconKelasModel (
    val iconKelas: List<Data>
) {
    data class Data (
        val id: Int,
        val icon_kelas: String?
    )
}