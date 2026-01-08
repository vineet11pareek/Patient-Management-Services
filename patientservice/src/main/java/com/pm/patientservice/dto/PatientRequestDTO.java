package com.pm.patientservice.dto;

import com.pm.patientservice.dto.validation.PatientValidationGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PatientRequestDTO {
    @NotNull(message = "Name is Required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Email is Required")
    @NotBlank(message = "Email not be Blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Address is Required")
    private String address;

    @NotNull(message = "Date of Birth is Required")
    private String dateOfBirth;

    @NotNull(groups = PatientValidationGroup.class,message = "Registered Date is required")
    private String registeredDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getRegisteredDate() {
        return registeredDate;
    }

    public void setRegisteredDate(String registeredDate) {
        this.registeredDate = registeredDate;
    }
}
