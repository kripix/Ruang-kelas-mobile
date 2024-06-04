package com.kripix.dev.ruangkelas.data.model

import java.io.Serializable

val KELAS_ID_EKSTRA = "kelasExtra"
val KELAS_DATA_EXTRA = "kelasListExtra"

data class KelasModel (
    val kelas: List<Data>
) : Serializable {
    data class Data (
        val id: Int,
        val kode_kelas: String?,
        val nama_kelas: String?,
        val nama_grade: String?,
        val wali_kelas: String?,
        val icon_kelas: String?,
        val deskripsi: String?,
        val created_at: String?,
        val updated_at: String?
    ) : Serializable
}
