package com.pfsoft.rnotes.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NotesController {

    @RequestMapping("/notes")
    public String notes(@RequestParam(value="fVersion", required=false, defaultValue="HEAD") String fVersion,
                        @RequestParam(value="tVersion", required=false, defaultValue="HEAD") String tVersion, Model model) {
        List<String>  notes = new ArrayList<>();
        model.addAttribute("notes", notes);
        return "index";
    }

}
