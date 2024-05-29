package com.kripix.dev.ruangkelas.data.api.kelas

import com.kripix.dev.ruangkelas.data.model.IconKelasModel
import com.kripix.dev.ruangkelas.data.model.KelasModel
import com.kripix.dev.ruangkelas.data.model.TambahKelasModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface KelasApiEndpoint {
    @GET("read.php")
    fun data() : Call<KelasModel>

    @GET("get_icon.php")
    fun get_icon() : Call<IconKelasModel>

    @FormUrlEncoded
    @POST("create.php")
    fun create(
        @Field("nama_kelas") kelas : String,
        @Field("nama_grade") grade : String,
        @Field("deskripsi") deskripsi : String,
        @Field("icon_kelas") icon:Int,
        @Field("wali_kelas") user: Int,
        @Field("kode_kelas") kode:String
    ): Call<TambahKelasModel>

}