package com.qipa.jetpackmvvm.pay.alipay

import android.text.TextUtils

class PayResult {
    /**
     * @return the resultStatus
     */
    var resultStatus: String? = null
        private set

    /**
     * @return the result
     */
    var result: String? = null
        private set

    /**
     * @return the memo
     */
    var memo: String? = null
        private set

    constructor(rawResult: Map<String?, String?>?) {
        if (rawResult == null) {
            return
        }
        for (key in rawResult.keys) {
            if (TextUtils.equals(key, "resultStatus")) {
                resultStatus = rawResult[key]
            } else if (TextUtils.equals(key, "result")) {
                result = rawResult[key]
            } else if (TextUtils.equals(key, "memo")) {
                memo = rawResult[key]
            }
        }
    }

    /**
     * Version 1 pay result
     *
     * @param rawResult pay result
     */
    constructor(rawResult: String) {
        if (TextUtils.isEmpty(rawResult)) {
            return
        }
        val resultParams = rawResult.split(";".toRegex()).toTypedArray()
        for (resultParam in resultParams) {
            if (resultParam.startsWith("resultStatus")) {
                resultStatus = gatValue(resultParam, "resultStatus")
            }
            if (resultParam.startsWith("result")) {
                result = gatValue(resultParam, "result")
            }
            if (resultParam.startsWith("memo")) {
                memo = gatValue(resultParam, "memo")
            }
        }
    }

    private fun gatValue(content: String, key: String): String {
        val prefix = "$key={"
        return content.substring(content.indexOf(prefix) + prefix.length, content.lastIndexOf("}"))
    }

    override fun toString(): String {
        return "resultStatus={$resultStatus};memo={$memo};result={$result}"
    }
}
