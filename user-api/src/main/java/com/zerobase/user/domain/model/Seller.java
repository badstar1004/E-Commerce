package com.zerobase.user.domain.model;

import com.zerobase.user.domain.SignUpForm;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@AuditOverride(forClass = BaseEntity.class)
public class Seller extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String name;

    // 암호화 할 수도 있음
    private String password;
    private LocalDate birth;
    private String phone;

    private LocalDateTime verifyExpiredAt;
    private String verificationCode;
    private boolean verify;

    /**
     * Seller builder()
     *
     * @param signUpForm
     * @return
     */
    public static Seller from(SignUpForm signUpForm) {
        return Seller.builder()
            .email(signUpForm.getEmail().toLowerCase(Locale.ROOT))
            .name(signUpForm.getName())
            .password(signUpForm.getPassword())
            .birth(signUpForm.getBirth())
            .phone(signUpForm.getPhone())
            .verify(false)
            .build();
    }



}
