package back.ecommerce.config.jpa;

import java.util.Locale;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class CamelCaseToSnakeAndUpperCaseStrategy extends CamelCaseToUnderscoresNamingStrategy {

	@Override
	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return new Identifier(name.getText().toUpperCase(Locale.ROOT), false);
	}
}
