package com.example.grid.exception

import org.example.grid.exception.BaseException

class ServiceException(message: String) : BaseException(message) {

}

class NoSuchElementFoundException(message: String) : BaseException(message) {
}
