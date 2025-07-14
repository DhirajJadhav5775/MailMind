package com.MailAI.MailMind.MailController;

import lombok.Data;

@Data
public class EmailRequest
{
    private String emailContent;
    private String tone;
}
