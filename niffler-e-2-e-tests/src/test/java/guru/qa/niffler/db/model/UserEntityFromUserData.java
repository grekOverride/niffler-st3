package guru.qa.niffler.db.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserEntityFromUserData {

    private UUID id;

    private String username;


    private CurrencyValues currency;

    private String firstname;

    private String surname;

    private byte[] photo;

    private List<FriendsEntityFromUserData> friends = new ArrayList<>();

    private List<FriendsEntityFromUserData> invites = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public CurrencyValues getCurrency() {
        return currency;
    }

    public void setCurrency(CurrencyValues currency) {
        this.currency = currency;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public  List<FriendsEntityFromUserData> getFriends() {
        return friends;
    }

    public void setFriends(List<FriendsEntityFromUserData> friends) {
        this.friends = friends;
    }

    public  List<FriendsEntityFromUserData> getInvites() {
        return invites;
    }

    public void setInvites(List<FriendsEntityFromUserData> invites) {
        this.invites = invites;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntityFromUserData that = (UserEntityFromUserData) o;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && currency == that.currency && Objects.equals(firstname, that.firstname) && Objects.equals(surname, that.surname) && Arrays.equals(photo, that.photo) && Objects.equals(friends, that.friends) && Objects.equals(invites, that.invites);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, username, currency, firstname, surname, friends, invites);
        result = 31 * result + Arrays.hashCode(photo);
        return result;
    }
}
