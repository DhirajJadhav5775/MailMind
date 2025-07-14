package com.MailAI.MailMind.MailController;

import com.MailAI.MailMind.Service.EmailGenService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
//@AllArgsConstructor
@CrossOrigin(origins = "*")
public class MainController
{
//    @Autowired
    private final EmailGenService emailGenService;
    public MainController(EmailGenService emailGenService) {
        this.emailGenService = emailGenService;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest)
    {
        String response = emailGenService.generateEmailReply(emailRequest);
        return ResponseEntity.ok(response);
    }
}
