package ru.astrainteractive.klibs.paging.state

data class LambdaPagingState<T : Any>(
    override val pageDescriptor: T,
    override val isLastPage: Boolean,
    override val isLoading: Boolean,
    override val isFailure: Boolean,
    override val pageSizeAtLeast: Int,
    private val getNextPage: LambdaPagingState<T>.() -> T
) : PagingState<T> {
    override fun createNextPageDescriptor(): T {
        return getNextPage.invoke(this)
    }

    override fun copyPagingState(
        pageDescriptor: T,
        isLastPage: Boolean,
        isLoading: Boolean,
        isFailure: Boolean
    ): PagingState<T> {
        return this.copy(
            pageDescriptor = pageDescriptor,
            isLastPage = isLastPage,
            isLoading = isLoading,
            isFailure = isFailure
        )
    }
}
