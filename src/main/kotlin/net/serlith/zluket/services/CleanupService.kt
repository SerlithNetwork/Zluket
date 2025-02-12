package net.serlith.zluket.services

import net.serlith.zluket.databases.PasteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class CleanupService

@Autowired constructor(
    private val pasteRepository: PasteRepository,
) {

    @Value("\${zluket.cleanup.weeks:1}")
    private var cleanupWeeks: Long = 1L

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    fun performCleanup() {
        this.pasteRepository.findAllByCreatedBefore(LocalDateTime.now().minusWeeks(this.cleanupWeeks))
    }

}