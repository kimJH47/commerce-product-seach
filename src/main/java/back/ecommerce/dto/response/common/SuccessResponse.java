package back.ecommerce.dto.response.common;

import lombok.Getter;

@Getter
public class SuccessResponse<T> extends Response{
	private final T entity;
	public SuccessResponse(String message, T entity) {
		super(message);
		this.entity = entity;
	}
}
