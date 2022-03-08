package com.qipa.jetpackmvvm.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.lang.Exception
import java.lang.NullPointerException
import java.lang.StringBuilder
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.util.ArrayList


/**
 * @Description:主要功能:Json工具类(需要依赖Gson 2.0以上)
 * @Company:
 * @version: 1.0.0
 */
object JsonUtils {
    fun <T> json2Bean(result: String?, clazz: Class<T>?): T {
        val gson = Gson()
        return gson.fromJson(result, clazz)
    }

    /**
     * @Title: toJson
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param bean
     * @return String 返回类型
     * @throws：
     */
    fun toJson(bean: Any?): String {
        val gson = Gson()
        return gson.toJson(bean)
    }

    fun toJson(bean: Any?, type: Type?): String {
        val gson = Gson()
        return gson.toJson(bean, type)
    }

    /**
     * @Title: fromJson
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param json
     * @param type
     * @return T 返回类型
     * @throws：
     */
    fun fromJson(json: String?, type: Type?): Any {
        val gson = Gson()
        return gson.fromJson(json, type)
    }

    /**
     * @Title: fromJson
     * @Description: TODO(对象转json)
     * @param <T>
     * @param json
     * @param classOfT
     * @return T 返回类型
     * @throws：
    </T> */
    fun <T> fromJson(json: String?, classOfT: Class<T>?): T {
        val gson = Gson()
        return gson.fromJson(json, classOfT)
    }

