package com.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;


/**
 * Email with PDF example.
 * <br><br>
 * Email sending code adapted from http://www.java-tips.org/other-api-tips/javamail/how-to-send-an-email-with-a-file-attachment.html.
 * @author Sandip Humbe
 *
 */

@SpringBootApplication
public class Task6InMemoryPdfCreationApplication implements CommandLineRunner {

	@Autowired
	private JavaMailSender javaMailSender;
	
	
	public static void main(String[] args) {
		SpringApplication.run(Task6InMemoryPdfCreationApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {

		System.out.println("Sending email....");
		
		try {
               email();
		}
		catch(MailException me) {
			me.printStackTrace();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public  void email() {

		String smtpHost = "smtp.gmail.com";
		int smtpPort = 587;

		String sender = "sandipsh321@gmail.com";
		String recipient = "sandipsh321@yahoo.com";
		String content = "dummy content";
		String subject = "dummy message";

		Properties prop = new Properties();
		prop.put("mail.smtp.host.", smtpHost);
		prop.put("mail.smtp.port.", smtpPort);

		Session session = Session.getDefaultInstance(prop, null);

		ByteArrayOutputStream outputStream = null;

		try {

			// construct the text body part
			MimeBodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setText(content);

			// now write the PDF content to outputStream
			outputStream = new ByteArrayOutputStream();
			writePdf(outputStream);
			byte bytes[] = outputStream.toByteArray();

			// construct the pdf body part
			DataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf");
			MimeBodyPart pdfBodyPart = new MimeBodyPart();
			pdfBodyPart.setDataHandler(new DataHandler(dataSource));
			pdfBodyPart.setFileName("test.pdf");

			// construct the mime multipart
			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(textBodyPart);
			multipart.addBodyPart(pdfBodyPart);

			// create the sender and recipient address
			InternetAddress iaSender = new InternetAddress(sender);
			InternetAddress iaRecipient = new InternetAddress(recipient);

			// construct the mime message
			//MimeMessage message = new MimeMessage(session);
			MimeMessage message = new MimeMessage(session);
			
			message.setSender(iaSender);
			message.setRecipient(Message.RecipientType.TO, iaRecipient);
			message.setSubject(subject);
			message.setContent(multipart);
			

			// send off the message
	
			 javaMailSender.send(message);
			System.out.println(
					"sent from " + sender + ", to " + recipient + "; server = " + smtpHost + ", port = " + smtpPort);

		} // try

		catch (Exception ex) {

			ex.printStackTrace();
		} // catch
		finally {

			if (outputStream != null) {
				try {
					outputStream.close();
					outputStream = null;
				} catch (IOException ie) {
					ie.printStackTrace();
				}

			}//if
		}//finally

	}//email()

	/**
     * Writes the content of a PDF file (using iText API)
     * to the {@link OutputStream}.
     * @param outputStream {@link OutputStream}.
     * @throws Exception
     */
	private static void writePdf(ByteArrayOutputStream outputStream) throws DocumentException {
		// TODO Auto-generated method stub

		Document document=new Document();
		
		PdfWriter.getInstance(document, outputStream);
		
		document.open();
		
		document.addTitle("Test PDF");
        document.addSubject("Testing email PDF");
        document.addKeywords("iText, email");
        document.addAuthor("Sandip");
        document.addCreator("YBM.com");
         
        Paragraph paragraph=new Paragraph();
        paragraph.add(new Chunk("hello!"));
        document.add(paragraph);
         
        document.close();
   }//writePdf()


}// class
