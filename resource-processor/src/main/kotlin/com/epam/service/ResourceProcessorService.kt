package com.epam.service

import com.epam.dto.SongView
import com.epam.model.ResourceType
import com.epam.service.adapter.AdapterSQS
import com.epam.service.adapter.ResourceServiceAdapter
import com.epam.service.adapter.SongServiceAdapter
import org.apache.tika.parser.AutoDetectParser
import org.apache.tika.parser.ParseContext
import org.apache.tika.sax.BodyContentHandler
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream


@Service
@EnableScheduling
class ResourceProcessorService(
    private val resourceServiceAdapter: ResourceServiceAdapter,
    private val adapterSQS: AdapterSQS,
    private val songServiceAdapter: SongServiceAdapter,
) {

    @Scheduled(fixedDelay = 5000)
    fun processAudioQueue() {
        adapterSQS.getMessages(ResourceType.Audio.queue).forEach {
            val byteArray = resourceServiceAdapter.getResource(it.body().toLong()).body?.inputStream

            val handler = BodyContentHandler()
            val metadata = org.apache.tika.metadata.Metadata()
            val parser = AutoDetectParser()
            val parseContext = ParseContext()
            parser.parse(byteArray, handler, metadata, parseContext)

            val length = metadata.get("xmpDM:duration")
            val name = metadata.get("title")

            songServiceAdapter.saveSong(
                SongView(
                    name = name,
                    length = length,
                    resourceId = it.body().toLong()
                )
            )

            adapterSQS.deleteMessage(ResourceType.Audio.queue, it.receiptHandle())
        }
    }
}