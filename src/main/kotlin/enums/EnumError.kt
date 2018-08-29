package enums

enum class EnumError(val code: Int, val message: String) {
    OK(0, "Ok"),
    REJECT(1, "Reject"),
    NOT_FOUND(2, "Not found"),
    INNER_ERROR(500, "Internal server error")
}