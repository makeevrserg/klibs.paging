package ru.astrainteractive.klibs.paging.state.util

import ru.astrainteractive.klibs.paging.state.model.PageResult

/**
 * Returns `true` if the [PageResult] is a [PageResult.Failure], `false` otherwise.
 */
public val PageResult.isFailure: Boolean
    get() = this is PageResult.Failure

/**
 * Returns `true` if the [PageResult] is [PageResult.Loading], `false` otherwise.
 */
public val PageResult.isLoading: Boolean
    get() = this is PageResult.Loading

/**
 * Returns `true` if the [PageResult] is [PageResult.LastPage], `false` otherwise.
 */
public val PageResult.isLastPage: Boolean
    get() = this is PageResult.LastPage

/**
 * Returns the [Throwable] if the [PageResult] is a [PageResult.Failure], or `null` otherwise.
 */
public val PageResult.throwableOrNull: Throwable?
    get() = (this as? PageResult.Failure)?.throwable
