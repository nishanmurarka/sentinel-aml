package com.meridiantrust.sentinel.controller;

import com.meridiantrust.sentinel.service.SarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class SarController {

    private final SarService sarService;

    public SarController(SarService sarService) {
        this.sarService = sarService;
    }

    @GetMapping("/alerts/{id}/sar")
    public String viewSar(@PathVariable("id") String id, Model model) {
        String draft = sarService.generateSarDraft(id);
        model.addAttribute("alertId", id);
        model.addAttribute("sarDraft", draft);
        return "sar";
    }
}
