package com.highthon.challenge.global.exception

import java.lang.RuntimeException

class CustomException(val error: CustomError) : RuntimeException()
