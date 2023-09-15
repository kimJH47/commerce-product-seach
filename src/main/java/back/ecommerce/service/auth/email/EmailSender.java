package back.ecommerce.service.auth.email;

public interface EmailSender {
	void send(SignUpEmailMessage message);
}
