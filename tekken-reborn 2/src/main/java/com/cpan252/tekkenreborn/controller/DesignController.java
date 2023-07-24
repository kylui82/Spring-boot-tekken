package com.cpan252.tekkenreborn.controller;

import java.io.IOException;
import java.util.EnumSet;

import com.cpan252.tekkenreborn.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.cpan252.tekkenreborn.model.Fighter;
import com.cpan252.tekkenreborn.model.Fighter.Anime;
import com.cpan252.tekkenreborn.model.User;
import com.cpan252.tekkenreborn.repository.FighterRepository;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

@Controller
@Slf4j
@RequestMapping("/design")
public class DesignController {

    /**
     * In java you should use the interface instead of the concrete class.
     * It helps you to switch implementations without having to change the code.
     */
    @Autowired
    private FighterRepository fighterRepository;

    @GetMapping
    public String design() {
        return "design";
    }

    @ModelAttribute
    public void animes(Model model) {
        var animes = EnumSet.allOf(Anime.class);
        model.addAttribute("animes", animes);
        log.info("animes converted to string:  {}", animes);
    }

    /**
     * 1. We have created a new Fighter object here, to be populated from the form
     * inputs
     * 2. We have to reference the Fighter object properties in the form and bind
     * them to the corresponding inputs
     * 3. We have to submit Form (execute POST request) and make sure fighter
     * details are valid
     * 
     * @return Fighter model that we will need only for request (form) submission
     */
    @ModelAttribute
    // This model attribute has a lifetime of a request
    public Fighter fighter() {
        return Fighter
                .builder()
                .build();
    }

    @PostMapping
    public String processFighterAddition(@Valid Fighter fighter, BindingResult result,
                                         @RequestParam("imgFile") MultipartFile multipartFile) throws IOException {
        if (result.hasErrors()) {
            log.info("ERROR fighter: {}", fighter);
            return "design";
        }
        log.info("Processing fighter: {}", fighter);
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        fighter.setImage(fileName);
        log.info("Processing fighter: {}", fighter);

        Fighter savedfighter = fighterRepository.save(fighter);
        String uploadDir = "src/main/resources/static/images/fighter-photos";
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        return "redirect:/fighterlist";
    }

    @PostMapping("/deleteAllFighters")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String processFightersDeletion(@AuthenticationPrincipal User user) {
        log.info("Deleting all fighters for user: {}", user.getAuthorities());
        fighterRepository.deleteAll();
        return "redirect:/design";
    }

}
