package io.sentry.protocol

import com.nhaarman.mockitokotlin2.mock
import io.sentry.FileFromResources
import io.sentry.ILogger
import io.sentry.JsonObjectReader
import io.sentry.JsonObjectWriter
import io.sentry.JsonSerializable
import java.io.StringReader
import java.io.StringWriter
import kotlin.test.assertEquals
import org.junit.Test

class RequestSerializationTest {

    private class Fixture {
        val logger = mock<ILogger>()

        fun getSut() = Request().apply {
            url = "67369bc9-64d3-4d31-bfba-37393b145682"
            method = "8185abc3-5411-4041-a0d9-374180081044"
            queryString = "e3dc7659-f42e-413c-a07c-52b24bf9d60d"
            data = mapOf(
                "d9d709db-b666-40cc-bcbb-093bb12aad26" to "1631d0e6-96b7-4632-85f8-ef69e8bcfb16"
            )
            cookies = "d84f4cfc-5310-4818-ad4f-3f8d22ceaca8"
            headers = mapOf(
                "c4991f66-9af9-4914-ac5e-e4854a5a4822" to "37714d22-25a7-469b-b762-289b456fbec3"
            )
            envs = mapOf(
                "6d569c89-5d5e-40e0-a4fc-109b20a53778" to "ccadf763-44e4-475c-830c-de6ba0dbd202"
            )
            others = mapOf(
                "669ff1c1-517b-46dc-a889-131555364a56" to "89043294-f6e1-4e2e-b152-1fdf9b1102fc"
            )
        }
    }
    private val fixture = Fixture()

    @Test
    fun serialize() {
        val expected = sanitizedFile("gson/request.json")
        val actual = serialize(fixture.getSut())
        assertEquals(expected, actual)
    }

    @Test
    fun deserialize() {
        val expectedJson = sanitizedFile("gson/request.json")
        val actual = deserialize(expectedJson)
        val actualJson = serialize(actual)
        assertEquals(expectedJson, actualJson)
    }

    // Helper

    private fun sanitizedFile(path: String): String {
        return FileFromResources.invoke(path)
            .replace(Regex("[\n\r]"), "")
            .replace(" ", "")
    }

    private fun serialize(jsonSerializable: JsonSerializable): String {
        val wrt = StringWriter()
        val jsonWrt = JsonObjectWriter(wrt)
        jsonSerializable.serialize(jsonWrt, fixture.logger)
        return wrt.toString()
    }

    private fun deserialize(json: String): Request {
        val reader = JsonObjectReader(StringReader(json))
        return Request.Deserializer().deserialize(reader, fixture.logger)
    }
}
