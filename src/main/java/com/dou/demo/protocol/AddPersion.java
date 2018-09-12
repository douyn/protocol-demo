package com.dou.demo.protocol;

import com.example.tutorial.AddressBookProtos;

import java.io.*;

public class AddPersion {
    public static void main(String[] args) throws Exception{

        if (args.length != 1) {
            System.err.println("Usage: AddPerson Address Book File");
            System.exit(-1);
        }

        AddressBookProtos.AddressBook.Builder addressBookBuilder = AddressBookProtos.AddressBook.newBuilder();

        addressBookBuilder.addPeople(promptForAddress(new BufferedReader(new InputStreamReader(System.in)), System.out));

        OutputStream outputStream = new FileOutputStream(args[0]);
        addressBookBuilder.build().writeTo(outputStream);
        outputStream.close();
    }

    public static AddressBookProtos.Person promptForAddress(BufferedReader stdin, PrintStream stdout) throws IOException {

        AddressBookProtos.Person.Builder builder = AddressBookProtos.Person.newBuilder();

        stdout.println("Enter Person ID : ");
        builder.setId(Integer.valueOf(stdin.readLine()));

        stdout.println("Enter Person name : ");
        builder.setName(stdin.readLine());

        stdout.println("Enter Person Email(Blank for none) : ");
        String email = stdin.readLine();
        if (email.length() > 0) {
            builder.setEmail(email);
        }

        while (true) {
            stdout.println("Enter a PhoneNumber( or leave blank to finish)");

            String phoneNumber = stdin.readLine();

            if (phoneNumber.length() == 0)
                break;

            AddressBookProtos.Person.PhoneNumber.Builder phoneNumberBuilder = AddressBookProtos.Person.PhoneNumber.newBuilder();
            phoneNumberBuilder.setNumber(phoneNumber);

            stdout.println("Is this the phone number home, work or mobile(default is home)");
            String phoneTypeString = stdin.readLine();

            switch (phoneTypeString) {
                case "home":
                    phoneNumberBuilder.setType(AddressBookProtos.Person.PhoneType.HOME);
                    break;
                case "work":
                    phoneNumberBuilder.setType(AddressBookProtos.Person.PhoneType.WORK);
                    break;
                case "mobile":
                    phoneNumberBuilder.setType(AddressBookProtos.Person.PhoneType.MOBILE);
                    break;
                default:
                    phoneNumberBuilder.setType(AddressBookProtos.Person.PhoneType.HOME);
                    break;
            }

            builder.addPhones(phoneNumberBuilder.build());
        }

        return builder.build();
    }
}
