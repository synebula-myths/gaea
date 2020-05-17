package com.synebula.gaea.extension

fun String.firstCharLowerCase(): String {
    if (Character.isLowerCase(this.elementAt(0)))
        return this
    else
        return StringBuilder().append(Character.toLowerCase(this.elementAt(0)))
                .append(this.substring(1)).toString()
}