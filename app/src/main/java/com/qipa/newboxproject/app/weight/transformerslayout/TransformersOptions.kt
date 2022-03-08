package com.qipa.newboxproject.app.weight.transformerslayout

import androidx.annotation.ColorInt
import androidx.annotation.Px

class TransformersOptions {
    var spanCount = 0
    var lines = 0
    var scrollBarWidth = 0
    var scrollBarHeight = 0
    var scrollBarTopMargin = 0
    var scrollBarBottomMargin = 0
    var scrollBarTrackColor = 0
    var scrollBarThumbColor = 0
    var scrollBarRadius = 0f
    var scrollBarThumbFixedMode = false
    var scrollBarThumbWidth = 0
    var pagingMode = false

    private fun TransformersOptions(builder: Builder) {
        spanCount = builder.spanCount
        lines = builder.lines
        scrollBarWidth = builder.scrollBarWidth
        scrollBarHeight = builder.scrollBarHeight
        scrollBarTopMargin = builder.scrollBarTopMargin
        scrollBarBottomMargin = builder.scrollBarBottomMargin
        scrollBarTrackColor = builder.scrollBarTrackColor
        scrollBarThumbColor = builder.scrollBarThumbColor
        scrollBarRadius = builder.scrollBarRadius
        scrollBarThumbFixedMode = builder.scrollBarThumbFixedMode
        scrollBarThumbWidth = builder.scrollBarThumbWidth
        pagingMode = builder.pagingMode
    }

    class Builder {
        var spanCount = 0
        var lines = 0
        var scrollBarWidth = 0
        var scrollBarHeight = 0
        var scrollBarTopMargin = 0
        var scrollBarBottomMargin = 0
        var scrollBarTrackColor = 0
        var scrollBarThumbColor = 0
        var scrollBarRadius = -1f
        var scrollBarThumbFixedMode = false
        var scrollBarThumbWidth = 0
        var pagingMode = false
        fun scrollBarThumbFixedMode(fixed: Boolean): Builder {
            scrollBarThumbFixedMode = fixed
            return this
        }

        fun scrollBarThumbWidth(@Px width: Int): Builder {
            scrollBarThumbWidth = width
            return this
        }

        fun pagingMode(paging: Boolean): Builder {
            pagingMode = paging
            return this
        }

        fun scrollBarRadius(@Px radius: Float): Builder {
            scrollBarRadius = radius
            return this
        }

        fun scrollBarTrackColor(@ColorInt color: Int): Builder {
            scrollBarTrackColor = color
            return this
        }

        fun scrollBarThumbColor(@ColorInt color: Int): Builder {
            scrollBarThumbColor = color
            return this
        }

        fun scrollBarTopMargin(@Px topMargin: Int): Builder {
            scrollBarTopMargin = topMargin
            return this
        }

        fun scrollBarBottomMargin(@Px bottomMargin: Int): Builder {
            scrollBarBottomMargin = bottomMargin
            return this
        }

        fun scrollBarWidth(@Px width: Int): Builder {
            scrollBarWidth = width
            return this
        }

        fun scrollBarHeight(@Px height: Int): Builder {
            scrollBarHeight = height
            return this
        }

        fun spanCount(spanCount: Int): Builder {
            this.spanCount = spanCount
            return this
        }

        fun lines(lines: Int): Builder {
            this.lines = lines
            return this
        }

        fun build(): TransformersOptions {
            return TransformersOptions()
        }
    }
}