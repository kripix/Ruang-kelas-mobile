package com.kripix.dev.ruangkelas.data.model

data class KelasModel (
    val kelas: List<Data>
) {
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
    )
}