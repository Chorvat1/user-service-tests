package org.example.dto;

public class UpdateUserDto {

    private String name;
    private String email;
    private int age;

    public UpdateUserDto() {}

    public String getName() { return name; }
    public String getEmail() { return email; }
    public int getAge() { return age; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setAge(int age) { this.age = age; }
}