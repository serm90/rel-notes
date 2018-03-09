package com.pfsoft.rnotes.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.pfsoft.rnotes.beans.Version;
import com.pfsoft.rnotes.services.IVersionService;
import com.pfsoft.rnotes.viewbeans.ChangesFormBean;

@Controller
public class ChangeLogController {
    private final IVersionService versionService;

    @Autowired
    public ChangeLogController(IVersionService repository) {
        this.versionService = repository;
    }


    @GetMapping("/changes")
    public String changes(Model model ){
        ChangesFormBean cForm = new ChangesFormBean();
        model.addAttribute("form", cForm);
        return "changes";
    }


    @PostMapping("/changes")
    public String changes(@ModelAttribute("form") ChangesFormBean form){
        form.setChanges(versionService.getChanges(form.getFrom(), form.getTo()));
        return "changes";
    }

    @ModelAttribute( "versions")
    public List<Version> getVersions(){
        return versionService.getVersions();
    }
}
