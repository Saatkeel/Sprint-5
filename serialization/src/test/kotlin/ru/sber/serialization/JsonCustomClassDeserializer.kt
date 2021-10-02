package ru.sber.serialization

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import java.io.IOException
import kotlin.test.assertEquals


class JsonCustomClassDeserializer {

    @Test
    fun `Нобходимо десериализовать данные в класс`() {
        // given
        val data = """{"client": "Иванов Иван Иванович"}"""

        val module = SimpleModule()
        module.addDeserializer(Client7::class.java, ItemDeserializer())
        val objectMapper = ObjectMapper()
            .registerModules(
                KotlinModule(),
                JavaTimeModule(),
                module
            )

        // when
        val client = objectMapper.readValue<Client7>(data)

        // then
        assertEquals("Иван", client.firstName)
        assertEquals("Иванов", client.lastName)
        assertEquals("Иванович", client.middleName)
    }
}

class ItemDeserializer : StdDeserializer<Client7?>(Client7::class.java) {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext?): Client7 {

        val node = jp.codec.readTree<JsonNode>(jp)

        val client = node.get("client").asText().split(" ")

        return Client7(
            client[1],//lastname
            client[0],//firstname
            client[2]//middlename
        )

    }

}