package movie.matcher.ru.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "main";
    }

    @GetMapping("/main")
    public String legacyAdminPage() {
        return "redirect:/admin";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

}
