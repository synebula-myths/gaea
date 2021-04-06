package com.synebula.gaea.app.struct.exception

import com.auth0.jwt.exceptions.TokenExpiredException

class TokenCloseExpireException(msg: String, var payload: String) : TokenExpiredException(msg)
