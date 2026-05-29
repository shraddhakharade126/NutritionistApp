package com.nutritionist.app;

public class User {
    private int id;
    private String fullName;
    private String email;
    private String password;
    private int age;
    private double weight;
    private double height;
    private String gender;
    private boolean isAdmin;

    public User() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    public double calculateBMI() {
        if (height <= 0) return 0;
        double heightM = height / 100.0;
        return weight / (heightM * heightM);
    }

    public String getBMIStatus() {
        double bmi = calculateBMI();
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 25.0) return "Normal Weight";
        else if (bmi < 30.0) return "Overweight";
        else return "Obese";
    }

    public int getDailyCalories() {
        // Harris-Benedict formula
        double bmr;
        if ("Male".equalsIgnoreCase(gender)) {
            bmr = 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else {
            bmr = 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }
        return (int) (bmr * 1.55); // Moderate activity
    }
}
