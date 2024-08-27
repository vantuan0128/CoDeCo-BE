package com.prj.furni_shop.base.mail;

import com.prj.furni_shop.utils.EncryptionUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private EncryptionUtil encryptionUtil;

    public void sendOtpMail(String email, String otp, EmailType emailType) throws MessagingException {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setTo(email);

        if (emailType == EmailType.VERIFY_ACCOUNT) {
            String params = String.format("email=%s&otp=%s", email, otp);
            String encodedParams = encryptionUtil.encodeBase64(params);
            String verifyLink = String.format("http://localhost:3030/verify?params=%s", encodedParams);

            mimeMessageHelper.setSubject("Verify your account");

            mimeMessageHelper.setText("""
                <div>
                    <p>Mã OTP của bạn là: %s</p>
                    <p>Nhấn vào <a href="%s">đây</a> để xác minh tài khoản của bạn.</p>
                </div>
                """.formatted(otp, verifyLink), true);
        } else {
            mimeMessageHelper.setSubject("Reset your password");

            mimeMessageHelper.setText("""
                <div>
                    <p>Mã OTP của bạn là: %s</p>
                    <p>Sử dụng mã OTP này để khôi phục lại mật khẩu của bạn</p>
                </div>
                """.formatted(otp), true);
        }
        emailSender.send(mimeMessage);
    }
}
