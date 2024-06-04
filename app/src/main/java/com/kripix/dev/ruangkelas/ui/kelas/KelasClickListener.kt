package com.kripix.dev.ruangkelas.ui.kelas

import com.kripix.dev.ruangkelas.data.model.KelasModel

interface KelasClickListener {
    fun onClick(kelas: KelasModel.Data)

}