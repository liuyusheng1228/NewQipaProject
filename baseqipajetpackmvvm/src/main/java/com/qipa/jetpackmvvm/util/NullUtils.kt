package com.qipa.jetpackmvvm.util

import android.text.TextUtils


/**
 * @author sam
 * @version 1.0
 * @date 2018/1/1
 */
class NullUtils {
    /**
     * java反射机制判断对象所有属性是否全部为空
     * @param obj
     * @return
     */
    private fun checkObjFieldIsNotNull(obj: Any): Boolean {
        try {
            for (f in obj.javaClass.declaredFields) {
                f.isAccessible = true
                if (f[obj] != null) {
                    return true
                }
            }
        } catch (e: IllegalAccessException) {
        }
        return false
    }

    companion object {
        /**
         * 判断字符串是否为null
         * @param str
         * @return
         */
        fun isEmptyString(str: CharSequence): Boolean {
            return if (TextUtils.isEmpty(str) || "null" == str) { //后台可能会返回“null”
                true
            } else {
                false
            }
        }

        /**
         * 判断list是否为null
         * @param list
         * @return
         */
        fun isEmptyList(list: List<*>?): Boolean {
            return if (list == null || list.size == 0) {
                true
            } else {
                false
            }
        }

        /**
         * 判断对象是否为null
         * @param object
         * @return
         */
        fun isEmptyObject(`object`: Any?): Boolean {
            return if (`object` == null) {
                true
            } else {
                false
            }
        }

        /**
         * 判断对象或对象数组中每一个对象是否为空: 对象为null，字符序列长度为0，集合类、Map为empty
         * @param obj
         * @return
         */
        fun isNull(obj: Any?): Boolean {
            if (obj == null) {
                return true
            }
            if (obj is CharSequence) {
                if (TextUtils.isEmpty(obj as CharSequence?) || "null" == obj) { //后台可能会返回“null”
                    return true
                }
            }
            if (obj is Collection<*>) {
                return obj.isEmpty()
            }
            if (obj is Map<*, *>) {
                return obj.isEmpty()
            }
            if (obj is Array<*>) {
                val `object` = obj
                if (`object`.size == 0) {
                    return true
                }
                var empty = true
                for (i in `object`.indices) {
                    if (!isNull(`object`[i])) {
                        empty = false
                        break
                    }
                }
                return empty
            }
            return false
        }
    }
}
