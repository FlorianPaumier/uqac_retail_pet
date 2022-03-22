package com.uqac.pet_retail.ui.register

import com.uqac.pet_retail.ui.login.LoggedInUserView

/**
 * Authentication result : success (user details) or error message.
 */
data class RegisterResult(
        val success: RegisteredInUserView? = null,
        val error: Int? = null
)