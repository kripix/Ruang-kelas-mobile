package com.kripix.dev.ruangkelas.data.model

import java.io.Serializable

data class UserModel (
    val user: List<Data>
) : Serializable {
    data class Data (
        val id: Int,
        val username: String?,
        val nama_lengkap: String?,
        val email: String?,
        val password: String?,
        val alamat: String?,
        val icon: String?
    ) : Serializable
}
