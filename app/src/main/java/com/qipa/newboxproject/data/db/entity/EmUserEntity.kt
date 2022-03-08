package com.qipa.newboxproject.data.db.entity

import com.hyphenate.easeui.domain.EaseUser

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index


@Entity(
    tableName = "em_users",
    primaryKeys = ["username"],
    indices = [Index(value = ["username"], unique = true)]
)
class EmUserEntity : EaseUser {
    constructor() : super() {}

    @Ignore
    constructor(username: String) : super(username) {
    }

    companion object {
        @Ignore
        fun parseList(users: List<EaseUser>?): List<EmUserEntity> {
            val entities: MutableList<EmUserEntity> = ArrayList()
            if (users == null || users.isEmpty()) {
                return entities
            }
            var entity: EmUserEntity
            for (user in users) {
                entity = parseParent(user)
                entities.add(entity)
            }
            return entities
        }

        @Ignore
        fun parseParent(user: EaseUser): EmUserEntity {
            val entity = EmUserEntity()
            entity.username = user.username
            entity.nickname = user.nickname
            entity.avatar = user.avatar
            entity.initialLetter = user.initialLetter
            entity.contact = user.contact
            entity.email = user.email
            entity.gender = user.gender
            entity.birth = user.birth
            entity.phone = user.phone
            entity.sign = user.sign
            entity.ext = user.ext
            return entity
        }
    }
}
