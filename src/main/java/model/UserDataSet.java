package model;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by entony on 17.10.2017.
 */


@Entity
@Table(name = "users")
public class UserDataSet extends DataSet {

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private int age;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = PhoneDataSet.class, mappedBy = "user", orphanRemoval = true)
    private List<PhoneDataSet> phones;

    @OneToOne(cascade = CascadeType.ALL, targetEntity = AddressDataSet.class, mappedBy = "user", orphanRemoval = true)
    private AddressDataSet address;

    public UserDataSet(){}

    public UserDataSet(long id, String name, int age, AddressDataSet address, PhoneDataSet... phones) {
        this.setId(id);
        this.setName(name);
        this.setAge(age);
        this.setAddress(address);
        address.setUser(this);
        this.setPhones(Arrays.asList(phones));
        Arrays.asList(phones).forEach((p) -> p.setUser(this));
    }

    public UserDataSet(String name, int age,AddressDataSet address, PhoneDataSet... phones) {
        this.setId(-1);
        this.setName(name);
        this.setAge(age);
        this.setAddress(address);
        address.setUser(this);
        this.setPhones(Arrays.asList(phones));
        Arrays.asList(phones).forEach((p) -> p.setUser(this));

    }

    public UserDataSet(String name, int age,AddressDataSet address, List<PhoneDataSet> phones) {
        this.setId(-1);
        this.setName(name);
        this.setAge(age);
        this.setAddress(address);
        address.setUser(this);
        this.setPhones(phones);
        phones.forEach((p) -> p.setUser(this));
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public int getAge(){
        return age;
    }

    private void setAge(int age){
        this.age = age;
    }

    public AddressDataSet getAddress() {
        return address;
    }

    private void setAddress(AddressDataSet address) {
        this.address = address;
    }

    public List<PhoneDataSet> getPhones() {
        return phones;
    }

    private void setPhones(List<PhoneDataSet> phones) {
        this.phones = phones;
    }

    @Override
    public String toString() {
        return "UserDataSet{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", address=" + address.toString() +
                ", phones=" + phones.toString() +
                '}';
    }

}
