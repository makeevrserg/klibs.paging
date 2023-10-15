package ru.astrainteractive.klibs.paging.state

/**
 * Core paging state
 * [T] is the type of page - can be int; string whatever
 * @see IntPagingState
 * @see LambdaPagingState
 */
interface PagingState<T : Any> {

    val pageDescriptor: T

    val pageSizeAtLeast: Int

    val isLastPage: Boolean

    val isLoading: Boolean

    val isFailure: Boolean

    fun createNextPageDescriptor(): T

    fun copyPagingState(
        pageDescriptor: T = this.pageDescriptor,
        isLastPage: Boolean = this.isLastPage,
        isLoading: Boolean = this.isLoading,
        isFailure: Boolean = this.isFailure
    ): PagingState<T>
}
