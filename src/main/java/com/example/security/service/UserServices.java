package com.example.security.service;

import com.example.model.Users;
import com.example.repository.UserDAO;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
@Service
public class UserServices {
  @Autowired
  private UserDAO userDAO;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JavaMailSender javaMailSender;

  public void register(Users user ,String siteURL) throws UnsupportedEncodingException, MessagingException {
    String randomCode = RandomString.make(64);
    user.setVerificationCode(randomCode);
    user.setEnabled(false);
    userDAO.save(user);
    sendVerificationEmail(user, siteURL);
  }

  public void sendVerificationEmail(Users users, String siteURL) throws MessagingException, UnsupportedEncodingException{
    String toAddress = users.getEmail();
    String fromAddress = "nhut23112000@gmail.com";
    String senderName = "DinhNhut2k Mobile Company";
    String subject = "Please verify your registration";
    String content = "Dear [[name]],<br>"
        + "Please click the link below to verify your registration:<br>"
        + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
        + "Thank you, have a good day<br>"
        + "DinhNhut2K Mobile Shop.";

    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom(fromAddress, senderName);
    helper.setTo(toAddress);
    helper.setSubject(subject);

    content = content.replace("[[name]]", users.getFullName());
    String verifyURL = siteURL + "/verify?code=" + users.getVerificationCode();

    content = content.replace("[[URL]]", verifyURL);

    helper.setText(content, true);

    javaMailSender.send(message);
  }
  public boolean verify(String verificationCode) {
    Users users = userDAO.findByVerificationCode(verificationCode);

    if (users == null || users.getEnabled()) {
      return false;
    } else {
      users.setVerificationCode(null);
      users.setEnabled(true);
      userDAO.save(users);

      return true;
    }


  }

  public void updateResetPasswordToken(String token, String email) throws UsernameNotFoundException {
    Users users= userDAO.findByEmail(email);
    if (users != null) {
      users.setResetPasswordToken(token);
      userDAO.save(users);
    } else {
      throw new UsernameNotFoundException("Could not find any user with the email " + email);
    }
  }

  public Users getByResetPasswordToken(String token) {
    return userDAO.findByResetPasswordToken(token);
  }

  public void updatePassword(Users users, String newPassword) {

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String encodedPassword = passwordEncoder.encode(newPassword);
    users.setPassword(encodedPassword);
    users.setResetPasswordToken(null);
    userDAO.save(users);
  }
  public void changePassword(Long userId,String newPassword){
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    String encodedPassword = passwordEncoder.encode(newPassword);
    Users users = userDAO.findById(userId).get();
    users.setPassword(encodedPassword);
    userDAO.save(users);
  }

  public void sendEmail(String recipientEmail, String link)
      throws MessagingException, UnsupportedEncodingException {
    MimeMessage message = javaMailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom("nhut23112000@gmail.com", "MobileShop Support");
    helper.setTo(recipientEmail);

    String subject = "Here's the link to reset your password";

    String content = "<p>Hello,</p>"
        + "<p>You have requested to reset your password.</p>"
        + "<p>Click the link below to change your password:</p>"
        + "<p><a href=\"" + link + "\">Change my password</a></p>"
        + "<br>"
        + "<p>Ignore this email if you do remember your password, "
        + "or you have not made the request.</p>";

    helper.setSubject(subject);

    helper.setText(content, true);

    javaMailSender.send(message);
  }
}
