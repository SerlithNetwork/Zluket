package net.serlith.zluket.controllers.rest

import net.serlith.zluket.databases.PasteRepository
import net.serlith.zluket.databases.types.PasteModel
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.InputStreamResource
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

@RestController
@RequestMapping("/paste")
class RootController

@Autowired
constructor(
    private val pasteRepository: PasteRepository,
) {

    @Value("\${zluket.api.content.max_length:100000}")
    private var contentMaxLength: Int = 0

    @PostMapping(
        consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE],
    )
    fun postIndex(@RequestParam(name = "content") content: String): RedirectView {
        if (content.isBlank()) return RedirectView("/paste")
        var contentVar = if (content.length > contentMaxLength) content.substring(0, contentMaxLength) else content
        val paste = this.pasteRepository.save(PasteModel(contentVar))
        return RedirectView("/paste/${paste.uuid}")
    }

    @GetMapping("/raw/{uuid}")
    @Throws(ResourceNotFoundException::class)
    fun getRawPaste(@PathVariable uuid: UUID) : ResponseEntity<String> {
        val paste = this.pasteRepository.findById(uuid).getOrNull() ?: throw ResourceNotFoundException()
        return ResponseEntity.ok(paste.content)
    }

    // FIXME: Doesn't actually dupe/edit the paste, it creates a new one from a form
    @PostMapping("/edit/{uuid}")
    @Throws(ResourceNotFoundException::class)
    fun postEditPaste(@PathVariable uuid: UUID, @RequestBody form: Map<String, String>): RedirectView {
        val paste = this.pasteRepository.findById(uuid).getOrNull() ?: throw ResourceNotFoundException()
        var content = form["content"] ?: return RedirectView("/paste")
        content = if (content.length > contentMaxLength) content.substring(0, contentMaxLength) else content
        val new = this.pasteRepository.save(PasteModel(content))
        return RedirectView("/paste/${new.uuid}")
    }

    @GetMapping("/favicon.ico")
    fun getFavicon(): ResponseEntity<InputStreamResource> {
        val icon = ClassPathResource("public/logo/logo.png")
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(InputStreamResource(icon.inputStream))
    }

    @GetMapping("/error")
    fun getError() = RedirectView("/paste")

}