package ru.astrainteractive.klibs.paging.state

sealed interface PageResult {
    data object Pending : PageResult
    data object LastPage : PageResult
    data object Loading : PageResult
    data class Failure(val throwable: Throwable) : PageResult
}

val PageResult.isFailure: Boolean
    get() = this is PageResult.Failure

val PageResult.isLoading: Boolean
    get() = this is PageResult.Loading

val PageResult.isLastPage: Boolean
    get() = this is PageResult.LastPage

val PageResult.throwableOrNull: Throwable?
    get() = (this as? PageResult.Failure)?.throwable
