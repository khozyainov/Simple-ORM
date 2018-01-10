package model;

import lombok.*;

import javax.persistence.*;

/**
 * Created by entony on 17.10.2017.
 */


@Entity
@Table(name = "addresses")
public class AddressDataSet extends DataSet{

    @NonNull
    @Column(name = "address")
    private String address;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserDataSet user;

    public AddressDataSet(){}

    public AddressDataSet(String address){
        this.address = address;
    }

    public void setUser(UserDataSet user){
        this.user = user;
    }

    @Override
    public String toString(){
        return "Address: " + address;
    }

}
