package com.epam.service

import com.epam.api.ResourceApi
import com.epam.api.SongApi
import com.epam.dto.SongView
import com.epam.model.ResourceType
import com.epam.service.adapter.AdapterSQS
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.sax.BodyContentHandler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


@Service
@EnableScheduling
class ResourceProcessorService(
    private val resourceApi: ResourceApi,
    private val adapterSQS: AdapterSQS,
    private val songApi: SongApi,
) {

    @Scheduled(fixedDelay = 5000)
    fun processAudioQueue() {
        adapterSQS.getMessages(ResourceType.Audio.queueIn).forEach {
            val byteArray = resourceApi.getResource(it.body().toLong()).body?.inputStream

            val handler = BodyContentHandler()
            val metadata = org.apache.tika.metadata.Metadata()
            val parser = AutoDetectParser()
            val parseContext = ParseContext()
            parser.parse(byteArray, handler, metadata, parseContext)

            val length = metadata.get("xmpDM:duration")
            val name = metadata.get("title")

            val songView = SongView()
            songView.name = name
            songView.length = length
            songView.resourceId = it.body().toLong()
            songApi.saveSong(songView)

            adapterSQS.deleteMessage(ResourceType.Audio.queueIn, it.receiptHandle())
            adapterSQS.putMessage(it.body(), ResourceType.Audio.queueOut)
        }
    }
}