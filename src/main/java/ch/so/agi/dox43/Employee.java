package ch.so.agi.dox43;

import java.math.BigDecimal;
import java.util.Date;

public class Employee {
    private String name;
    private Date birthDate;
    private BigDecimal payment;
    
    public Employee(String name, Date birthDate, BigDecimal payment) {
        this.name = name;
        this.birthDate = birthDate;
        this.payment = payment;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Date getBirthDate() {
        return birthDate;
    }
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    public BigDecimal getPayment() {
        return payment;
    }
    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

}
