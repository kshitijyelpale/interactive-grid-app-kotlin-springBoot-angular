package com.example.todoapp.exception

class ServiceException(message: String) : BaseException(message) {

}

class NoSuchElementFoundException(message: String) : BaseException(message) {
}
