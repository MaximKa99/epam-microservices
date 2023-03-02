class CustomException(
    override val message: String,
    val code: Int,
): RuntimeException() {
}