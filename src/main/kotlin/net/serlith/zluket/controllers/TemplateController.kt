package net.serlith.zluket.controllers

import net.serlith.zluket.databases.PasteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.view.RedirectView
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.jvm.Throws
import kotlin.jvm.optionals.getOrNull

@Controller
class TemplateController

@Autowired
constructor(
    private val pasteRepository: PasteRepository,
) {

    @GetMapping
    fun index() = RedirectView("/paste")

    @GetMapping("/paste")
    fun altIndex() = "index"

    @GetMapping("/paste/{uuid}")
    @Throws(ResourceNotFoundException::class)
    fun getPaste(@PathVariable uuid: UUID, model: Model): String {
        val paste = this.pasteRepository.findById(uuid).getOrNull() ?: throw ResourceNotFoundException()
        val id = paste.uuid.toString().split("-").last()
        model.addAttribute("id", id)
        model.addAttribute("paste", paste)
        model.addAttribute("date", paste.created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
        model.addAttribute("language", "plaintext")
        return "view_paste"
    }

    @GetMapping("/edit/{uuid}")
    @Throws(ResourceNotFoundException::class)
    fun getEditPaste(@PathVariable uuid: UUID, model: Model): String {
        val paste = this.pasteRepository.findById(uuid).getOrNull() ?: throw ResourceNotFoundException()
        model.addAttribute("paste", paste)
        return "edit_paste"
    }

    @RequestMapping("/404.html")
    fun catchNotFound() = "errors/404"

}