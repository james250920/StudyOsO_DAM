package com.menfroyt.studyoso.utils

import org.mindrot.jbcrypt.BCrypt

fun hashPassword(password: String): String {
    return BCrypt.hashpw(password, BCrypt.gensalt(12))
}

fun checkPassword(password: String, hashed: String): Boolean {
    return BCrypt.checkpw(password, hashed)
}