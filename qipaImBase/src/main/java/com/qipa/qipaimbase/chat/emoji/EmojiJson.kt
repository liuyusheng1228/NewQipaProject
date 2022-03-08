package com.qipa.qipaimbase.chat.emoji

class EmojiJson {
    private var emojis: List<EmojiBean?>? = null

    fun getEmojis(): List<EmojiBean?>? {
        return emojis
    }

    fun setEmojis(emojis: List<EmojiBean?>?) {
        this.emojis = emojis
    }

    class EmojiBean {
        /**
         * credentialName : [龇牙]
         * resId : R.id.emoji_ciya
         */
        var credentialName: String? = null
        var resId: String? = null
    }
}