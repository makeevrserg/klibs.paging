package ru.astrainteractive.klibs.paging.state

data class IntPagingState(
    override var pageDescriptor: Int,
    override var isLastPage: Boolean = false,
    override val isLoading: Boolean = false,
    override val isFailure: Boolean = false,
    override val pageSizeAtLeast: Int = 10
) : PagingState<Int> {
    override fun createNextPageDescriptor(): Int {
        return pageDescriptor + 1
    }

    override fun copyPagingState(
        pageDescriptor: Int,
        isLastPage: Boolean,
        isLoading: Boolean,
        isFailure: Boolean
    ): PagingState<Int> {
        return this.copy(
            pageDescriptor = pageDescriptor,
            isLastPage = isLastPage,
            isLoading = isLoading,
            isFailure = isFailure
        )
    }
}
