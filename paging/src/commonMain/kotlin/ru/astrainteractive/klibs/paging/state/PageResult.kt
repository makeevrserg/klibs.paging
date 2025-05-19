package ru.astrainteractive.klibs.paging.state

/**
 * Represents the result of a page loading operation.
 */
sealed interface PageResult {

    /**
     * Indicates that no page has been requested yet.
     */
    data object Pending : PageResult

    /**
     * Indicates that the last page has been reached and no further data is available.
     */
    data object LastPage : PageResult

    /**
     * Indicates that a page is currently being loaded.
     */
    data object Loading : PageResult

    /**
     * Represents a failure that occurred during page loading.
     *
     * @property throwable The exception that caused the failure.
     */
    data class Failure(val throwable: Throwable) : PageResult
}

/**
 * Returns `true` if the [PageResult] is a [PageResult.Failure], `false` otherwise.
 */
val PageResult.isFailure: Boolean
    get() = this is PageResult.Failure

/**
 * Returns `true` if the [PageResult] is [PageResult.Loading], `false` otherwise.
 */
val PageResult.isLoading: Boolean
    get() = this is PageResult.Loading

/**
 * Returns `true` if the [PageResult] is [PageResult.LastPage], `false` otherwise.
 */
val PageResult.isLastPage: Boolean
    get() = this is PageResult.LastPage

/**
 * Returns the [Throwable] if the [PageResult] is a [PageResult.Failure], or `null` otherwise.
 */
val PageResult.throwableOrNull: Throwable?
    get() = (this as? PageResult.Failure)?.throwable
