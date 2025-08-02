package com.highthon.challenge.global.config.openai

import com.highthon.challenge.domain.mission.dto.response.MissionSuggestion
import com.highthon.challenge.domain.opinion.dto.projection.MissionOpinionProjection
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicInteger

@Component
class OpenAiMissionClient(@Value("\${openai.api-keys}") private val apiKeysStr: String) {
    private val apiKeys = apiKeysStr.split(",").map { it.trim() }
    private val index = AtomicInteger(0)

    private fun getNextApiKey(): String = apiKeys[index.getAndUpdate { (it + 1) % apiKeys.size }]

    private fun buildWebClient(): WebClient {
        val key = getNextApiKey()

        return WebClient.builder()
            .baseUrl("https://api.openai.com/v1/chat/completions")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $key")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build()
    }

    fun generateMissionFromOpinions(opinions: List<MissionOpinionProjection>): Mono<List<MissionSuggestion>> {
        val prompt = buildPrompt(opinions)
        val request = mapOf(
            "model" to "gpt-4o",
            "messages" to listOf(
                mapOf("role" to "system", "content" to "너는 사용자의 사회적 불안을 줄이는 미션을 단계별로 만들어주는 전문가야."),
                mapOf("role" to "user", "content" to prompt)
            ),
            "temperature" to 0.8,
            "max_tokens" to 500,
        )

        return buildWebClient().post()
            .bodyValue(request)
            .retrieve()
            .bodyToMono(OpenAiResponse::class.java)
            .map { response ->
                val content = response.choices.firstOrNull()?.message?.content ?: return@map emptyList()

                val pattern = Regex("""\[다음 미션\d+]:\s*(.+?)\s*\(난이도:\s*(\d)\)""")
                pattern.findAll(content).mapNotNull { match ->
                    val mission = match.groupValues[1].trim()
                    val level = match.groupValues[2].toIntOrNull() ?: return@mapNotNull null
                    MissionSuggestion(mission, level)
                }.toList()
            }
    }

    private fun buildPrompt(opinions: List<MissionOpinionProjection>): String {
        val history = opinions.joinToString("\n\n") { opinion ->
            """
        [미션]: ${opinion.content}
        [미션의 난이도]: ${opinion.level}
        [느낀점]: ${opinion.impression}
        [상대 반응]: ${opinion.reaction}
        [내가 느낀 난이도]: ${opinion.difficulty}
        """.trimIndent()
        }

        return """
        다음은 사용자가 수행했던 미션과 소감입니다:

        $history

        이 데이터를 바탕으로 사용자의 사회적 불안이 점차 줄고 있다고 판단됩니다.
        다음 조건을 지켜 새로운 미션을 3개 만들어주세요:

        - 너무 부담스럽지 않고 자연스럽게 사회적 상호작용을 유도해야 합니다.
        - 내향적인 사람도 도전할 수 있도록 부드러운 톤의 행동 하나만 포함되어야 합니다.
        - 각 미션은 **한 문장**이어야 하며, 괄호 속에 난이도(1~5)를 숫자로 함께 표기해주세요. (1은 매우 쉬움, 5는 매우 어려움)
        - 출력 형식은 다음과 같아야 합니다:

        [다음 미션1]: (한 문장) (난이도: 1)
        [다음 미션2]: (한 문장) (난이도: 2)
        [다음 미션3]: (한 문장) (난이도: 3)
    """.trimIndent()
    }

    data class OpenAiResponse(val choices: List<Choice>)

    data class Choice(val message: Message)

    data class Message(
        val role: String,
        val content: String,
    )
}
