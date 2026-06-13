package com.example.janagroandroid.data.remote

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import okio.Buffer

object FlexibleStringListAdapter {

    @FromJson
    fun fromJson(reader: JsonReader): List<String> {
        return when (reader.peek()) {
            JsonReader.Token.BEGIN_ARRAY -> readArray(reader)
            JsonReader.Token.STRING -> parseString(reader.nextString())
            JsonReader.Token.NULL -> {
                reader.nextNull<Unit>()
                emptyList()
            }
            else -> {
                reader.skipValue()
                emptyList()
            }
        }
    }

    @ToJson
    fun toJson(writer: JsonWriter, value: List<String>?) {
        writer.beginArray()
        value?.forEach { writer.value(it) }
        writer.endArray()
    }

    private fun readArray(reader: JsonReader): List<String> {
        val list = mutableListOf<String>()
        reader.beginArray()
        while (reader.hasNext()) {
            if (reader.peek() == JsonReader.Token.NULL) {
                reader.nextNull<Unit>()
            } else {
                list.add(reader.nextString())
            }
        }
        reader.endArray()
        return list
    }

    private fun parseString(raw: String): List<String> {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return emptyList()

        if (trimmed.startsWith("[")) {
            return try {
                readArray(JsonReader.of(Buffer().writeUtf8(trimmed)))
            } catch (e: Exception) {
                listOf(trimmed)
            }
        }

        // String tunggal biasa
        return listOf(trimmed)
    }
}
