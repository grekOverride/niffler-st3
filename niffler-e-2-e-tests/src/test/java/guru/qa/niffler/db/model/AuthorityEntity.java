package guru.qa.niffler.db.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthorityEntity {

    private UUID id;
    private Authority authority;
    private UserEntity user;

    public void setId(UUID id) {
        this.id = id;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}
