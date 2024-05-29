package com.kripix.dev.ruangkelas.data.kelas

var kelasList = mutableListOf<Kelas>()
val KELAS_ID_EKSTRA = "kelasExtra"
val PERTEMUAN_ID_EKSTRA = "pertemuanExtra"
class Kelas (
    val img: Int,
    val grade: String,
    val kelas: String,
    val creator: String,
    val pertemuanList: List<Pertemuan>,
    val id: Int? = kelasList.size
)

class Pertemuan (
    val id: Int,
    val kelasID:Int,
    val nomor: Int,
    val judul: String,
    val deskripsi: String,
)