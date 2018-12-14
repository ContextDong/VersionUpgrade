/*
 * Copyright (c) 2018. Xi'an iRain IOT Technology service CO., Ltd (ShenZhen). All Rights Reserved.
 */

package com.cherry.version.upgrade

import java.util.regex.Pattern

class Version(
        /** 主版本 */
        val major: Int,
        /** 副版本 */
        val minor: Int,
        /** 修复版本 */
        val revision: Int,
        /** 构建版本 */
        val build: Int,
        val name: String?) {

    fun isLowerThan(version: Version?): Boolean {
        if (version == null) {
            return false
        }
        if (major < version.major) {
            return true
        }
        if (major == version.major) {
            if (minor < version.minor) {
                return true
            }
            if (minor == version.minor) {
                if (revision < version.revision) {
                    return true
                }
                if (revision == version.revision) {
                    return build < version.build
                }
            }
        }
        return false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }

        val version = other as Version?

        if (major != version!!.major) {
            return false
        }
        if (minor != version.minor) {
            return false
        }
        if (revision != version.revision) {
            return false
        }
        if (build != version.build) {
            return false
        }
        return if (name != null) name == version.name else version.name == null
    }

    override fun toString(): String {
        return "Version(major=$major, minor=$minor, revision=$revision, onStartSetupNotification=$build, name=$name)"
    }


    companion object {

        //                                  major       minor      revision     onStartSetupNotification
        private const val VERSION_REGEX = "(\\d+)(?:\\.(\\d+)(?:\\.(\\d+)(?:\\.(\\d+))?)?)?.*"
        private val sVersionPatter = Pattern.compile(VERSION_REGEX)

        fun parse(version: String?): Version? {
            if (version == null) {
                return null
            }
            val matcher = sVersionPatter.matcher(version)
            if (matcher.matches()) {
                var major = 0
                var minor = 0
                var revision = 0
                var build = 0

                var s: String? = matcher.group(1)
                if (s != null) {
                    major = Integer.parseInt(s)
                }

                s = matcher.group(2)
                if (s != null) {
                    minor = Integer.parseInt(s)
                }

                s = matcher.group(3)
                if (s != null) {
                    revision = Integer.parseInt(s)
                }

                s = matcher.group(4)
                if (s != null) {
                    build = Integer.parseInt(s)
                }
                return Version(major, minor, revision, build, version)
            } else {
                return null
            }
        }
    }
}