    /**
     * Map转为JSONObject
     * @param data
     * @return
     */
    fun map2Json(data: Map<*, *>): JSONObject {
        val `object` = JSONObject()
        for ((key1, value) in data) {
            val key = key1 as String? ?: throw NullPointerException("key == null")
            try {
                `object`.put(key, wrap(value))
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        return `object`
    }

    /**
     * 集合转换为JSONArray
     * @param data
     * @return
     */
    fun collection2Json(data: Collection<*>?): JSONArray {
        val jsonArray = JSONArray()
        if (data != null) {
            for (aData in data) {
                jsonArray.put(wrap(aData))
            }
        }
        return jsonArray
    }

    /**
     * Object对象转换为JSONArray
     * @param data
     * @return
     * @throws JSONException
     */
    @Throws(JSONException::class)
    fun object2Json(data: Any): JSONArray {
        if (!data.javaClass.isArray) {
            throw JSONException("Not a primitive data: " + data.javaClass)
        }
        val length = java.lang.reflect.Array.getLength(data)
        val jsonArray = JSONArray()
        for (i in 0 until length) {
            jsonArray.put(wrap(java.lang.reflect.Array.get(data, i)))
        }
        return jsonArray
    }

    private fun wrap(o: Any?): Any? {
        if (o == null) {
            return null
        }
        if (o is JSONArray || o is JSONObject) {
            return o
        }
        try {
            if (o is Collection<*>) {
                return collection2Json(o as Collection<*>?)
            } else if (o.javaClass.isArray) {
                return object2Json(o)
            }
            if (o is Map<*, *>) {
                return map2Json(o)
            }
            if (o is Boolean || o is Byte || o is Char || o is Double || o is Float || o is Int || o is Long
                || o is Short || o is String
            ) {
                return o
            }
            if (o.javaClass.getPackage().name.startsWith("java.")) {
                return o.toString()
            }
        } catch (ignored: Exception) {
        }
        return null
    }

    /**
     * json字符串生成JSONObject对象
     * @param json
     * @return
     */
    fun string2JSONObject(json: String?): JSONObject? {
        var jsonObject: JSONObject? = null
        try {
            val jsonParser = JSONTokener(json)
            jsonObject = jsonParser.nextValue() as JSONObject
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return jsonObject
    }

    /**
     * 对象转换为Json
     * @param obj
     * @return
     */
    fun object2json(obj: Any?): String {
        val json = StringBuilder()
        if (obj == null) {
            json.append("\"\"")
        } else if (obj is String || obj is Int
            || obj is Float || obj is Boolean
            || obj is Short || obj is Double
            || obj is Long || obj is BigDecimal
            || obj is BigInteger || obj is Byte
        ) {
            json.append("\"").append(string2json(obj.toString())).append("\"")
        } else if (obj is Array<*>) {
            json.append(array2json(obj as Array<Any?>?))
        } else if (obj is List<*>) {
            json.append(list2json(obj as List<*>?))
        } else if (obj is Map<*, *>) {
            json.append(map2json(obj as Map<*, *>?))
        } else if (obj is Set<*>) {
            json.append(set2json(obj as Set<*>?))
        }
        return json.toString()
    }

    /**
     * List集合转换为Json
     * @param list
     * @return
     */
    fun list2json(list: List<*>?): String {
        val json = StringBuilder()
        json.append("[")
        if (list != null && list.size > 0) {
            for (obj in list) {
                json.append(object2json(obj))
                json.append(",")
            }
            json.setCharAt(json.length - 1, ']')
        } else {
            json.append("]")
        }
        return json.toString()
    }

    /**
     * 对象数组转换为Json
     * @param array
     * @return
     */
    fun array2json(array: Array<Any?>?): String {
        val json = StringBuilder()
        json.append("[")
        if (array != null && array.size > 0) {
            for (obj in array) {
                json.append(object2json(obj))
                json.append(",")
            }
            json.setCharAt(json.length - 1, ']')
        } else {
            json.append("]")
        }
        return json.toString()
    }

    /**
     * Map集合转换为Json
     * @param map
     * @return
     */
    fun map2json(map: Map<*, *>?): String {
        val json = StringBuilder()
        json.append("{")
        if (map != null && map.size > 0) {
            for (key in map.keys) {
                json.append(object2json(key))
                json.append(":")
                json.append(object2json(map[key]))
                json.append(",")
            }
            json.setCharAt(json.length - 1, '}')
        } else {
            json.append("}")
        }
        return json.toString()
    }

    /**
     * Set集合转为Json
     * @param set
     * @return
     */
    fun set2json(set: Set<*>?): String {
        val json = StringBuilder()
        json.append("[")
        if (set != null && set.size > 0) {
            for (obj in set) {
                json.append(object2json(obj))
                json.append(",")
            }
            json.setCharAt(json.length - 1, ']')
        } else {
            json.append("]")
        }
        return json.toString()
    }

    /**
     * 字符串转换为Json
     * @param s
     * @return
     */
    fun string2json(s: String?): String {
        if (s == null) return ""
        val sb = StringBuilder()
        for (i in 0 until s.length) {
            val ch = s[i]
            when (ch) {
                '"' -> sb.append("\\\"")
                '\\' -> sb.append("\\\\")
                '\b' -> sb.append("\\b")
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                '/' -> sb.append("\\/")
                else -> if (ch >= '\u0000' && ch <= '\u001F') {
                    val ss = Integer.toHexString(ch.toInt())
                    sb.append("\\u")
                    var k = 0
                    while (k < 4 - ss.length) {
                        sb.append('0')
                        k++
                    }
                    sb.append(ss.toUpperCase())
                } else {
                    sb.append(ch)
                }
            }
        }
        return sb.toString()
    }

    /**
     * Convert a json string to List
     * 将json转为list集合
     * @param json
     * @return
     */
    fun <T> jsonToList(json: String?): List<T> {
        val gson = Gson()
        return gson.fromJson(json, object : TypeToken<List<T>?>() {}.type)
    }

    /**
     * Convert a json string to ArrayList
     * 将json转为ArrayList
     * @param json
     * @return
     */
    fun <T> jsonToArrayList(json: String?): ArrayList<T> {
        val gson = Gson()
        return gson.fromJson(json, object : TypeToken<ArrayList<T>?>() {}.type)
    }

    /**
     * Convert a json string to Object
     * 将json转为object对象
     * @param json
     * @param clazz
     * @return
     */
    fun <T> jsonToObject(json: String?, clazz: Class<T>?): T {
        val gson = Gson()
        return gson.fromJson(json, clazz)
    }
}
