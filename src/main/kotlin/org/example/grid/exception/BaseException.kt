package org.example.grid.exception

import java.lang.RuntimeException

abstract class BaseException(override val message: String) : RuntimeException() {
}
