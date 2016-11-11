import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by IPotapchuk on 6/22/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:test-mail-applicationContext.xml"})
@PropertySource("classpath:test-mail.properties")
public class MailTest {

    @Autowired
    private MailSender sender;

    @Autowired
    private SimpleMailMessage msg;

    @Value("${mail.port}")
    private String smtpPort;
    @Value("${mail.message.from}")
    private String from;

    private SimpleSmtpServer server;

    @Before
    public void setUp() {
        server = SimpleSmtpServer.start(Integer.parseInt(smtpPort));
    }

    @After
    public void cleanUp() {
        server.stop();
    }

    @Test
    public void sendSimpleEmail() throws ParseException {
        String to = "test@test.com";
        Date now = new Date();
        String subj = "Test subject";
        String text = "Hello! It's a test.";
        msg.setTo(to);
        msg.setSentDate(now);
        msg.setSubject(subj);
        msg.setText(text);
        sender.send(msg);
        assertTrue(server.getReceivedEmailSize() == 1);
        SmtpMessage received = (SmtpMessage) server.getReceivedEmail().next();
        assertNotNull(received);
        assertTrue(received.getClass().equals(SmtpMessage.class));
        assertEquals("From", from, received.getHeaderValue("From"));
        assertEquals("To", to, received.getHeaderValue("To"));
        assertEquals("Subject", subj, received.getHeaderValue("Subject"));
        assertEquals("Text", text, received.getBody());
        assertEquals("ReplyTo", "noreply", received.getHeaderValue("Reply-To"));
    }

}
