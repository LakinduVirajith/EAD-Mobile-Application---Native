package com.example.ead_mobile_application__native.utils

import android.util.Log
import com.example.ead_mobile_application__native.BuildConfig
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureException
import java.util.Date

object JwtUtils {
    // REPLACE WITH YOUR SECRET KEY
    private const val SECRET_KEY = BuildConfig.SECRET_KEY

    // FUNCTION TO VALIDATE JWT TOKEN
    fun isValidToken(token: String?): Boolean {
        return try {
            Log.d("Claims", "Working")
            // DECODE THE JWT WITHOUT VERIFICATION
            val claims: Claims = Jwts.parser()
                .setSigningKey(SECRET_KEY.toByteArray()) // SET SIGNING KEY
                .parseClaimsJws(token)
                .body

            // CHECK IF THE TOKEN HAS EXPIRED
            val expiration: Date? = claims.expiration
            expiration != null && !expiration.before(Date())  // TOKEN IS VALID IF NOT EXPIRED
        } catch (e: SignatureException) {
            // TOKEN SIGNATURE IS INVALID
            false
        } catch (e: Exception) {
            // ANY OTHER EXCEPTIONS (E.G., MALFORMED TOKEN)
            false
        }
    }
}