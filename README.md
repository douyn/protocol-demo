## Protocol Buffer: Java

### Protocol Buffer基础
简单点说procotol buffer就是类似与json这样的事先定义好的格式化语言，用于程序间的数据通信。

Protocol buffer是google开发的，与语言无关的，与平台无关的，可扩展的，可用于序列化结构化数据

通过本教程你将学会:

1. 如何在.proto文件中定义消息格式
2. 如何使用protocol buffer编译器
3. 使用使用protocol buffer-java来编写和读取消息

为什么要使用protocol buffer:

示例是一个简单的地址薄应用，可以在文件中读取和写入人员的详细信息，地址薄中的每个人都有姓名，id，电子邮件地址和联系号码。

之前的处理方式：

1. java序列化，你可以使用Serilizable或者Android中的Parcable来序列化数据。但是这属于java所特有的，无法在其他语言使用
2. 使用xml，xml的缺点在于编解码会对应用程序造成巨大的性能损耗，且复杂格式的xml解析起来比较麻烦
3. 可以将数据保存为特定格式的字符串，再使用特定格式进行解析，只适合简单的数据

Protocol Buffer是灵活，高效，自动化的解决方案，你只需要编写.proto文件存储要保存的数据，然后通过协议缓冲区编译器对这个文件进行解析和自动编码生成一个类，生成的类为.proto文件中的字段提供getter和setter方法，并负责将protocol buffer作为一个单元进行读写的详细信息。并且扩展性极好，可以随时通过修改.proto文件修改数据

### 如何定义Protocol Buffer
#### 定义.proto
.proto 文件中的定义很简单: 为要序列化的每个数据添加Message，然后为消息中每个数据添加字段和类型，下边是示例中的address.proto

	syntax = "proto2";
    
    package tutorial;

	option java_package = "com.example.tutorial";
    option java_outer_classname = "AddressBookProtos";
    
    message Persion {
    	required string name = 1;
        required int32 id = 2;
        optional string email = 3;
        
        enum PhoneTYpe {
        	MOBILE = 0;
            HOME = 1;
            WORK = 2;
        }
        
        message PhoneNumber {
        	required string number = 1;
            optional PhoneType type = 2 [default = HOME]
        }
        
        repeated PhoneNumber phones = 4;
    }	
    
    message AddressBook {
    	repeated Persion people = 1;
    }
    
 如上所示：
 
 文件以package声明开始，防止不同项目之间的命名冲突
 
java_package和java_outer_class用于指定输出的类的包名和类名。如果没有指定java_package，他会匹配上边package给出的包名，如果没有指定java_outer_class，他会自动将.proto文件名转换为类名,例如my_test.proto会被转换为MyTest.java

接下来就是定义message,消息只是包含一组类型字段的聚合。很多基本数据类型都可以作为字段类型，包括bool, int32, float, double和string。

你还可以使用其他的message作为字段类型。例如上边的Persion消息中包含PhoneNumber消息，AddressBook消息包含Persion消息。甚至可以定义嵌套在其他消息里边的消息类型，例如PhoneNumber定义在Persion中。

enum 与java类似，如果你想让一个字段具有预定义的值列表之一，你可以使用这个关键字定义。

必须使用以下的修饰符修饰每个字段：

- required 必须提供该字段的值
- optional 不必须提供该字段的值，如果没有提供，则由系统提供，类似java的字段的定义
- repeated 该字段可能会出现任意次数(包括零)

> 每个元素的"=1","=2"标示该字段在二进制编码中使用的唯一的标志，一般1-15用于常用或者重复的元素，16以上留给不常用的元素

#### 编译.proto
如果你现在已经有.proto文件了，那么下一步就是如何生成对应的java文件。

1. 下载安装编译器

	例如我的环境是ubuntu，就去[下载页面](https://github.com/protocolbuffers/protobuf/releases/tag/v3.6.1)下载最新的包，解压之后把路径添加到path中即可
2. 运行编译器，编译出java文件。指定源目录(如果不提供，则使用当前路径)，目标目录(你希望生成的目录)，以及你的.proto文件path

		proto -I=$SRC_DIR --java_out=$DST_DIR $SRC_DIR/address.proto
        
3. 在项目中使用，包括序列化和反序列化。在IDE中新建项目并添加依赖 $ compile 'com.google.protobuf:protobuf-java:3.6.1', 因为编译出来的java类使用了builder模式，所以大多数的序列化的过程就是创建builder设置值的一个过程，最后可以通过writeTo()或者toByteArray()获的字节流。反序列化一般是通过parseFrom()方法进行的

序列化：

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
    
反序列化：

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

[源码](https://github.com/douyn/protocol-demo)

### 参考
[通信协议只protocol buffer(java篇)](https://blog.csdn.net/qq_39940205/article/details/80050185)



