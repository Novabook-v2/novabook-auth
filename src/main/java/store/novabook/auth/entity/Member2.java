package store.novabook.auth.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Member2 {
	private int id;

	private String username;
	private String password;

	private String role;
}
