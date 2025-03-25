package site.easy.to.build.crm.dtos.importdata;

import lombok.*;
import site.easy.to.build.crm.entity.Customer;
import site.easy.to.build.crm.entity.User;

import java.util.Random;

@Getter
@ToString
@AllArgsConstructor
public class CustomerImport {

    // Obligatoire
    @Setter
    String customerEmail;
    @Setter
    String customerName;

    @Setter
    User user;

    // random
    String country;
    String phoneNumber;

    public void setCountry() {
        this.country = getRandomCountry();
    }

    public void setPhoneNumber() {
        this.phoneNumber = getRandomPhoneNumber();
    }

    private String getRandomCountry() {
        String[] countries = {"USA", "France", "Germany", "Canada", "Japan", "Brazil", "India"};
        return countries[new Random().nextInt(countries.length)];
    }

    private String getRandomPhoneNumber() {
        return "+1" + (1000000000L + new Random().nextInt(900000000));
    }

    public CustomerImport() {
        setCountry();
        setPhoneNumber();
    }


}
