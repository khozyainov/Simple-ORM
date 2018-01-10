package model;

import lombok.*;

import javax.persistence.*;

/**
 * Created by entony on 17.10.2017.
 */

@Entity
@Table(name = "phones")
public class PhoneDataSet extends DataSet{

    @Column(name = "phone")
    private String phone;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserDataSet user;

    public PhoneDataSet(){}

    public PhoneDataSet(String phone){
        this.phone = phone;
    }

    public void setUser(UserDataSet user){
        this.user = user;
    }

    @Override
    public String toString(){
        return "Number: " + phone;
    }

}
