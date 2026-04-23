package hospital.management.system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long doctorId;

    @NotBlank(message = "First name is required")
    private String firstName;

    private String lastName;

    @NotBlank(message = "Specialization is required")
    private String specialization;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone must be 10 digits")
    private String phone;

    @Email(message = "Invalid email format")
    private String email;

    private Integer experienceYears;
}