package com.juanpablo0612.carpool.presentation.payment.manage

import com.juanpablo0612.carpool.domain.payment.model.PaymentMethod
import com.juanpablo0612.carpool.domain.payment.model.PaymentMethodType
import io.github.vinceglb.filekit.PlatformFile

sealed class PaymentMethodsAction {
    data object OnAddClick : PaymentMethodsAction()
    data object OnDismissTypePicker : PaymentMethodsAction()
    data class OnTypeSelected(val type: PaymentMethodType) : PaymentMethodsAction()
    data class OnBreBKeyChange(val key: String) : PaymentMethodsAction()
    /** Null when the user closed the gallery or camera without choosing. */
    data class OnQrImagePicked(val file: PlatformFile?) : PaymentMethodsAction()
    data object OnSave : PaymentMethodsAction()
    data object OnDismissEditor : PaymentMethodsAction()
    data class OnDeleteRequest(val method: PaymentMethod) : PaymentMethodsAction()
    data object OnConfirmDelete : PaymentMethodsAction()
    data object OnDismissDelete : PaymentMethodsAction()
    data object OnDismissError : PaymentMethodsAction()
    data object OnRetry : PaymentMethodsAction()
    data object OnBackClick : PaymentMethodsAction()
}
