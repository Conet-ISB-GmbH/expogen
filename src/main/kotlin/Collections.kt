fun <T> Array<T>.second(): T {
    require(size >= 2)
    return get(1)
}
