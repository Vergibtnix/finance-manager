package at.vergibtnix.finance_manager.controller;

import at.vergibtnix.finance_manager.dto.RegistrationForm;
import at.vergibtnix.finance_manager.service.AppUserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AppUserService appUserService;

    public AuthController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/login")
    public String login(Authentication authentication, Model model) {
        if (isAuthenticated(authentication)) {
            return "redirect:/";
        }

        model.addAttribute("page", "login");
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Authentication authentication, Model model) {
        if (isAuthenticated(authentication)) {
            return "redirect:/";
        }

        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new RegistrationForm());
        }
        model.addAttribute("page", "register");
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registrationForm") RegistrationForm registrationForm,
                           BindingResult bindingResult,
                           Authentication authentication,
                           Model model) {
        if (isAuthenticated(authentication)) {
            return "redirect:/";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("page", "register");
            return "register";
        }

        try {
            appUserService.registerNewUser(registrationForm);
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("username", "duplicate", exception.getMessage());
            model.addAttribute("page", "register");
            return "register";
        }

        return "redirect:/login?registered";
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }
}

