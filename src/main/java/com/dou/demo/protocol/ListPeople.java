package com.dou.demo.protocol;

import com.example.tutorial.AddressBookProtos;

import java.io.FileInputStream;

public class ListPeople {
    public static void main(String[] args) throws Exception{
        if (args.length != 1) {
            System.err.println("Usage: ListPeople ADDRESS_BOOK_FILE");
            System.exit(-1);
        }

        AddressBookProtos.AddressBook addressBook = AddressBookProtos.AddressBook.parseFrom(new FileInputStream(args[0]));
        pinrt(addressBook);
    }

    private static void pinrt(AddressBookProtos.AddressBook addressBook) {
        for (AddressBookProtos.Person person :addressBook.getPeopleList()){
            System.out.println("Person ID: " + person.getId());
            System.out.println("Person Name: " + person.getName());
            System.out.println("Person Email: " + person.getEmail());

            for (AddressBookProtos.Person.PhoneNumber phoneNumber : person.getPhonesList()){
                System.out.println("Person PhoneNumber: " + phoneNumber.getNumber());
                System.out.println("Person PhoneType: " + phoneNumber.getType());
            }
        }
    }
}
