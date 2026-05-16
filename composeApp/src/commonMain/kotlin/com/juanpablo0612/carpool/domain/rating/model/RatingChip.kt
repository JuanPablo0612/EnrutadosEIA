package com.juanpablo0612.carpool.domain.rating.model

sealed class RatingChip(val key: String) {
    data object Puntual : RatingChip("puntual")
    data object Amable : RatingChip("amable")
    data object CarroLimpio : RatingChip("carro_limpio")
    data object ConduccionSegura : RatingChip("conduccion_segura")
    data object BuenaConversacion : RatingChip("buena_conversacion")
    data object Respetuoso : RatingChip("respetuoso")
    data object AporteExacto : RatingChip("aporte_exacto")
    data object BuenViaje : RatingChip("buen_viaje")

    companion object {
        fun fromKey(key: String): RatingChip? = listOf(
            Puntual, Amable, CarroLimpio, ConduccionSegura,
            BuenaConversacion, Respetuoso, AporteExacto, BuenViaje
        ).find { it.key == key }

        val driverChips = listOf(Puntual, Amable, CarroLimpio, ConduccionSegura, BuenaConversacion)
        val passengerChips = listOf(Puntual, Respetuoso, AporteExacto, BuenViaje)
    }
}
