package ru.astrainteractive.klibs.paging.state.model

/**
 * Represents the result of a page loading operation.
 */
public sealed interface PageResult {

    /**
     * Indicates that no page has been requested yet.
     */
    public data object Pending : PageResult

    /**
     * Indicates that the last page has been reached and no further data is available.
     */
    public data object LastPage : PageResult

    /**
     * Indicates that a page is currently being loaded.
     */
    public data object Loading : PageResult

    /**
     * Represents a failure that occurred during page loading.
     *
     * @property throwable The exception that caused the failure.
     */
    public data class Failure(public val throwable: Throwable) : PageResult
}
